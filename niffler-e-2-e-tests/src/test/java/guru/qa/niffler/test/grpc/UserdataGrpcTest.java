package guru.qa.niffler.test.grpc;


import guru.qa.niffler.grpc.*;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.rest.UserJson;
import org.junit.jupiter.api.Test;

import static guru.qa.niffler.model.FriendshipStatus.INVITE_SENT;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserdataGrpcTest extends BaseGrpcTest {

    @Test
    @User(friends = 5)
    void shouldReturnFriendsListPage(UserJson user) {
        final UserPageRequest request = UserPageRequest.newBuilder()
                .setUsername(user.username())
                .setPage(0)
                .setSize(10)
                .build();
        UserPageResponse userPageResponse = userdataBlockingStub.friendsPage(request);
        assertEquals(5, userPageResponse.getEdgesCount());
        assertEquals(5, userPageResponse.getTotalElements());
        assertEquals(1, userPageResponse.getTotalPages());
    }

    @Test
    @User(friends = 5)
    void shouldReturnFriendFilteredByUsername(UserJson user) {
        final String expectedUsername = user.testData().friendsUsernames()[0];
        final UserPageRequest request = UserPageRequest.newBuilder()
                .setUsername(user.username())
                .setPage(0)
                .setSize(10)
                .setSearchQuery(expectedUsername)
                .build();
        UserPageResponse userPageResponse = userdataBlockingStub.friendsPage(request);
        assertEquals(expectedUsername, userPageResponse.getEdgesList().getFirst().getUsername());
        assertEquals(1, userPageResponse.getEdgesCount());
    }

    @Test
    @User(friends = 1)
    void shouldRemoveFriendship(UserJson user) {
        final String friendToRemove = user.testData().friendsUsernames()[0];
        final FriendshipRequest removeRequest = FriendshipRequest.newBuilder()
                .setUsername(user.username())
                .setTargetUsername(friendToRemove)
                .build();
        userdataBlockingStub.removeFriend(removeRequest);

        final UserBulkRequest request = UserBulkRequest.newBuilder()
                .setUsername(user.username())
                .setSearchQuery(friendToRemove)
                .build();
        UsersBulkResponse userPageResponse = userdataBlockingStub.friends(request);
        assertEquals(0, userPageResponse.getUserForBulkResponseList().size());
    }

    @Test
    @User(incomeInvitations = 1)
    void shouldAcceptFriendship(UserJson user) {
        final UserJson friendToAccept = user.testData().incomeInvitations().get(0);
        final FriendshipRequest removeRequest = FriendshipRequest.newBuilder()
                .setUsername(user.username())
                .setTargetUsername(friendToAccept.username())
                .build();
        userdataBlockingStub.acceptFriendshipRequest(removeRequest);

        final UserBulkRequest request = UserBulkRequest.newBuilder()
                .setUsername(user.username())
                .setSearchQuery(friendToAccept.username())
                .build();
        UsersBulkResponse userPageResponse = userdataBlockingStub.friends(request);
        assertEquals(
                friendToAccept.username(),
                userPageResponse.getUserForBulkResponseList().get(0).getUsername()
        );
    }

    @Test
    @User(incomeInvitations = 1)
    void shouldDeclineFriendship(UserJson user) {
        final UserJson friendToDecline = user.testData().incomeInvitations().get(0);
        final FriendshipRequest removeRequest = FriendshipRequest.newBuilder()
                .setUsername(user.username())
                .setTargetUsername(friendToDecline.username())
                .build();
        userdataBlockingStub.declineFriendshipRequest(removeRequest);

        final UserBulkRequest request = UserBulkRequest.newBuilder()
                .setUsername(user.username())
                .setSearchQuery(friendToDecline.username())
                .build();
        UsersBulkResponse userPageResponse = userdataBlockingStub.friends(request);
        assertEquals(0, userPageResponse.getUserForBulkResponseList().size());
    }

    @Test
    @User
    void sendFriendshipRequest(UserJson user) {
        final UserPageRequest allUsersRequest = UserPageRequest.newBuilder()
                .setUsername(user.username())
                .setPage(2)
                .setSize(1)
                .build();

        String targetUsername = userdataBlockingStub.allUsersPage(allUsersRequest)
                .getEdgesList()
                .getLast()
                .getUsername();

        final FriendshipRequest friendshipRequest = FriendshipRequest.newBuilder()
                .setUsername(user.username())
                .setTargetUsername(targetUsername)
                .build();
        UserResponse targetUserResponse = userdataBlockingStub.createFriendshipRequest(friendshipRequest);

        assertEquals(user.username(), targetUserResponse.getUsername());
        assertEquals(FriendshipStatus.valueOf(INVITE_SENT.name()),
                targetUserResponse.getFriendshipStatus());
    }

}