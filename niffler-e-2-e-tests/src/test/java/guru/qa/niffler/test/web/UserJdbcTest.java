package guru.qa.niffler.test.web;

import guru.qa.niffler.data.entity.userdata.FriendshipEntity;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.UsersDbClient;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static guru.qa.niffler.model.FriendshipStatus.INVITE_SENT;
import static org.junit.jupiter.api.Assertions.*;

public class UserJdbcTest {
    private static final String USER_PW = "12345";
    private static final UsersDbClient usersDbClient = new UsersDbClient();

    @ValueSource(strings = {
            "valentin-10"
    })
    @ParameterizedTest
    void springJdbcTest(String uname) {

        UserJson user = usersDbClient.createUser(
                uname,
                USER_PW
        );

        usersDbClient.createIncomeInvitations(user, 1);
        usersDbClient.createOutcomeInvitations(user, 1);
    }

    @Test
    void userCreationJdbcTest() {
        String username = RandomDataUtils.randomUsername();
        UserJson userWithJdbc = usersDbClient.createUser(
                username,
                USER_PW
        );
        assertTrue(usersDbClient.findByUsername(userWithJdbc.username()).isPresent());
        assertTrue(usersDbClient.findById(userWithJdbc.id()).isPresent());
    }

    @Test
    void updateUserInfoJdbcTest() {
        String username = RandomDataUtils.randomUsername();
        String firstName = "Мария";
        String surname = "Олеговна";
        String fullname = "Мурашкина Мария Олеговна";

        UserJson initialUser = usersDbClient.createUser(
                username,
                USER_PW
        );

        UserJson newUserInfo = new UserJson(
                initialUser.id(),
                initialUser.username(),
                firstName,
                surname,
                fullname,
                CurrencyValues.USD,
                null,
                null,
                INVITE_SENT
        );

        UserJson updatedUser = usersDbClient.updateUserInfo(newUserInfo);
        FriendshipEntity invite =
                usersDbClient.findInvitationByRequesterId(initialUser.id()).getFirst();

        assertEquals(firstName, updatedUser.firstname());
        assertEquals(surname, updatedUser.surname());
        assertEquals(fullname, updatedUser.fullname());
        assertEquals(CurrencyValues.USD, updatedUser.currency());
        assertEquals("PENDING", invite.getStatus().name());
    }

    @Test
    void sendInvitationStatusJdbcTest() {
        String username = RandomDataUtils.randomUsername();
        UserJson requester = usersDbClient.createUser(
                username,
                USER_PW
        );
        usersDbClient.sendInvitation(requester);

        FriendshipEntity invite =
                usersDbClient.findInvitationByRequesterId(requester.id()).getFirst();
        assertEquals("PENDING", invite.getStatus().name());
    }

    @Test
    void addFriendJdbcTest() {
        String rUsername = RandomDataUtils.randomUsername();
        String aUsername = RandomDataUtils.randomUsername();
        UserJson requester = usersDbClient.createUser(
                rUsername,
                USER_PW
        );

        UserJson addressee = usersDbClient.createUser(
                aUsername,
                USER_PW
        );

        usersDbClient.addFriend(requester, addressee);

        FriendshipEntity outgoingInvite =
                usersDbClient.findInvitationByRequesterId(requester.id()).getFirst();
        FriendshipEntity incomingInvite =
                usersDbClient.findInvitationByRequesterId(addressee.id()).getFirst();
        assertEquals("ACCEPTED", outgoingInvite.getStatus().name());
        assertEquals("ACCEPTED", incomingInvite.getStatus().name());
    }

    @Test
    void removeUserWithJdbcTest() {
        String username = RandomDataUtils.randomUsername();
        UserJson requester = usersDbClient.createUser(
                username,
                USER_PW
        );

        usersDbClient.sendInvitation(requester);
        usersDbClient.deleteUser(requester);

        assertFalse(usersDbClient.findById(requester.id()).isPresent());
        assertTrue(usersDbClient.findInvitationByRequesterId(requester.id()).isEmpty());
    }
}
