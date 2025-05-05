package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static guru.qa.niffler.data.jdbc.Connections.holder;


public class AuthAuthorityDaoJdbc implements AuthAuthorityDao {

    private static final Config CFG = Config.getInstance();
    private final String url = Config.getInstance().authJdbcUrl();


    @Override
    public void create(AuthorityEntity... authority) {
        try (PreparedStatement ps = holder(url).connection().prepareStatement(
                "INSERT INTO \"authority\" (user_id, authority) VALUES (?, ?)")) {
            for (AuthorityEntity a : authority) {
                ps.setObject(1, a.getUser().getId());
                ps.setString(2, a.getAuthority().name());
                ps.addBatch();
                ps.clearParameters();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<AuthorityEntity> update(AuthorityEntity... authority) {
        try (PreparedStatement ps = holder(url).connection().prepareStatement(
                "UPDATE \"authority\" SET user_id = ?, authority = ? WHERE id = ?")) {
            for (AuthorityEntity a : authority) {
                ps.setObject(1, a.getUser().getId());
                ps.setString(2, a.getAuthority().name());
                ps.setObject(3, a.getId());
                ps.addBatch();
                ps.clearParameters();
            }
            ps.executeBatch();

            return Arrays.stream(authority).toList();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<AuthorityEntity> findAll() {
        try (PreparedStatement ps = holder(url).connection().prepareStatement(
                "SELECT * FROM \"authority\"")) {
            ps.execute();
            List<AuthorityEntity> result = new ArrayList<>();
            try (ResultSet rs = ps.getResultSet()) {
                while (rs.next()) {
                    AuthorityEntity ae = new AuthorityEntity();
                    ae.setId(rs.getObject("id", UUID.class));
                    AuthUserEntity ue = new AuthUserEntity();
                    ue.setId(rs.getObject("user_id", UUID.class));
                    ae.setUser(ue);
                    ae.setAuthority(Authority.valueOf(rs.getString("authority")));
                    result.add(ae);
                }
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void remove(AuthorityEntity... authority) {
        try (PreparedStatement ps = holder(url).connection().prepareStatement(
                "DELETE FROM \"authority\" WHERE user_id = ?")) {
            for (AuthorityEntity a : authority) {
                ps.setObject(1, a.getUser().getId());
                ps.addBatch();
                ps.clearParameters();
            }
            ps.executeBatch();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
