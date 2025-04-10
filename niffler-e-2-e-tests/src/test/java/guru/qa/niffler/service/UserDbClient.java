package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.impl.AuthAuthorityDaoJdbc;
import guru.qa.niffler.data.dao.impl.AuthUserDaoJdbc;
import guru.qa.niffler.data.dao.impl.UserDaoJdbc;
import guru.qa.niffler.data.entity.user.AuthUserEntity;
import guru.qa.niffler.data.entity.user.UserEntity;
import guru.qa.niffler.model.UserJson;

import java.util.Optional;

import static guru.qa.niffler.data.Databases.*;
import static java.sql.Connection.TRANSACTION_READ_COMMITTED;

public class UserDbClient {


    private static final Config CFG = Config.getInstance();

    public UserJson createUser(UserJson userJson) {
        XaFunction<UserJson> xaAuthF = new XaFunction<>(
                connection -> {
                    AuthUserEntity authUserEntity = new AuthUserDaoJdbc(connection).createPermission(AuthUserEntity.fromJson(userJson));
                    new AuthAuthorityDaoJdbc(connection).createUser(authUserEntity);
                    return UserJson.fromAuthEntity(authUserEntity);
                },
                CFG.authJdbcUrl());

        XaFunction<UserJson> xaUserDataF = new XaFunction<>(connection -> {
            UserEntity ue = new UserDaoJdbc(connection).createUser(UserEntity.fromJson(userJson));
            return UserJson.fromEntity(ue);
        },
                CFG.userdataJdbcUrl());

        return xaTransaction(TRANSACTION_READ_COMMITTED, xaAuthF, xaUserDataF);
    }

    public Optional<UserEntity> findUserByUsername(String username) {
        return transaction(TRANSACTION_READ_COMMITTED, connection -> {
                    return new UserDaoJdbc(connection)
                            .findUserByUsername(username);
                },
                CFG.userdataJdbcUrl()
        );
    }
}
