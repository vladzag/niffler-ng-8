package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.userdata.FriendshipEntity;
import guru.qa.niffler.data.entity.userdata.FriendshipStatus;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.mapper.UdUserEntityRowMapper;
import guru.qa.niffler.data.repository.UDUserRepository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.templates.Connections.holder;

public class UDUserRepositoryJdbc implements UDUserRepository {

    private static final Config CFG = Config.getInstance();

    @Override
    public UserEntity create(UserEntity user) {
        try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                "INSERT INTO \"user\" (username, currency, firstname, surname, photo, photo_small, full_name) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)",
                PreparedStatement.RETURN_GENERATED_KEYS
        )) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getCurrency().name());
            ps.setString(3, user.getFirstname());
            ps.setString(4, user.getSurname());
            ps.setBytes(5, user.getPhoto());
            ps.setBytes(6, user.getPhotoSmall());
            ps.setString(7, user.getFullname());

            ps.executeUpdate();

            final UUID generatedKey;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedKey = rs.getObject("id", UUID.class);
                } else {
                    throw new SQLException("Can't find user id in ResultSet");
                }
            }

            user.setId(generatedKey);
            return user;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<UserEntity> findById(UUID id) {
        try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM \"user\" u LEFT JOIN friendship f ON (u.id = f.addressee_id OR u.id = f.requester_id)\n" +
                        "WHERE u.id = ?"
        )) {
            ps.setObject(1, id);
            ps.execute();

            try (ResultSet rs = ps.getResultSet()) {
                UserEntity user = null;

                while (rs.next()) {
                    if (user == null) {
                        user = UdUserEntityRowMapper.INSTANCE.mapRow(rs, 1);
                    }
                    //По полю created_date определяем есть ли у юзера запросы дружбы (friendship requests)
                    if (rs.getObject("created_date") != null) {
                        FriendshipEntity friendship = new FriendshipEntity();
                        //Если пользователь является отправителем запроса дружбы, то заполняем сущность friendship
                        if (rs.getObject("requester_id", UUID.class).equals(user.getId())) {
                            friendship.setRequester(user);
                            UserEntity addressee = new UserEntity();
                            addressee.setId(rs.getObject("addressee_id", UUID.class));
                            friendship.setAddressee(addressee);
                            friendship.setCreatedDate(rs.getDate("created_date"));
                            friendship.setStatus(FriendshipStatus.valueOf(rs.getString("status")));
                            user.getFriendshipRequests().add(friendship);
                        }
                        //Если пользователю является получателем запрос дружбы, то заполняем сущность friendship
                        if (rs.getObject("addressee_id", UUID.class).equals(user.getId())) {
                            friendship.setAddressee(user);
                            UserEntity requester = new UserEntity();
                            requester.setId(rs.getObject("requester_id", UUID.class));
                            friendship.setRequester(requester);
                            friendship.setCreatedDate(rs.getDate("created_date"));
                            friendship.setStatus(FriendshipStatus.valueOf(rs.getString("status")));
                            user.getFriendshipAddressees().add(friendship);
                        }
                    }
                }
                if (user == null) {
                    return Optional.empty();
                } else {
                    return Optional.of(user);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addIncomeInvitation(UserEntity requester, UserEntity addressee) {
        try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                "INSERT INTO friendship (requester_id, addressee_id, status, created_date) " +
                        "VALUES (?,?,?,?)"
        )) {
            ps.setObject(1, requester.getId());
            ps.setObject(2, addressee.getId());
            ps.setString(3, FriendshipStatus.PENDING.name());
            ps.setDate(4, new Date(System.currentTimeMillis()));

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void addOutcomeInvitation(UserEntity requester, UserEntity addressee) {

        try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                "INSERT INTO friendship (requester_id, addressee_id, status, created_date) " +
                        "VALUES (?,?,?,?)"
        )) {
            ps.setObject(1, requester.getId());
            ps.setObject(2, addressee.getId());
            ps.setString(3, FriendshipStatus.PENDING.name());
            ps.setDate(4, new Date(System.currentTimeMillis()));

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addFriend(UserEntity requester, UserEntity addressee) {
        try (PreparedStatement sentByRequester = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                "INSERT INTO friendship (requester_id, addressee_id, status, created_date) VALUES (?,?,?,?)");
             PreparedStatement acceptedByAddressee = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                     "INSERT INTO friendship (requester_id, addressee_id, status, created_date) VALUES (?,?,?,?)"
             )
        ) {
            sentByRequester.setObject(1, requester.getId());
            sentByRequester.setObject(2, addressee.getId());
            sentByRequester.setString(3, FriendshipStatus.ACCEPTED.name());
            sentByRequester.setDate(4, new Date(System.currentTimeMillis()));
            sentByRequester.executeUpdate();

            acceptedByAddressee.setObject(1, addressee.getId());
            acceptedByAddressee.setObject(2, requester.getId());
            acceptedByAddressee.setString(3, FriendshipStatus.ACCEPTED.name());
            acceptedByAddressee.setDate(4, new Date(System.currentTimeMillis()));
            acceptedByAddressee.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<FriendshipEntity> getFriendshipRequestsByUsersID(UserEntity requester, UserEntity addressee) {
        try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM friendship WHERE (requester_id = ? AND addressee_id = ?) OR (requester_id = ? AND addressee_id = ?)"
        )) {
            ps.setObject(1, requester.getId());
            ps.setObject(2, addressee.getId());
            ps.setObject(3, addressee.getId());
            ps.setObject(4, requester.getId());
            ps.execute();

            List<FriendshipEntity> result = new ArrayList<>();

            try (ResultSet rs = ps.getResultSet()) {

                while (rs.next()) {
                    FriendshipEntity fe = new FriendshipEntity();
                    UserEntity requesterEntity = new UserEntity();
                    requesterEntity.setId(rs.getObject("requester_id", UUID.class));
                    fe.setRequester(requesterEntity);
                    UserEntity addresseeEntity = new UserEntity();
                    addresseeEntity.setId(rs.getObject("addressee_id", UUID.class));
                    fe.setAddressee(addresseeEntity);
                    fe.setCreatedDate(rs.getDate("created_date"));
                    fe.setStatus(FriendshipStatus.valueOf(rs.getString("status")));

                    result.add(fe);
                }
                return result;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}