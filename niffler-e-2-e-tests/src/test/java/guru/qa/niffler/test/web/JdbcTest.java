package guru.qa.niffler.test.web;

import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.SpendDbClient;
import guru.qa.niffler.service.UsersDbClient;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.util.Date;

public class JdbcTest {

    @Test
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


    @Test
    void springJdbcTest() {
        UsersDbClient usersDbClient = new UsersDbClient();
        String username = RandomDataUtils.randomUsername();

        UserJson user = usersDbClient.createUserJdbcWithoutTx(
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
                        /*
                        При передаче значения null в качестве username в метод UdUserDaoSpringJdbc,
                         вызов ps.setString(1, null) приведет к ошибке при создании пользователя.
                         Однако, несмотря на эту ошибку, записи в таблицах user-auth и authorities-auth всё равно будут созданы.

                         Это указывает на то, что транзакция не была откатана после ошибки в userdata,
                         что демонстрирует невозможность отката внутренней транзакции при сбое во внешней.
                         */
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
}
