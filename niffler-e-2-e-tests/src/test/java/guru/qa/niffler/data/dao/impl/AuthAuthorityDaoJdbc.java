package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import static guru.qa.niffler.data.templates.Connections.holder;

import java.sql.PreparedStatement;
import java.sql.SQLException;


public class AuthAuthorityDaoJdbc implements AuthAuthorityDao {

private static final Config CFG = Config.getInstance();


  @Override
  public void create(AuthorityEntity... authority) {
    try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
        "INSERT INTO \"authority\" (user_id, authority) VALUES (?, ?)",
        PreparedStatement.RETURN_GENERATED_KEYS)) {
      for (AuthorityEntity a : authority) {
        ps.setObject(1, a.getUserId());
        ps.setString(2, a.getAuthority().name());
        ps.addBatch();
        ps.clearParameters();
      }
      ps.executeBatch();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
