package guru.qa.niffler.test.web;

import guru.qa.niffler.model.AuthUserJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.UserDbClient;
import org.junit.jupiter.api.Test;

import static guru.qa.niffler.utils.RandomDataUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class JTAVerificationTest {

    @Test
    void xaTransactionTest() {
        UserDbClient userDbClient = new UserDbClient();
        String username = randomUsername();
        String password = "123456";

        UserJson user = userDbClient.createUser(new UserJson(
                null,
                username,
                randomName(),
                randomSurname(),
                randomName() + randomSurname(),
                CurrencyValues.RUB,
                null,
                null,
                new AuthUserJson(
                        null,
                        username,
                        password,
                        true,
                        true,
                        true,
                        true
                        , null)));

        assertEquals(username, user.username());
    }

    @Test
    void xaTransactionTestFailed() {
        UserDbClient userDbClient = new UserDbClient();
        String username = "incorrect" + randomUsername();
        String password = "123456";

        UserJson user = userDbClient.createUser(new UserJson(null, username, randomName(), randomSurname(), randomName() + randomSurname(), CurrencyValues.RUB,
                null, null, new AuthUserJson(
                null, username, "9", true, true, true, true, null)));

        assertFalse(userDbClient.findUserByUsername(user.username()).isPresent());
    }


}