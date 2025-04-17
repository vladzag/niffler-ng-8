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
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.templates.DataSources;
import guru.qa.niffler.data.templates.XaTransactionTemplate;
import guru.qa.niffler.model.UserJson;
import org.springframework.data.transaction.ChainedTransactionManager;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Arrays;
import java.util.Optional;

import static guru.qa.niffler.data.templates.DataSources.testDataSource;
import static java.sql.Connection.TRANSACTION_READ_UNCOMMITTED;

public class UsersDbClient {

    private static final Config CFG = Config.getInstance();
    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    private final AuthUserDao authUserDao = new AuthUserDaoSpringJdbc();
    private final AuthAuthorityDao authAuthorityDao = new AuthAuthorityDaoSpringJdbc();
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

            AuthUserEntity createdAuthUser = authUserDao.create(aue);

            AuthorityEntity[] authorityEntities = getAuthorityEntities(createdAuthUser);

            authAuthorityDao.create(authorityEntities);
            return UserJson.fromEntity(userDao.create(UserEntity.fromJson(user)), null);
        });
    }

    public UserJson createUser(UserJson user) {
        return xaTransactionTemplate.execute(() -> {
                    AuthUserEntity authUser = new AuthUserEntity();
                    authUser.setUsername(user.username());
                    authUser.setPassword(pe.encode("12345"));
                    authUser.setEnabled(true);
                    authUser.setAccountNonExpired(true);
                    authUser.setAccountNonLocked(true);
                    authUser.setCredentialsNonExpired(true);


                    AuthUserEntity createdAuthUser = authUserDao.create(authUser);


                    AuthorityEntity[] authorityEntities = Arrays.stream(Authority.values()).map(
                            e -> {
                                AuthorityEntity ae = new AuthorityEntity();
                                ae.setUserId(createdAuthUser.getId());
                                ae.setAuthority(e);
                                return ae;
                            }
                    ).toArray(AuthorityEntity[]::new);

                    authAuthorityDao.create(authorityEntities);
                    return UserJson.fromEntity(
                            userDao.create(UserEntity.fromJson(user)),
                            null
                    );
                }
        );


    }

    public UserJson createUserJdbcWithoutTx(UserJson user) {
        AuthUserEntity aue = new AuthUserEntity();
        aue.setUsername(user.username());
        aue.setPassword(pe.encode("12345"));
        aue.setEnabled(true);
        aue.setAccountNonLocked(true);
        aue.setAccountNonExpired(true);
        aue.setCredentialsNonExpired(true);

        AuthUserEntity createdAuthUser = authUserDao.create(aue);

        AuthorityEntity[] authorityEntities = getAuthorityEntities(createdAuthUser);

        authAuthorityDao.create(authorityEntities);
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
}
