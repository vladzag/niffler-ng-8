package guru.qa.niffler.service;

import com.google.protobuf.Empty;
import guru.qa.niffler.data.FriendshipEntity;
import guru.qa.niffler.data.UserEntity;
import guru.qa.niffler.data.projection.UserWithStatus;
import guru.qa.niffler.data.repository.UserRepository;
import guru.qa.niffler.ex.NotFoundException;
import guru.qa.niffler.ex.SameUsernameException;
import guru.qa.niffler.grpc.*;
import io.grpc.stub.StreamObserver;
import jakarta.annotation.Nonnull;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.stream.Collectors;

import static guru.qa.niffler.data.UserEntity.toUserResponse;
import static sun.java2d.marlin.CollinearSimplifier.SimplifierState.Empty;

@GrpcService
public class GrpcUserdataService extends NifflerUserdataServiceGrpc.NifflerUserdataServiceImplBase {

    private final UserRepository userRepository;

    @Autowired
    public GrpcUserdataService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void update(UserRequest request, StreamObserver<UserResponse> responseObserver) {
        UserEntity ueToUpdate = userRepository.findByUsername(request.getUsername()).orElseThrow(
                () -> new NotFoundException("User" + request.getUsername() + " not found")
        );
        ueToUpdate.setId(ueToUpdate.getId());
        ueToUpdate.setUsername(ueToUpdate.getUsername());
        ueToUpdate.setCurrency(ueToUpdate.getCurrency());
        ueToUpdate.setFullname(request.getFullname());
        ueToUpdate.setFirstname(request.getFirstname());
        ueToUpdate.setSurname(request.getSurname());
        ueToUpdate.setPhoto(!request.getPhoto().isEmpty() ? request.getPhoto().getBytes(StandardCharsets.UTF_8) : null);
        ueToUpdate.setPhotoSmall(!request.getPhoto().isEmpty() ? new SmallPhoto(100, 100, request.getPhoto()).bytes() : null);

        UserEntity saved = userRepository.save(ueToUpdate);

        responseObserver.onNext(
                toUserResponse(saved)
        );
        responseObserver.onCompleted();
    }

    @Override
    public void getCurrentUser(UsernameRequest request, StreamObserver<UserResponse> responseObserver) {
        UserEntity userEntity = userRepository.findByUsername(request.getUsername()).orElseThrow(
                () -> new NotFoundException("User" + request.getUsername() + " not found")
        );
        responseObserver.onNext(
                UserResponse.newBuilder()
                        .setId(userEntity.getId().toString())
                        .setUsername(userEntity.getUsername())
                        .setFirstname(userEntity.getFirstname() != null ? userEntity.getFirstname() : "")
                        .setSurname(userEntity.getSurname() != null ? userEntity.getSurname() : "")
                        .setFullname(userEntity.getFullname() != null ? userEntity.getFullname() : "")
                        .setCurrency(CurrencyValues.valueOf(userEntity.getCurrency().name()))
                        .setPhoto(userEntity.getPhoto() != null && userEntity.getPhoto().length > 0 ? new String(userEntity.getPhoto(), StandardCharsets.UTF_8) : "")
                        .setPhotoSmall(userEntity.getPhotoSmall() != null && userEntity.getPhotoSmall().length > 0 ? new String(userEntity.getPhotoSmall(), StandardCharsets.UTF_8) : "")
                        .setFriendshipStatus(FriendshipStatus.UNDEFINED)
                        .build());
        responseObserver.onCompleted();
    }

    @Override
    public void allUsers(UserBulkRequest request, StreamObserver<UsersBulkResponse> responseObserver) {
        responseObserver.onNext(
                UsersBulkResponse.newBuilder()
                        .addAllUserForBulkResponse(
                                userRepository.findByUsernameNot(
                                                request.getUsername(),
                                                request.getSearchQuery()
                                        )
                                        .stream()
                                        .map(UserWithStatus::toUserForBulkResponse)
                                        .collect(Collectors.toList())
                        )
                        .build()
        );
        responseObserver.onCompleted();
    }

    @Override
    public void allUsersPage(UserPageRequest request, StreamObserver<UserPageResponse> responseObserver) {
        Page<UserWithStatus> users = userRepository.findByUsernameNot(
                request.getUsername(),
                request.getSearchQuery(),
                PageRequest.of(request.getPage(), request.getSize())
        );
        responseObserver.onNext(
                UserPageResponse.newBuilder()
                        .addAllEdges(
                                users.stream()
                                        .map(UserWithStatus::toUserForBulkResponse)
                                        .collect(Collectors.toList())
                        )
                        .setTotalElements(users.getNumberOfElements())
                        .setTotalPages(users.getTotalPages())
                        .setFirst(users.isFirst())
                        .setLast(users.isLast())
                        .setSize(request.getSize())
                        .build()
        );
        responseObserver.onCompleted();
    }

