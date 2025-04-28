package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.data.dao.UdUserDao;

import guru.qa.niffler.data.dao.impl.UdUserDaoJdbc;
import guru.qa.niffler.data.entity.userdata.FriendshipEntity;
import guru.qa.niffler.data.entity.userdata.FriendshipStatus;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.repository.UserDataUserRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserDataUserRepositoryJdbc implements UserDataUserRepository {

    private final UdUserDao udUserDao = new UdUserDaoJdbc();

    @Override
    public UserEntity create(UserEntity user) {
        return udUserDao.create(user);
    }

    @Override
    public Optional<UserEntity> findById(UUID id) {
        return udUserDao.findById(id);
    }

    @Override
    public Optional<UserEntity> findByUsername(String username) {
        return udUserDao.findByUsername(username);
    }

    @Override
    public UserEntity update(UserEntity user) {
        return udUserDao.update(user);
    }

    @Override
    public void sendInvitation(UserEntity requester, UserEntity addressee) {
        requester.addFriends(FriendshipStatus.PENDING, addressee);
        udUserDao.update(requester);
    }

    @Override
    public void addFriend(UserEntity requester, UserEntity addressee) {
        requester.addFriends(FriendshipStatus.ACCEPTED, addressee);
        addressee.addFriends(FriendshipStatus.ACCEPTED, requester);
        udUserDao.update(requester);
        udUserDao.update(addressee);
    }

    @Override
    public void remove(UserEntity user) {
        udUserDao.remove(user);
    }

    @Override
    public List<FriendshipEntity> findInvitationByRequesterId(UUID requesterId) {
        return udUserDao.findInvitationByRequesterId(requesterId);
    }
}