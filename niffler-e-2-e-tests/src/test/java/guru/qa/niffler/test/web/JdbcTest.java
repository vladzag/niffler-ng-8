package guru.qa.niffler.test.web;

import guru.qa.niffler.data.entity.userdata.FriendshipEntity;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.SpendDbClient;
import guru.qa.niffler.service.UsersDbClient;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Disabled
public class JdbcTest {

    /*@Test
    void txTest() {
        UsersDbClient usersDbClient = new UsersDbClient();
        String username = RandomDataUtils.randomUsername();

        UserJson user = usersDbClient.createUserJdbcWithTx(
                new UserJson(
                        null,
                        username,
                        null,
                        null,
                        null,
                        CurrencyValues.RUB,
                        null,
                        null,
                        null
                )
        );
        Assertions.assertTrue(usersDbClient.findUserByUsername(user.username()).isPresent());
    }

    static UsersDbClient usersDbClient = new UsersDbClient();

    @ValueSource(
            strings = {
                    "valentin-11",
            }
    )
    @ParameterizedTest
    void springJdbcTest() {
        String username = RandomDataUtils.randomUsername();

        UserJson user = usersDbClient.createUser(username, "12345");
        usersDbClient.createIncomeInvitations(user, 1);
        usersDbClient.createOutcomeInvitations(user, 1);
    }

    @Test
    void springJdbcWithoutTxTest() {
        UsersDbClient userDbClient = new UsersDbClient();
        String username = RandomDataUtils.randomUsername();

        UserJson user = userDbClient.createUserSpringJdbcWithoutTx(
                new UserJson(
                        null,
                        username,
                        null,
                        null,
                        null,
                        CurrencyValues.RUB,
                        null,
                        null,
                        null
                )
        );
        Assertions.assertTrue(userDbClient.findUserByUsername(user.username()).isPresent());
    }


    @Test
    void springJdbcTxTest() {
        SpendDbClient spendDbClient = new SpendDbClient();
        String categoryName = RandomDataUtils.randomCategoryName();

        spendDbClient.createSpendSpringJdbc(
                new SpendJson(
                        null,
                        new Date(),
                        new CategoryJson(
                                null,
                                categoryName,
                                "duck",
                                false
                        ),
                        CurrencyValues.RUB,
                        1800.0,
                        "Fast Food description",
                        "duck"
                )
        );
    }

    @Test
    void springChainedManagerWithCorrectDataTest() {
        UsersDbClient userDbClient = new UsersDbClient();
        String username = RandomDataUtils.randomUsername();

        UserJson user = userDbClient.createUser(
                new UserJson(
                        null,
                        username,
                        null,
                        null,
                        "Chained Manager Positive Test",
                        CurrencyValues.RUB,
                        null,
                        null,
                        null

                ));

        Assertions.assertTrue(userDbClient.findUserByUsername(user.username()).isPresent());
    }

    @Test
    void springChainedManagerWithIncorrectDataTest() {
        UsersDbClient userDbClient = new UsersDbClient();
        String username = RandomDataUtils.randomUsername();

        UserJson user = userDbClient.createUser(
                new UserJson(
                        null,
                        username,
                        null,
                        null,
                        "Chained Manager Negative Test",
                        CurrencyValues.RUB,
                        null,
                        null,
                        null

                ));

        System.out.println(user);
    }

    @Test
    void findUserWithFriendshipByIdWithJoinRequestTest() {
        UsersDbClient usersDbClient = new UsersDbClient();

        Optional<UserEntity> userByID =
                usersDbClient.findUserByID(UUID.fromString("c7f0e3b7-a0f7-4d6e-a819-57289217cd0b"));

        if (userByID.isPresent()) {
            UserEntity user = userByID.get();
            System.out.println(user);
            System.out.println("Friendship requests: ");
            user.getFriendshipRequests().forEach(System.out::println);
            System.out.println("Friendship addressees: ");
            user.getFriendshipAddressees().forEach(System.out::println);
        }
    }

    @Test
    void addFriendInvitationTest() {
        UsersDbClient usersDbClient = new UsersDbClient();

        UUID requesterUUID = UUID.fromString("598c9c39-e3d5-4f4a-b803-6044da6f5c1e");
        UUID addresseeUUID = UUID.fromString("68adfcea-54c1-4991-84f8-e68156de5d3b");

        usersDbClient.createIncomeInvitations(requesterUUID, addresseeUUID);

        List<FriendshipEntity> requests = usersDbClient.getFriendshipRequestsByUserID(requesterUUID, addresseeUUID);

        assertEquals("PENDING", requests.getFirst().getStatus().name());
    }


    @Test
    void addFriendTest() {
        UsersDbClient usersDbClient = new UsersDbClient();

        UUID requesterUUID = UUID.fromString("36b16a70-62e6-4727-912a-f6dfe66cdbe5");
        UUID addresseeUUID = UUID.fromString("1f50791c-69a0-49e4-a81a-502a153174ca");

        usersDbClient.addFriend(requesterUUID, addresseeUUID);

        List<FriendshipEntity> requests = usersDbClient.getFriendshipRequestsByUserID(requesterUUID, addresseeUUID);

        assertEquals(2, requests.size());
        assertTrue(requests.stream().allMatch(f -> f.getStatus().name().equals("ACCEPTED")));
    }*/
}
