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
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.data.repository.UDUserRepository;
import guru.qa.niffler.data.repository.impl.AuthUserRepositoryHibernate;
import guru.qa.niffler.data.repository.impl.UDUserRepositoryHibernate;
import guru.qa.niffler.data.templates.DataSources;
import guru.qa.niffler.data.templates.XaTransactionTemplate;
import guru.qa.niffler.grpc.CurrencyValues;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.utils.RandomDataUtils;
import org.springframework.data.transaction.ChainedTransactionManager;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.templates.DataSources.testDataSource;

public class UsersDbClient {

    private static final Config CFG = Config.getInstance();
    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    private final AuthUserRepository authUserRepository = new AuthUserRepositoryHibernate();
    private final UDUserRepository udUserRepository = new UDUserRepositoryHibernate();

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

    public UserJson createUserJdbcWithTx(UserJson user) {
        return xaTransactionTemplate.execute(() -> {
            AuthUserEntity aue = new AuthUserEntity();
            aue.setUsername(user.username());
            aue.setPassword(pe.encode("12345"));
            aue.setEnabled(true);
            aue.setAccountNonLocked(true);
            aue.setAccountNonExpired(true);
            aue.setCredentialsNonExpired(true);

            AuthUserEntity createdAuthUser = authUserRepository.create(aue);

            AuthorityEntity[] authorityEntities = getAuthorityEntities(createdAuthUser);

            authUserRepository.create(createdAuthUser);
            return UserJson.fromEntity(userDao.create(UserEntity.fromJson(user)), null);
        });
    }

    public UserJson createUser(String username, String password) {
        return xaTransactionTemplate.execute(() -> {
                    AuthUserEntity authUser = authUserEntity(username, password);
                    authUserRepository.create(authUser);
                    return UserJson.fromEntity(
                            udUserRepository.create(userEntity(username)), null
                    );
                }
        );
    }

    private UserEntity userEntity(String username) {
        UserEntity ue = new UserEntity();
        ue.setUsername(username);
        ue.setCurrency(CurrencyValues.RUB);
        return ue;
    }

    public void addIncomeInvitation(UserJson targetUser, int count) {
        if (count > 0) {
            UserEntity targetEntity = udUserRepository.findById(
                    targetUser.id()
            ).orElseThrow();

            for (int i = 0; i < count; i++) {
                xaTransactionTemplate.execute(() -> {
                    String username = RandomDataUtils.randomUsername();
                    AuthUserEntity authUser = authUserEntity(username, "12345");
                    authUserRepository.create(authUser);
                    UserEntity addressee = udUserRepository.create(userEntity(username));

                    udUserRepository.addIncomeInvitation(targetEntity, addressee);
                    return null;
                });
            }
        }
    }

    public void addOutcomeInvitation(UserJson targetUser, int count) {
        if (count > 0) {
            UserEntity targetEntity = udUserRepository.findById(
                    targetUser.id()
            ).orElseThrow();

            for (int i = 0; i < count; i++) {
                xaTransactionTemplate.execute(() -> {
                    String username = RandomDataUtils.randomUsername();
                    AuthUserEntity authUser = authUserEntity(username, "12345");
                    authUserRepository.create(authUser);
                    UserEntity addressee = udUserRepository.create(userEntity(username));

                    udUserRepository.addOutcomeInvitation(targetEntity, addressee);
                    return null;
                });
            }
        }
    }

    public void addFriend(UserJson targetUser, int count) {

    }

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

    public UserJson createUserJdbcWithoutTx(UserJson user) {
        AuthUserEntity aue = new AuthUserEntity();
        aue.setUsername(user.username());
        aue.setPassword(pe.encode("12345"));
        aue.setEnabled(true);
        aue.setAccountNonLocked(true);
        aue.setAccountNonExpired(true);
        aue.setCredentialsNonExpired(true);

        AuthUserEntity createdAuthUser = authUserRepository.create(aue);

        AuthorityEntity[] authorityEntities = getAuthorityEntities(createdAuthUser);

        authUserRepository.create(createdAuthUser);
        return UserJson.fromEntity(userDao.create(UserEntity.fromJson(user)), null);
    }

    public UserJson createUserSpringJdbcWithoutTx(UserJson user) {
        AuthUserEntity aue = new AuthUserEntity();
        aue.setUsername(user.username());
        aue.setPassword(pe.encode("12345"));
        aue.setEnabled(true);
        aue.setAccountNonLocked(true);
        aue.setAccountNonExpired(true);
        aue.setCredentialsNonExpired(true);

        AuthUserEntity createdAuthUser = authUserSpringDao.create(aue);

        AuthorityEntity[] authorityEntities = getAuthorityEntities(createdAuthUser);

        authAuthoritySpringDao.create(authorityEntities);
        return UserJson.fromEntity(userSpringDao.create(UserEntity.fromJson(user)), null);

    }

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

    private static AuthorityEntity[] getAuthorityEntities(AuthUserEntity createdAuthUser) {
        AuthorityEntity[] authorityEntities = Arrays.stream(Authority.values()).map(
                e -> {
                    AuthorityEntity ae = new AuthorityEntity();
                    ae.setUserId(createdAuthUser.getId());
                    ae.setAuthority(e);
                    return ae;
                }
        ).toArray(AuthorityEntity[]::new);
        return authorityEntities;
    }

    public Optional<UserEntity> findUserByUsername(String username) {
        return userSpringDao.findByUsername(username);

    }

    public Optional<UserEntity> findUserByID(UUID id) {
        return udUserRepository.findById(id);
    }

    public void addIncomeInvitation(UUID requesterUUID, UUID addresseeUUID) {
        UserEntity requester = new UserEntity();
        requester.setId(requesterUUID);
        UserEntity addressee = new UserEntity();
        addressee.setId(addresseeUUID);

        udUserRepository.addIncomeInvitation(requester, addressee);
    }

    public void addFriend(UUID requesterUUID, UUID addresseeUUID) {
        UserEntity requester = new UserEntity();
        requester.setId(requesterUUID);
        UserEntity addressee = new UserEntity();
        addressee.setId(addresseeUUID);

        udUserRepository.addFriend(requester, addressee);
    }

    //метод для проверки запросов на дружбу
    public List<FriendshipEntity> getFriendshipRequestsByUserID(UUID requesterUUID, UUID addresseeUUID) {
        UserEntity requester = new UserEntity();
        requester.setId(requesterUUID);
        UserEntity addressee = new UserEntity();
        addressee.setId(addresseeUUID);

        return udUserRepository.getFriendshipRequestsByUsersID(requester, addressee);
    }
}
