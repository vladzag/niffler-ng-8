package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.auth.AuthUserEntity;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

public interface AuthUserDao {

  AuthUserEntity create(AuthUserEntity user);

  Optional<AuthUserEntity> findById(UUID id);

  AuthUserEntity update(AuthUserEntity user);

  Optional<AuthUserEntity> findByUsername(String username);

  List<AuthUserEntity> findAll();

  void remove(AuthUserEntity user);
}
