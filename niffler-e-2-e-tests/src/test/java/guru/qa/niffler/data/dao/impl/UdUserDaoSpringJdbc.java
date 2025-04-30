package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.UdUserDao;
import guru.qa.niffler.data.entity.userdata.FriendshipEntity;
import guru.qa.niffler.data.entity.userdata.FriendshipStatus;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.mapper.UdUserEntityRowMapper;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UdUserDaoSpringJdbc implements UdUserDao {

    private static final Config CFG = Config.getInstance();
    private final String url = Config.getInstance().userdataJdbcUrl();

    @Override
    public UserEntity create(UserEntity user) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(url));
        KeyHolder kh = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO \"user\" (username, currency, firstname, surname, photo, photo_small, full_name) " +
                            "VALUES (?,?,?,?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getCurrency().name());
            ps.setString(3, user.getFirstname());
            ps.setString(4, user.getSurname());
            ps.setBytes(5, user.getPhoto());
            ps.setBytes(6, user.getPhotoSmall());
            ps.setString(7, user.getFullname());
            return ps;
        }, kh);

        final UUID generatedKey = (UUID) kh.getKeys().get("id");
        user.setId(generatedKey);
        return user;
    }

    @Override
    public Optional<UserEntity> findById(UUID id) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(url));
        return Optional.ofNullable(
                jdbcTemplate.queryForObject(
                        "SELECT * FROM \"user\" WHERE id = ?",
                        UdUserEntityRowMapper.INSTANCE,
                        id
                )
        );
    }

    @Override
    public Optional<UserEntity> findByUsername(String username) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(url));

        return Optional.ofNullable(
                jdbcTemplate.queryForObject(
                        "SELECT * FROM \"user\" WHERE username = ?",
                        UdUserEntityRowMapper.INSTANCE,
                        username
                )
        );
    }

    @Override
    public List<UserEntity> findAll() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(url));
        return jdbcTemplate.query(
                "SELECT * FROM \"user\"",
                UdUserEntityRowMapper.INSTANCE
        );
    }

    @Override
    public UserEntity update(UserEntity user) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(url));

        jdbcTemplate.update(
                "UPDATE \"user\" SET currency = ?, firstname = ?, surname = ?, photo = ?, photo_small = ?, full_name = ? WHERE id = ?",
                user.getCurrency().name(),
                user.getFirstname(),
                user.getSurname(),
                user.getPhoto(),
                user.getPhotoSmall(),
                user.getId()
        );


        FriendshipEntity[] fRequests = user.getFriendshipRequests().toArray(FriendshipEntity[]::new);
        jdbcTemplate.batchUpdate(
                "INSERT INTO friendship (requester_id, addressee_id, status, created_date) VALUES (?,?,?,?) ON CONFLICT (requester_id, addressee_id) DO UPDATE SET status = ?",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setObject(1, fRequests[i].getRequester().getId());
                        ps.setObject(2, fRequests[i].getAddressee().getId());
                        ps.setString(3, fRequests[i].getStatus().name());
                        ps.setString(4, fRequests[i].getStatus().name());
                    }

                    @Override
                    public int getBatchSize() {
                        return fRequests.length;
                    }
                }
        );

        FriendshipEntity[] fAddressee = user.getFriendshipAddressees().toArray(FriendshipEntity[]::new);
        jdbcTemplate.batchUpdate(
                "INSERT INTO friendship (requester_id, addressee_id, status, created_date) VALUES (?,?,?,?) ON CONFLICT (requester_id, addressee_id) DO UPDATE SET status = ?",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setObject(1, fAddressee[i].getRequester().getId());
                        ps.setObject(2, fAddressee[i].getAddressee().getId());
                        ps.setString(3, fAddressee[i].getStatus().name());
                        ps.setString(4, fAddressee[i].getStatus().name());
                    }

                    @Override
                    public int getBatchSize() {
                        return fAddressee.length;
                    }
                }
        );

        return user;
    }

    @Override
    public void remove(UserEntity user) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(url));

        jdbcTemplate.update(
                "DELETE FROM friendship WHERE requester_id = ? OR addressee_id = ?",
                user.getId()
        );
        jdbcTemplate.update(
                "DELETE FROM \"user\" WHERE id = ?",
                user.getId()
        );
    }

    @Override
    public List<FriendshipEntity> findInvitationByRequesterId(UUID requesterId) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(url));
        return jdbcTemplate.query(
                "SELECT * FROM friendship f " +
                        "LEFT JOIN \"user\" u ON f.requester_id = u.id " +
                        "WHERE f.requester_id = ?",
                new ResultSetExtractor<List<FriendshipEntity>>() {
                    @Override
                    public List<FriendshipEntity> extractData(ResultSet rs) throws SQLException, DataAccessException {
                        List<FriendshipEntity> invitations = new ArrayList<>();
                        while (rs.next()) {
                            FriendshipEntity fe = new FriendshipEntity();
                            UserEntity requester = new UserEntity();
                            requester.setId(requesterId);
                            fe.setRequester(requester);
                            UserEntity addressee = new UserEntity();
                            addressee.setId(rs.getObject("addressee_id", UUID.class));
                            fe.setAddressee(addressee);
                            fe.setStatus(FriendshipStatus.valueOf(rs.getString("status")));
                            fe.setCreatedDate(rs.getTimestamp("created_date"));
                            invitations.add(fe);
                        }
                        return invitations;
                    }
                },
                requesterId
        );
    }
}
