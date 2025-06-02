package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.dao.UdUserDao;
import guru.qa.niffler.data.dao.impl.AuthAuthorityDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.AuthUserDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.UdUserDaoJdbc;
import guru.qa.niffler.data.dao.impl.UdUserDaoSpringJdbc;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.userdata.FriendshipEntity;
import guru.qa.niffler.data.entity.userdata.FriendshipStatus;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.jdbc.DataSources;
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.data.repository.UserDataUserRepository;
import guru.qa.niffler.data.repository.impl.AuthUserRepositoryHibernate;
import guru.qa.niffler.data.repository.impl.UserDataUserRepositoryHibernate;
import guru.qa.niffler.data.templates.XaTransactionTemplate;
import guru.qa.niffler.grpc.CurrencyValues;
import guru.qa.niffler.model.TestData;
import guru.qa.niffler.model.UserJson;
import io.qameta.allure.Step;
import org.springframework.data.transaction.ChainedTransactionManager;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.jdbc.DataSources.testDataSource;
import static guru.qa.niffler.utils.RandomDataUtils.randomUsername;

public class UsersDbClient implements UsersClient {


    private static final Config CFG = Config.getInstance();
    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    private static final String USER_PW = "12345";

    private final AuthUserRepository authUserRepository = new AuthUserRepositoryHibernate(); //TODO: Hibernate
    private final UserDataUserRepository udUserRepository = new UserDataUserRepositoryHibernate(); //TODO: Hibernate

//    private final AuthUserRepository authUserRepository = new AuthUserRepositoryJdbc(); //TODO: JDBC
//    private final UserDataUserRepository udUserRepository = new UserDataUserRepositoryJdbc(); //TODO: JDBC

//    private final AuthUserRepository authUserRepository = new AuthUserRepositorySpringJdbc(); //TODO: Spring
//    private final UserDataUserRepository udUserRepository = new UserDataUserRepositorySpringJdbc(); //TODO: Spring

    private final UdUserDao userDao = new UdUserDaoJdbc();

    private final AuthUserDao authUserSpringDao = new AuthUserDaoSpringJdbc();
    private final AuthAuthorityDao authAuthoritySpringDao = new AuthAuthorityDaoSpringJdbc();
    private final UdUserDao userSpringDao = new UdUserDaoSpringJdbc();

    private final TransactionTemplate txTemplate = new TransactionTemplate(
            new JdbcTransactionManager(
                    DataSources.dataSource(CFG.authJdbcUrl())
            )
    );


    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
            CFG.authJdbcUrl(),
            CFG.userdataJdbcUrl()
    );

    TransactionTemplate txTemplateWithChainedTxManager = new TransactionTemplate(
            new ChainedTransactionManager(
                    new JdbcTransactionManager(
                            testDataSource(CFG.authJdbcUrl())
                    ),
                    new JdbcTransactionManager(
                            testDataSource(CFG.userdataJdbcUrl())
                    )
            )
    );

