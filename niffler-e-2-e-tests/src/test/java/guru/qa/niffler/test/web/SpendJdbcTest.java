package guru.qa.niffler.test.web;

import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.SpendDbClient;
import guru.qa.niffler.service.UsersDbClient;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class SpendJdbcTest {
    private static final String USER_PW = "12345";
    private static final UsersDbClient usersDbClient = new UsersDbClient();
    private static final SpendDbClient spendDbClient = new SpendDbClient();

    @Test
    void createSpendAndCategoryTest() {
        String categoryName = RandomDataUtils.randomCategoryName();
        String username = RandomDataUtils.randomUsername();
        UserJson user = usersDbClient.createUser(username, USER_PW);

        SpendJson spend = spendDbClient.createSpend(
                new SpendJson(
                        null,
                        new Date(),
                        new CategoryJson(
                                null,
                                categoryName,
                                user.username(),
                                false
                        ),
                        CurrencyValues.RUB,
                        1000.0,
                        "Spend creation test",
                        user.username()
                )
        );

        assertTrue(spendDbClient.findByUsernameAndSpendDescription(spend).isPresent());
        assertTrue(spendDbClient.findCategoryByUsernameAndName(spend.category()).isPresent());
    }

    @Test
    void updateSpendAndCategoryTest() {
        String categoryName = RandomDataUtils.randomCategoryName();
        String username = RandomDataUtils.randomUsername();
        double newAmount = 5000.0;
        String newDesc = "Spend updating test";
        String newCatName = "category-updated";
        UserJson user = usersDbClient.createUser(username, USER_PW);

        SpendJson initialSpend = spendDbClient.createSpend(
                new SpendJson(
                        null,
                        new Date(),
                        new CategoryJson(
                                null,
                                categoryName,
                                user.username(),
                                false
                        ),
                        CurrencyValues.RUB,
                        1000.0,
                        "Spend creation test",
                        user.username()
                )
        );

        SpendJson newSpendInfo = new SpendJson(
                initialSpend.id(),
                initialSpend.spendDate(),
                new CategoryJson(
                        initialSpend.category().id(),
                        newCatName,
                        user.username(),
                        true
                ),
                CurrencyValues.USD,
                newAmount,
                newDesc,
                user.username()
        );

        SpendJson updatedSpend = spendDbClient.update(newSpendInfo);

        assertEquals(CurrencyValues.USD, updatedSpend.currency());
        assertEquals(newAmount, updatedSpend.amount());
        assertEquals(newDesc, updatedSpend.description());
        assertEquals(newCatName, updatedSpend.category().name());
        assertTrue(updatedSpend.category().archived());
    }

    @Test
    void removeSpendTest() {
        String username = RandomDataUtils.randomUsername();
        String categoryName = RandomDataUtils.randomCategoryName();
        UserJson user = usersDbClient.createUser(
                username,
                USER_PW
        );

        SpendJson createdSpend = spendDbClient.createSpend(
                new SpendJson(
                        null,
                        new Date(),
                        new CategoryJson(
                                null,
                                categoryName,
                                user.username(),
                                false
                        ),
                        CurrencyValues.RUB,
                        1000.0,
                        "Spend remove test",
                        user.username()
                )
        );

        spendDbClient.removeSpend(createdSpend);

        assertFalse(spendDbClient.findSpendById(createdSpend.id()).isPresent());
    }

    @Test
    void removeCategoryTest() {
        String username = RandomDataUtils.randomUsername();
        String categoryName = RandomDataUtils.randomCategoryName();
        UserJson user = usersDbClient.createUser(
                username,
                USER_PW
        );

        CategoryJson createdCategory = spendDbClient.createCategory(
                new CategoryJson(
                        null,
                        categoryName,
                        user.username(),
                        false
                )
        );

        spendDbClient.removeCategory(createdCategory);
        assertFalse(spendDbClient.findCategoryById(createdCategory.id()).isPresent());
    }
}
