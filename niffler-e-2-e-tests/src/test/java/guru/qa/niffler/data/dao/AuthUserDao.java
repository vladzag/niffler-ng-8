package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.user.AuthUserEntity;

public interface AuthUserDao {

    AuthUserEntity сreateUser(AuthUserEntity user);
}