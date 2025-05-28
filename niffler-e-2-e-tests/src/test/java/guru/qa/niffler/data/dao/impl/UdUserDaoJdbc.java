package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.UdUserDao;
import guru.qa.niffler.data.entity.userdata.FriendshipEntity;
import guru.qa.niffler.data.entity.userdata.FriendshipStatus;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.grpc.CurrencyValues;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.jdbc.Connections.holder;


public class UdUserDaoJdbc implements UdUserDao {

    private static final Config CFG = Config.getInstance();
    private final String url = Config.getInstance().userdataJdbcUrl();


    @Override
    @NotNull
    public UserEntity create(@NotNull UserEntity user) {
        try (PreparedStatement ps = holder(url).connection().prepareStatement(
                "INSERT INTO \"user\" (username, currency, firstname, surname, photo, photo_small, full_name) " +
                        "VALUES (?,?,?,?,?,?,?)",
                PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getCurrency().name());
            ps.setString(3, user.getFirstname());
            ps.setString(4, user.getSurname());
            ps.setBytes(5, user.getPhoto());
            ps.setBytes(6, user.getPhotoSmall());
            ps.setString(7, user.getFullname());
            ps.executeUpdate();
            final UUID generatedUserId;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedUserId = rs.getObject("id", UUID.class);
                } else {
                    throw new IllegalStateException("Can`t find id in ResultSet");
                }
            }
            user.setId(generatedUserId);
            return user;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @NotNull
    public UserEntity update(@NotNull UserEntity user) {
        try (PreparedStatement userPs = holder(url).connection().prepareStatement(
                "UPDATE \"user\" SET username = ?, currency = ?, firstname = ?, surname = ?, photo = ?, photo_small = ?, full_name = ? WHERE id = ?");
             PreparedStatement friendshipPs = holder(url).connection().prepareStatement(
                     "INSERT INTO friendship (requester_id, addressee_id, status) VALUES (?, ?, ?) ON CONFLICT (requester_id, addressee_id) DO UPDATE SET status = ?"
             )

        ) {
            userPs.setString(1, user.getUsername());
            userPs.setString(2, user.getCurrency().name());
            userPs.setString(3, user.getFirstname());
            userPs.setString(4, user.getSurname());
            userPs.setBytes(5, user.getPhoto());
            userPs.setBytes(6, user.getPhotoSmall());
            userPs.setString(7, user.getFullname());
            userPs.setObject(8, user.getId());
            userPs.executeUpdate();

            for (FriendshipEntity f : user.getFriendshipRequests()) {
                friendshipPs.setObject(1, f.getRequester().getId());
                friendshipPs.setObject(2, f.getAddressee().getId());
                friendshipPs.setString(3, f.getStatus().name());
                friendshipPs.setString(4, f.getStatus().name());
                friendshipPs.addBatch();
                friendshipPs.clearParameters();
            }

            for (FriendshipEntity f : user.getFriendshipAddressees()) {
                friendshipPs.setObject(1, f.getRequester().getId());
                friendshipPs.setObject(2, f.getAddressee().getId());
                friendshipPs.setString(3, f.getStatus().name());
                friendshipPs.setString(4, f.getStatus().name());
                friendshipPs.addBatch();
                friendshipPs.clearParameters();
            }
            friendshipPs.executeBatch();

            return user;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @NotNull
    public Optional<UserEntity> findById(@NotNull UUID id) {
        try (PreparedStatement ps = holder(url).connection().prepareStatement(
                "SELECT * FROM \"user\" WHERE id = ? "
        )) {
            ps.setObject(1, id);

            ps.execute();
            ResultSet rs = ps.getResultSet();

            if (rs.next()) {
                UserEntity result = new UserEntity();
                result.setId(rs.getObject("id", UUID.class));
                result.setUsername(rs.getString("username"));
                result.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
                result.setFirstname(rs.getString("firstname"));
                result.setSurname(rs.getString("surname"));
                result.setPhoto(rs.getBytes("photo"));
                result.setPhotoSmall(rs.getBytes("photo_small"));
                return Optional.of(result);
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @NotNull
    public Optional<UserEntity> findByUsername(@NotNull String username) {
        try (PreparedStatement ps = holder(url).connection().prepareStatement(
                "SELECT * FROM \"user\" WHERE username = ?"
        )) {
            ps.setString(1, username);
            ps.execute();

            try (ResultSet rs = ps.getResultSet()) {
                if (rs.next()) {
                    UserEntity ue = new UserEntity();
                    ue.setId(rs.getObject("id", UUID.class));
                    ue.setUsername(rs.getString("username"));
                    ue.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
                    ue.setFirstname(rs.getString("firstname"));
                    ue.setSurname(rs.getString("surname"));
                    ue.setPhoto(rs.getBytes("photo"));
                    ue.setPhotoSmall(rs.getBytes("photo_small"));
                    ue.setFullname(rs.getString("full_name"));

                    return Optional.of(ue);
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @NotNull
    public List<UserEntity> findAll() {
        try (PreparedStatement ps = holder(url).connection().prepareStatement(
                "SELECT * FROM \"user\"")) {
            ps.execute();
            List<UserEntity> result = new ArrayList<>();
            try (ResultSet rs = ps.getResultSet()) {
                while (rs.next()) {
                    UserEntity ue = new UserEntity();
                    ue.setId(rs.getObject("id", UUID.class));
                    ue.setUsername(rs.getString("username"));
                    ue.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
                    ue.setFirstname(rs.getString("firstname"));
                    ue.setSurname(rs.getString("surname"));
                    ue.setFullname(rs.getString("full_name"));
                    ue.setPhoto(rs.getBytes("photo"));
                    ue.setPhotoSmall(rs.getBytes("photo_small"));
                    result.add(ue);
                }
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void remove(@NotNull UserEntity user) {
        try (PreparedStatement fPs = holder(url).connection().prepareStatement(
                "DELETE FROM friendship WHERE requester_id = ? OR addressee_id = ?"
        );
             PreparedStatement userPs = holder(url).connection().prepareStatement(
                     "DELETE FROM \"user\" WHERE id = ?")) {

            fPs.setObject(1, user.getId());
            fPs.setObject(2, user.getId());
            fPs.executeUpdate();

            userPs.setObject(1, user.getId());
            userPs.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @NotNull
    public List<FriendshipEntity> findInvitationByRequesterId(@NotNull UUID requesterId) {
        try (PreparedStatement ps = holder(url).connection().prepareStatement(
                "SELECT * FROM friendship f LEFT JOIN \"user\" u ON f.requester_id = u.id WHERE f.requester_id = ?"
        )) {
            ps.setObject(1, requesterId);
            ps.execute();

            List<FriendshipEntity> result = new ArrayList<>();
            try (ResultSet rs = ps.getResultSet()) {
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
                    result.add(fe);
                }
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}