    @Override
    public void friends(UserBulkRequest request, StreamObserver<UsersBulkResponse> responseObserver) {
        responseObserver.onNext(
                UsersBulkResponse.newBuilder()
                        .addAllUserForBulkResponse(
                                userRepository.findFriends(
                                                userRepository.findByUsername(request.getUsername()).orElseThrow(),
                                                request.getSearchQuery()
                                        ).stream()
                                        .map(UserWithStatus::toUserForBulkResponse)
                                        .collect(Collectors.toList())
                        )
                        .build()
        );
        responseObserver.onCompleted();
    }

    @Override
    public void friendsPage(UserPageRequest request, StreamObserver<UserPageResponse> responseObserver) {
        Page<UserWithStatus> friends = userRepository.findFriends(
                userRepository.findByUsername(request.getUsername()).orElseThrow(),
                request.getSearchQuery(),
                PageRequest.of(request.getPage(), request.getSize())
        );
        responseObserver.onNext(
                UserPageResponse.newBuilder()
                        .addAllEdges(
                                friends.stream()
                                        .map(UserWithStatus::toUserForBulkResponse)
                                        .collect(Collectors.toList())
                        )
                        .setTotalElements(friends.getNumberOfElements())
                        .setTotalPages(friends.getTotalPages())
                        .setFirst(friends.isFirst())
                        .setLast(friends.isLast())
                        .setSize(request.getSize())
                        .build()
        );
        responseObserver.onCompleted();
    }

    @Transactional
    @Override
    public void createFriendshipRequest(FriendshipRequest request, StreamObserver<UserResponse> responseObserver) {
        if (Objects.equals(request.getUsername(), request.getTargetUsername())) {
            throw new SameUsernameException("Can`t create friendship request for self user");
        }
        UserEntity currentUser = getRequiredUser(request.getUsername());
        UserEntity targetUser = getRequiredUser(request.getTargetUsername());
        currentUser.addFriends(guru.qa.niffler.data.FriendshipStatus.PENDING, targetUser);
        UserEntity saved = userRepository.save(currentUser);

        responseObserver.onNext(
                toUserResponse(saved, FriendshipStatus.INVITE_SENT)
        );
        responseObserver.onCompleted();
    }

    @Transactional
    @Override
    public void acceptFriendshipRequest(FriendshipRequest request, StreamObserver<UserResponse> responseObserver) {
        if (Objects.equals(request.getUsername(), request.getTargetUsername())) {
            throw new SameUsernameException("Can`t accept friendship request for self user");
        }
        UserEntity currentUser = getRequiredUser(request.getUsername());
        UserEntity targetUser = getRequiredUser(request.getTargetUsername());

        FriendshipEntity invite = currentUser.getFriendshipAddressees()
                .stream()
                .filter(fe -> fe.getRequester().equals(targetUser))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Can`t find invitation from username: '" + request.getTargetUsername() + "'"));

        invite.setStatus(guru.qa.niffler.data.FriendshipStatus.ACCEPTED);
        currentUser.addFriends(guru.qa.niffler.data.FriendshipStatus.ACCEPTED, targetUser);
        UserEntity saved = userRepository.save(currentUser);

        responseObserver.onNext(
                toUserResponse(saved, FriendshipStatus.FRIEND)
        );
        responseObserver.onCompleted();
    }

    @Transactional
    @Override
    public void declineFriendshipRequest(FriendshipRequest request, StreamObserver<UserResponse> responseObserver) {
        if (Objects.equals(request.getUsername(), request.getTargetUsername())) {
            throw new SameUsernameException("Can`t decline friendship request for self user");
        }
        UserEntity currentUser = getRequiredUser(request.getUsername());
        UserEntity targetUser = getRequiredUser(request.getTargetUsername());

        currentUser.removeInvites(targetUser);
        targetUser.removeFriends(currentUser);

        userRepository.save(currentUser);
        UserEntity savedTargetUser = userRepository.save(targetUser);

        responseObserver.onNext(
                toUserResponse(savedTargetUser)
        );
        responseObserver.onCompleted();
    }

    @Transactional
    @Override
    public void removeFriend(FriendshipRequest request, StreamObserver<Empty> responseObserver) {
        if (Objects.equals(request.getUsername(), request.getTargetUsername())) {
            throw new SameUsernameException("Can`t remove friendship relation for self user");
        }
        UserEntity currentUser = getRequiredUser(request.getUsername());
        UserEntity targetUser = getRequiredUser(request.getTargetUsername());

        currentUser.removeFriends(targetUser);
        currentUser.removeInvites(targetUser);
        targetUser.removeFriends(currentUser);
        targetUser.removeInvites(currentUser);

        userRepository.save(currentUser);
        userRepository.save(targetUser);

        responseObserver.onNext(
                Empty.getDefaultInstance()
        );
        responseObserver.onCompleted();
    }

    @Nonnull
    UserEntity getRequiredUser(@Nonnull String username) {
        return userRepository.findByUsername(username).orElseThrow(
                () -> new NotFoundException("Can`t find user by username: '" + username + "'")
        );
    }

}