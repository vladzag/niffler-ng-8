package guru.qa.niffler.test.rest;

import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.RestTest;
import guru.qa.niffler.jupiter.extension.ApiLoginExtension;
import guru.qa.niffler.model.pageable.RestResponsePage;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.service.impl.GatewayV2ApiClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RestTest
public class FriendsV2RestTest {

    @RegisterExtension
    static ApiLoginExtension apiLoginExtension = ApiLoginExtension.rest();

    private final GatewayV2ApiClient gatewayV2ApiClient = new GatewayV2ApiClient();

    @User(friends = 1, incomeInvitations = 2)
    @ApiLogin
    @Test
    void friendsAndIncomeInvitationsShouldBeReturnedFromGateway(@Token String bearerToken) {
        final Page<UserJson> responseBody = gatewayV2ApiClient.allFriends(
                "Bearer " + bearerToken,
                0,
                10,
                "username",
                null
        );
        assertEquals(3, responseBody.getContent().size());
    }

    @ApiLogin
    @User(friends = 5)
    @Test
    void friendsAndIncomeInvitationsListShouldBeReturnedSorted(@Token String token) {
        final RestResponsePage<UserJson> response = gatewayV2ApiClient.allFriends(
                token,
                0,
                5,
                "username,DESC",
                null);

        List<String> actualUsernames = response.getContent().stream()
                .map(UserJson::username)
                .toList();

        List<String> expectedUsernames = new ArrayList<>(actualUsernames);
        expectedUsernames.sort(Comparator.reverseOrder());

        assertEquals(expectedUsernames, actualUsernames);
    }

    @ApiLogin
    @User(friends = 1, incomeInvitations = 1)
    @Test
    void friendsAndIncomeInvitationsListShouldBeReturned(UserJson user, @Token String token) {
        final UserJson expectedFriend = user.testData().friends().getFirst();
        final UserJson expectedInvitation = user.testData().incomeInvitations().getFirst();

        final RestResponsePage<UserJson> response = gatewayV2ApiClient.allFriends(token, 0, 2, null, null);

        assertEquals(2, response.getContent().size());

        final UserJson actualInvitation = response.getContent().getFirst();
        final UserJson actualFriend = response.getContent().getLast();

        assertEquals(expectedFriend.id(), actualFriend.id());
        assertEquals(expectedInvitation.id(), actualInvitation.id());
    }

}