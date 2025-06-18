package guru.qa.niffler.test.rest;

import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.RestTest;
import guru.qa.niffler.jupiter.extension.ApiLoginExtension;
import guru.qa.niffler.model.FriendshipStatus;
import guru.qa.niffler.model.pageable.RestResponsePage;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.service.impl.GatewayApiClient;
import guru.qa.niffler.service.impl.GatewayV2ApiClient;
import guru.qa.niffler.service.impl.UsersApiClient;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.List;

import static guru.qa.niffler.model.FriendshipStatus.INVITE_RECEIVED;
import static org.junit.jupiter.api.Assertions.assertEquals;

@RestTest
public class PublicApiRestTest {

    @RegisterExtension
    private static final ApiLoginExtension apiLoginExtension = ApiLoginExtension.rest();
    ;

    private final GatewayV2ApiClient gatewayV2ApiClient = new GatewayV2ApiClient();
    private final GatewayApiClient gatewayApiClient = new GatewayApiClient();
    private final UsersApiClient usersApiClient = new UsersApiClient();

    @ApiLogin
    @User(friends = 2, incomeInvitations = 2)
    @Test
    void friendsAndIncomeInvitationsShouldBeReturnedFilteredByUsername(UserJson user, @Token String token) {
        final int pageNumber = 0;
        final int size = 4;
        final UserJson searchedFriend = user.testData().friends().getFirst();

        final RestResponsePage<UserJson> response = gatewayV2ApiClient.allFriends(
                token,
                pageNumber,
                size,
                null,
                searchedFriend.username()
        );

        final UserJson actualFriend = response.getContent().getFirst();

        assertEquals(1, response.getNumberOfElements());
        assertEquals(searchedFriend.id(), actualFriend.id());
        assertEquals(searchedFriend.username(), actualFriend.username());
        assertEquals(FriendshipStatus.FRIEND, actualFriend.friendshipStatus());
    }

    @ApiLogin
    @User(friends = 1)
    @Test
    void shouldBeAbleToRemoveFriendship(UserJson user, @Token String token) {
        final UserJson friendToRemove = user.testData().friends().getFirst();

        gatewayApiClient.removeFriend(
                token,
                friendToRemove.username()
        );

        final List<UserJson> response = gatewayApiClient.allFriends(
                token,
                null
        );

        assertEquals(0, response.size());
    }


    @ApiLogin
    @User(incomeInvitations = 1)
    @Test
    void shouldBeAbleToAcceptFriendship(UserJson user, @Token String token) {
        final UserJson invitationToAccept = user.testData().incomeInvitations().getFirst();

        gatewayApiClient.acceptInvitation(
                token,
                invitationToAccept.username()
        );

        final List<UserJson> response = gatewayApiClient.allFriends(
                token,
                null
        );

        final UserJson acceptedFriend = response.getFirst();

        assertEquals(1, response.size());
        assertEquals(invitationToAccept.username(), acceptedFriend.username());
        assertEquals(FriendshipStatus.FRIEND, acceptedFriend.friendshipStatus());
    }

    @ApiLogin
    @User(incomeInvitations = 1)
    @Test
    void shouldBeAbleToDeclineFriendship(UserJson user, @Token String token) {
        final UserJson invitationToDecline = user.testData().incomeInvitations().getFirst();

        UserJson declinedFriend = gatewayApiClient.declineInvitation(
                token,
                invitationToDecline.username()
        );

        final List<UserJson> friends = gatewayApiClient.allFriends(
                token,
                null
        );

        final List<UserJson> incomeInvitations = friends
                .stream()
                .filter(userJson -> userJson.friendshipStatus().equals(INVITE_RECEIVED))
                .toList();

        assertEquals(invitationToDecline.username(), declinedFriend.username());
        assertEquals(0, friends.size());
        assertEquals(0, incomeInvitations.size());
    }

    @ApiLogin
    @User
    @Test
    void shouldCreateIncomeAndOutcomeInvitationsAfterSendingFriendRequest(UserJson sender, @Token String token) {
        final String addresseeName = RandomDataUtils.randomUsername();
        final String pw = "12345";

        final UserJson addressee = usersApiClient.createUser(
                addresseeName,
                pw
        );

        gatewayApiClient.sendInvitation(
                token,
                addresseeName
        );

        final UserJson actualOutcomeInvitation = gatewayApiClient.allPeople(
                token,
                addressee.username()
        ).getFirst();

        final UserJson incomeInvitationActual = usersApiClient.friends(
                addresseeName,
                sender.username()
        ).getFirst();

        assertEquals(addresseeName, actualOutcomeInvitation.username());
        assertEquals(sender.username(), incomeInvitationActual.username());
    }
}