package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.user.AuthUserEntity;

public interface AuthAuthorityDao {

    void createPermission(AuthUserEntity user);
}