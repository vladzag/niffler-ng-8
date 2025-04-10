package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.entity.user.AuthUserEntity;
import guru.qa.niffler.data.entity.user.AuthorityEntity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AuthAuthorityDaoJdbc implements AuthAuthorityDao {

    private final Connection connection;

    public AuthAuthorityDaoJdbc(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void createUser(AuthUserEntity authUser) {
        List<AuthorityEntity> created = new ArrayList<>();
        List<AuthorityEntity> userAuthorities = authUser.getPermissions();

        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO \"authority\" (user_id, authority) VALUES (?, ?)",
                PreparedStatement.RETURN_GENERATED_KEYS
        )) {
            for (AuthorityEntity authority : userAuthorities) {
                ps.setObject(1, authority.getUser().getId());
                ps.setString(2, authority.getAuthority().name());
                ps.addBatch();
            }

            ps.executeBatch();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                for (AuthorityEntity authority : userAuthorities) {
                    if (rs.next()) {
                        UUID generatedKey = rs.getObject(1, UUID.class);
                        authority.setId(generatedKey);
                        created.add(authority);
                    } else {
                        throw new SQLException("Can't find user id in ResultSet");
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error executing batch insert", e);
        }
    }
}