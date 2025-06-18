package guru.qa.niffler.test.fake;

import guru.qa.niffler.jupiter.extension.InjectClientExtension;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.rest.CategoryJson;
import guru.qa.niffler.model.rest.SpendJson;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.service.SpendClient;
import guru.qa.niffler.service.UsersClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Date;

@ExtendWith(InjectClientExtension.class)
@EnabledIfSystemProperty(named = "client.impl", matches = "db")
public class JdbcTest {

    private UsersClient usersClient;
    private SpendClient spendClient;

    @Test
    void txTest() {
        SpendJson spend = spendClient.createSpend(
                new SpendJson(
                        null,
                        new Date(),
                        new CategoryJson(
                                null,
                                "cat-name-tx-3",
                                "duck",
                                false
                        ),
                        CurrencyValues.RUB,
                        1000.0,
                        "spend-name-tx-3",
                        "duck"
                )
        );
    }

    @ValueSource(strings = {
            "valentin-11"
    })
    @ParameterizedTest
    void springJdbcTest(String uname) {
        UserJson user = usersClient.createUser(
                uname,
                "12345"
        );

        usersClient.addIncomeInvitation(user, 1);
        usersClient.addOutcomeInvitation(user, 1);
    }
}