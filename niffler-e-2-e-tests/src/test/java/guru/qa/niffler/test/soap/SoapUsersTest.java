package guru.qa.niffler.test.soap;

import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.SoapTest;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.service.impl.UserdataSoapClient;
import jaxb.userdata.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static guru.qa.niffler.model.FriendshipStatus.INVITE_SENT;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SoapTest
public class SoapUsersTest {


    private final UserdataSoapClient userdataSoapClient = new UserdataSoapClient();


    @Test
    @User
    void currentUserTest(UserJson user) throws IOException {
        CurrentUserRequest request = new CurrentUserRequest();
        request.setUsername(user.username());
        UserResponse response = userdataSoapClient.currentUser(request);
        assertEquals(
                user.username(),
                response.getUser().getUsername()
        );
    }

    @Test
    @User(friends = 5)
    void shouldReturnFriendsListPage(UserJson user) {
        final FriendsPageRequest request = new FriendsPageRequest();
        request.setUsername(user.username());
        PageInfo pageInfo = new PageInfo();
        pageInfo.setPage(0);
        pageInfo.setSize(5);
        request.setPageInfo(pageInfo);

        UsersResponse userPageResponse = userdataSoapClient.friendsPage(request);

        assertEquals(5, userPageResponse.getUser().size());
        assertEquals(5, userPageResponse.getTotalElements());
        assertEquals(1, userPageResponse.getTotalPages());
    }

    @Test
    @User(friends = 5)
    void shouldReturnFriendFilteredByUsername(UserJson user) {
        final String expectedUsername = user.testData().friendsUsernames()[0];
        final FriendsPageRequest request = new FriendsPageRequest();
        request.setUsername(user.username());
        request.setSearchQuery(expectedUsername);
        PageInfo pageInfo = new PageInfo();
        pageInfo.setPage(0);
        pageInfo.setSize(5);
        request.setPageInfo(pageInfo);

        UsersResponse userPageResponse = userdataSoapClient.friendsPage(request);

        assertEquals(expectedUsername, userPageResponse.getUser().getFirst().getUsername());
        assertEquals(1, userPageResponse.getUser().size());
    }

    @Test
    @User(friends = 1)
    void shouldRemoveFriendship(UserJson user) {
        final String friendToRemove = user.testData().friendsUsernames()[0];

        final RemoveFriendRequest removeRequest = new RemoveFriendRequest();
        removeRequest.setUsername(user.username());
        removeRequest.setFriendToBeRemoved(friendToRemove);

        userdataSoapClient.removeFriend(removeRequest);

        UsersResponse friendFilteredResponse = getFriendByUsername(
                user.username(),
                friendToRemove
        );
        assertEquals(0, friendFilteredResponse.getUser().size());
    }

    @Test
    @User(incomeInvitations = 1)
    void shouldAcceptFriendship(UserJson user) {
        final UserJson friendToAccept = user.testData().incomeInvitations().get(0);
        final AcceptInvitationRequest removeRequest = new AcceptInvitationRequest();
        removeRequest.setUsername(user.username());
        removeRequest.setFriendToBeAdded(friendToAccept.username());

        userdataSoapClient.acceptFriend(removeRequest);

        UsersResponse friendFilteredResponse = getFriendByUsername(
                user.username(),
                friendToAccept.username()
        );

        assertEquals(
                friendToAccept.username(),
                friendFilteredResponse.getUser().getFirst().getUsername()
        );
    }

    @Test
    @User(incomeInvitations = 1)
    void shouldDeclineFriendship(UserJson user) {
        final UserJson friendToDecline = user.testData().incomeInvitations().get(0);
        final DeclineInvitationRequest declineRequest = new DeclineInvitationRequest();
        declineRequest.setUsername(user.username());
        declineRequest.setInvitationToBeDeclined(friendToDecline.username());

        userdataSoapClient.declineFriend(declineRequest);

        UsersResponse friendFilteredResponse = getFriendByUsername(
                user.username(),
                friendToDecline.username()
        );

        assertEquals(0, friendFilteredResponse.getUser().size());
    }

    @Test
    @User
    void sendFriendshipRequest(UserJson user) {
        final AllUsersPageRequest request = new AllUsersPageRequest();
        request.setUsername(user.username());
        PageInfo pageInfo = new PageInfo();
        pageInfo.setPage(0);
        pageInfo.setSize(5);
        request.setPageInfo(pageInfo);

        final String targetUsername = userdataSoapClient.allUsersPage(request)
                .getUser().getLast().getUsername();

        final SendInvitationRequest friendshipRequest = new SendInvitationRequest();
        friendshipRequest.setUsername(user.username());
        friendshipRequest.setFriendToBeRequested(targetUsername);

        UserResponse userResponse = userdataSoapClient.addFriend(friendshipRequest);

        assertEquals(
                targetUsername,
                userResponse.getUser().getUsername()
        );
        assertEquals(
                FriendshipStatus.valueOf(INVITE_SENT.name()),
                userResponse.getUser().getFriendshipStatus()
        );
    }

    private UsersResponse getFriendByUsername(String username, String targetUsername) {
        final FriendsRequest friendRequest = new FriendsRequest();
        friendRequest.setUsername(username);
        friendRequest.setSearchQuery(targetUsername);
        return userdataSoapClient.friends(friendRequest);
    }
}
