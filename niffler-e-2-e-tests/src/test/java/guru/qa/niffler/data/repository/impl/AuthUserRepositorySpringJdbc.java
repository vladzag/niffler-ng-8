package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.data.templates.DataSources;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class AuthUserRepositorySpringJdbc implements AuthUserRepository {

    private static final Config CFG = Config.getInstance();

    @Override
    public AuthUserEntity create(AuthUserEntity user) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));

        KeyHolder kh = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO \"user\" (username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired) " +
                            "VALUES (?,?,?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setBoolean(3, user.getEnabled());
            ps.setBoolean(4, user.getAccountNonExpired());
            ps.setBoolean(5, user.getAccountNonLocked());
            ps.setBoolean(6, user.getCredentialsNonExpired());
            return ps;
        }, kh);

        final UUID generatedKey = (UUID) kh.getKeys().get("id");
        user.setId(generatedKey);

        List<AuthorityEntity> authorities = user.getAuthorities();

        jdbcTemplate.batchUpdate(
                "INSERT INTO authority (user_id, authority) VALUES (? , ?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setObject(1, authorities.get(i).getUser().getId());
                        ps.setString(2, authorities.get(i).getAuthority().name());
                    }

                    @Override
                    public int getBatchSize() {
                        return authorities.size();
                    }
                }
        );

        return user;
    }

    @Override
    public Optional<AuthUserEntity> findById(UUID id) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));
        return Optional.ofNullable(
                jdbcTemplate.query(
                        "SELECT a.id as authority_id, authority, user_id as id, u.username, u.password, u.enabled, u.account_non_expired, u.account_non_locked, u.credentials_non_expired FROM \"user\" u join public.authority a on u.id = a.user_id WHERE u.id = ?",
                        new ResultSetExtractor<AuthUserEntity>() {
                            @Override
                            public AuthUserEntity extractData(ResultSet rs) throws SQLException, DataAccessException {
                                Map<UUID, AuthUserEntity> userMap = new HashMap<>();
                                UUID userId = null;

                                while (rs.next()) {
                                    userId = rs.getObject("id", UUID.class);

                                    AuthUserEntity user = userMap.computeIfAbsent(userId, id -> {
                                        AuthUserEntity userEntity = new AuthUserEntity();
                                        userEntity.setId(id);
                                        try {
                                            userEntity.setUsername(rs.getString("username"));
                                            userEntity.setPassword(rs.getString("password"));
                                            userEntity.setEnabled(rs.getBoolean("enabled"));
                                            userEntity.setAccountNonExpired(rs.getBoolean("account_non_expired"));
                                            userEntity.setAccountNonLocked(rs.getBoolean("account_non_locked"));
                                            userEntity.setCredentialsNonExpired(rs.getBoolean("credentials_non_expired"));
                                        } catch (SQLException e) {
                                            throw new RuntimeException(e);
                                        }
                                        return userEntity;
                                    });

                                    AuthorityEntity authority = new AuthorityEntity();
                                    authority.setId(rs.getObject("authority_id", UUID.class));
                                    authority.setAuthority(Authority.valueOf(rs.getString("authority")));
                                    user.getAuthorities().add(authority);
                                }
                                return userMap.get(userId);
                            }
                        },
                        id
                )
        );

    }

}