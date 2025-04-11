package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.entity.user.AuthUserEntity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class AuthUserDaoJdbc implements AuthUserDao {
    private static final PasswordEncoder ENCODER = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    private final Connection connection;

    public AuthUserDaoJdbc(Connection connection) {
        this.connection = connection;
    }

    @Override
    public AuthUserEntity сreateUser(AuthUserEntity user) {
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO \"user\" (username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired) " +
                        "VALUES (?, ?, ?, ?, ?, ?)",
                PreparedStatement.RETURN_GENERATED_KEYS
        )) {
            ps.setString(1, user.getUsername());
            ps.setString(2, ENCODER.encode(user.getPassword()));
            ps.setBoolean(3, user.getEnabled());
            ps.setBoolean(4, user.getAccountNonExpired());
            ps.setBoolean(5, user.getAccountNonLocked());
            ps.setBoolean(6, user.getCredentialsNonExpired());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    UUID generatedKey = rs.getObject("id", UUID.class);
                    user.setId(generatedKey);
                } else {
                    throw new SQLException("Can't find user id in ResultSet");
                }
            }

            return user;

        } catch (SQLException e) {
            throw new RuntimeException("Error creating user", e);
        }
    }
}