//    public UserJson createUserJdbcWithTx(UserJson user) {
//        return xaTransactionTemplate.execute(() -> {
//            AuthUserEntity aue = new AuthUserEntity();
//            aue.setUsername(user.username());
//            aue.setPassword(pe.encode("12345"));
//            aue.setEnabled(true);
//            aue.setAccountNonLocked(true);
//            aue.setAccountNonExpired(true);
//            aue.setCredentialsNonExpired(true);
//
//            AuthUserEntity createdAuthUser = authUserRepository.create(aue);
//
//            AuthorityEntity[] authorityEntities = getAuthorityEntities(createdAuthUser);
//
//            authUserRepository.create(createdAuthUser);
//            return UserJson.fromEntity(userDao.create(UserEntity.fromJson(user)), null);
//        });
//    }

    @Step("Создать пользователя с использованием SQL запроса")
    public UserJson createUser(String username, String password) {
        return xaTransactionTemplate.execute(() -> {
                    AuthUserEntity authUser = authUserEntity(username, password);
                    authUserRepository.create(authUser);
                    return UserJson.fromEntity(
                            udUserRepository.create(userEntity(username)), null
                    ).withTestData(new TestData(password));
                }
        );
    }
    @Step("Найти пользователя по имени пользователя")
    public Optional<UserJson> findByUsername(String username) {
        Optional<UserEntity> ue = udUserRepository.findByUsername(username);
        return ue.map(userEntity -> UserJson.fromEntity(userEntity, null));
    }
    @Step("Найти пользователя по id")
    public Optional<UserJson> findById(UUID id) {
        Optional<UserEntity> ue = udUserRepository.findById(id);
        return ue.map(userEntity -> UserJson.fromEntity(userEntity, null));
    }
    @Step("Отправить запрос дружбы")
    public void sendInvitation(UserJson user) {
        xaTransactionTemplate.execute(() -> {
            UserEntity requester = udUserRepository.findById(user.id()).orElseThrow();

            String username = randomUsername();
            AuthUserEntity aue = authUserEntity(username, USER_PW);
            authUserRepository.create(aue);
            UserEntity ue = udUserRepository.create(userEntity(username));

            udUserRepository.sendInvitation(requester, ue);
            return null;
        });
    }
    @Step("Обновить информацию о пользователе")
    public UserJson updateUserInfo(UserJson user) {
        return xaTransactionTemplate.execute(() -> {
                    UserEntity ueToUpdate = UserEntity.fromJson(user);

                    String username = randomUsername();
                    AuthUserEntity aue = authUserEntity(username, USER_PW);
                    authUserRepository.create(aue);
                    UserEntity ue = udUserRepository.create(userEntity(username));

                    switch (user.friendshipStatus()) {
                        case INVITE_SENT -> { //ueToUpdate - requester send invite
                            ueToUpdate.addFriends(FriendshipStatus.PENDING, ue);
                            ue.addInvitations(ueToUpdate);
                        }
                        case INVITE_RECEIVED -> {//ueToUpdate - addressee gets friend request
                            ueToUpdate.addInvitations(ue);
                            ue.addFriends(FriendshipStatus.PENDING, ueToUpdate);
                        }
                        case FRIEND -> {
                            ueToUpdate.addFriends(FriendshipStatus.ACCEPTED, ue);
                            ue.addFriends(FriendshipStatus.ACCEPTED, ueToUpdate);
                        }
                    }
                    UserEntity updated = udUserRepository.update(ueToUpdate);
                    udUserRepository.update(ue);

                    return UserJson.fromEntity(updated,
                            user.friendshipStatus());
                }
        );
    }
    @Step("Установить дружбу между пользователями")
    public void addFriend(UserJson requester, UserJson addressee) {
        UserEntity sender = udUserRepository.findById(requester.id()).orElseThrow();
        UserEntity receiver = udUserRepository.findById(addressee.id()).orElseThrow();
        udUserRepository.addFriend(sender, receiver);
    }
    @Step("Удалить пользователя")
    public void deleteUser(UserJson user) {
        xaTransactionTemplate.execute(() -> {
            UserEntity ueToDelete = udUserRepository.findById(
                    user.id()
            ).orElseThrow();
            AuthUserEntity aueToDelete = authUserRepository.findByUsername(
                    user.username()
            ).orElseThrow();

            authUserRepository.remove(aueToDelete);
            udUserRepository.remove(ueToDelete);
            return null;
        });
    }
    @Step("Найти запросы дружбы по id")
    public List<FriendshipEntity> findInvitationByRequesterId(UUID id) {
        return udUserRepository.findInvitationByRequesterId(id);
    }
    @Step("Найти сущность пользователя {username}")
    private UserEntity userEntity(String username) {
        UserEntity ue = new UserEntity();
        ue.setUsername(username);
        ue.setCurrency(CurrencyValues.RUB);
        return ue;
    }
    @Step("Создать входящие запросы дружбы у пользователя {targetUser} в количестве {count}")
    public void createIncomeInvitations(UserJson targetUser, int count) {
        if (count > 0) {
            UserEntity targetEntity = udUserRepository.findById(
                    targetUser.id()
            ).orElseThrow();

            for (int i = 0; i < count; i++) {
                targetUser.testData()
                        .incomeInvitations()
                        .add(UserJson.fromEntity(
                                        xaTransactionTemplate.execute(() -> {
                                                    final String username = randomUsername();
                                                    final UserEntity newUser = createRandomUser();
                                                    udUserRepository.sendInvitation(
                                                            newUser,
                                                            targetEntity
                                                    );
                                                    return newUser;
                                                }
                                        ),
                                        guru.qa.niffler.model.FriendshipStatus.INVITE_RECEIVED
                                )
                        );
            }
        }
    }
    @Step("Создать случайного пользователя")
    private UserEntity createRandomUser() {
        String username = randomUsername();
        AuthUserEntity authUser = authUserEntity(username, USER_PW);
        authUserRepository.create(authUser);
        UserEntity addressee = udUserRepository.create(userEntity(username));
        return addressee;
    }
    @Step("Создать исходящие запросы дружбы у пользователя {targetUser} в количестве {count}")
    public void createOutcomeInvitations(UserJson targetUser, int count) {
        if (count > 0) {
            UserEntity targetEntity = udUserRepository.findById(
                    targetUser.id()
            ).orElseThrow();

            for (int i = 0; i < count; i++) {
                targetUser.testData()
                        .outcomeInvitations()
                        .add(UserJson.fromEntity(
                                        xaTransactionTemplate.execute(() -> {
                                                    final String username = randomUsername();
                                                    final UserEntity newUser = createRandomUser();
                                                    udUserRepository.sendInvitation(
                                                            targetEntity,
                                                            newUser
                                                    );
                                                    return newUser;
                                                }
                                        ),
                                        guru.qa.niffler.model.FriendshipStatus.INVITE_SENT
                                )
                        );
            }
        }
    }
    @Step("Создать друзей для {targetUser} в количестве {count}")
    public void createFriends(UserJson targetUser, int count) {
        if (count > 0) {
            UserEntity targetEntity = udUserRepository.findById(
                    targetUser.id()
            ).orElseThrow();

            for (int i = 0; i < count; i++) {
                targetUser.testData()
                        .friends()
                        .add(UserJson.fromEntity(
                                        xaTransactionTemplate.execute(() -> {
                                                    final String username = randomUsername();
                                                    final UserEntity newUser = createRandomUser();
                                                    udUserRepository.addFriend(
                                                            targetEntity,
                                                            newUser
                                                    );
                                                    return newUser;
                                                }
                                        ),
                                        guru.qa.niffler.model.FriendshipStatus.FRIEND
                                )
                        );
            }
        }
    }
    @Step("Создать сущность авторизованного пользователя")
    private AuthUserEntity authUserEntity(String username, String password) {
        AuthUserEntity authUser = new AuthUserEntity();
        authUser.setUsername(username);
        authUser.setPassword(pe.encode(password));
        authUser.setEnabled(true);
        authUser.setAccountNonExpired(true);
        authUser.setAccountNonLocked(true);
        authUser.setCredentialsNonExpired(true);
        authUser.setAuthorities(
                Arrays.stream(Authority.values()).map(
                        e -> {
                            AuthorityEntity ae = new AuthorityEntity();
                            ae.setUser(authUser);
                            ae.setAuthority(e);
                            return ae;
                        }
                ).toList()
        );
        return authUser;
    }

    @Step("Создать нового пользователя")
    public UserJson create(UserJson userJson) {
        return txTemplateWithChainedTxManager.execute(status -> {
                    AuthUserEntity aue = new AuthUserEntity();
                    aue.setUsername(userJson.username());
                    aue.setPassword(pe.encode("12345"));
                    aue.setEnabled(true);
                    aue.setAccountNonLocked(true);
                    aue.setAccountNonExpired(true);
                    aue.setCredentialsNonExpired(true);
                    //1 - создаем запись в табл user, niffler-auth
                    AuthUserEntity createdAuthUser = authUserSpringDao.create(aue);

                    //2 - создаем 2 записи read и write в табл authorities, niffler-auth
                    AuthorityEntity[] authorityEntities = getAuthorityEntities(createdAuthUser);
                    authAuthoritySpringDao.create(authorityEntities);

                    //3- создаем запись в табл user, niffler-userdata
                    UserEntity ue = userSpringDao.create(UserEntity.fromJson(userJson));
                    return UserJson.fromEntity(ue, null);
                }
        );
    }

    @Step("Получить сущности авторизации")
    private static AuthorityEntity[] getAuthorityEntities(AuthUserEntity createdAuthUser) {
        AuthorityEntity[] authorityEntities = Arrays.stream(Authority.values()).map(
                e -> {
                    AuthorityEntity ae = new AuthorityEntity();
//                    ae.setUserId(createdAuthUser.getId());
                    ae.setAuthority(e);
                    return ae;
                }
        ).toArray(AuthorityEntity[]::new);
        return authorityEntities;
    }

    @Step("Поиск пользователя по его имени {username}")
    public Optional<UserEntity> findUserByUsername(String username) {
        return userSpringDao.findByUsername(username);

    }

    @Step("Поиск пользователя по его {id}")
    public Optional<UserEntity> findUserByID(UUID id) {
        return udUserRepository.findById(id);
    }

    @Step("Добавить друга")
    public void addFriend(UUID requesterUUID, UUID addresseeUUID) {
        UserEntity requester = new UserEntity();
        requester.setId(requesterUUID);
        UserEntity addressee = new UserEntity();
        addressee.setId(addresseeUUID);

        udUserRepository.addFriend(requester, addressee);
    }
}