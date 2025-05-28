package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideDriver;
import guru.qa.niffler.condition.Bubble;
import guru.qa.niffler.condition.Colour;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.ScreenShotTest;
import guru.qa.niffler.jupiter.annotation.Spend;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.jupiter.converter.BrowserConverter;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.EditSpendingPage;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.page.ProfilePage;
import guru.qa.niffler.page.component.SpendingTable;
import guru.qa.niffler.utils.Browser;
import guru.qa.niffler.utils.SelenideUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.EnumSource;

import java.awt.image.BufferedImage;
import java.io.IOException;

import static guru.qa.niffler.test.web.FriendsTest.USER_PW;

@WebTest
public class SpendingTest {

    private static final Config CFG = Config.getInstance();
    private final SelenideDriver driver = new SelenideDriver(SelenideUtils.chromeConfig);


    @User(
            spendings = @Spend(
                    category = "Обучение",
                    description = "Обучение Advanced 2.0",
                    amount = 79990,
                    currency = CurrencyValues.RUB
            )
    )
    @ParameterizedTest
    @EnumSource(Browser.class)
    void categoryDescriptionShouldBeChangedFromTable(@ConvertWith(BrowserConverter.class) SelenideDriver driver, UserJson userJson) {
        final String newDescription = "Обучение Niffler Next Generation";

        driver.open(CFG.frontUrl());
        new LoginPage(driver)
                .successLogin(userJson.username(), userJson.testData().password());

        new MainPage(driver).editSpending(userJson.testData().spendings().getFirst().description());
        new EditSpendingPage(driver)
                .editDescription(newDescription)
                .save();

        new MainPage(driver).checkThatTableContainsSpending(newDescription);
    }

    @ScreenShotTest("img/expected/expected-stat.png")
    @ParameterizedTest
    @EnumSource(Browser.class)
    void checkStatComponentTest(@ConvertWith(BrowserConverter.class) SelenideDriver driver, UserJson user, BufferedImage expected) throws IOException {
        driver.open(CFG.authUrl());
        new LoginPage(driver)
                .login(user.username(), user.testData().password());

        new MainPage(driver).getStatComponent()
                .waitForPieChartToLoad()
                .checkBubblesContainsText("Обучение 79990 ₽")
                .checkStatisticImage(expected)
                .checkBubbles(Colour.yellow);
    }

    @User(
            spendings =
                    {
                            @Spend(
                                    category = "Обучение", description = "Обучение Advanced 2.0", amount = 79990, currency = CurrencyValues.RUB),
                            @Spend(
                                    category = "Рыбалка",
                                    description = "Рыбалка на Неве",
                                    amount = 1000,
                                    currency = CurrencyValues.RUB
                            )
                    }
    )
    @ParameterizedTest
    @EnumSource(Browser.class)
    void checkBubblesTest(@ConvertWith(BrowserConverter.class) SelenideDriver driver, UserJson user) throws InterruptedException {
        driver.open(CFG.authUrl());
        new LoginPage(driver)
                .login(user.username(), user.testData().password());

        new MainPage(driver).getStatComponent()
                .waitForPieChartToLoad()
                .checkBubbles(
                        new Bubble(Colour.yellow, "Обучение 79990 ₽"),
                        new Bubble(Colour.green, "Рыбалка 1000 ₽")
                );
    }

    @User(
            spendings =
                    {
                            @Spend(
                                    category = "Обучение", description = "Обучение Advanced 2.0", amount = 79990, currency = CurrencyValues.RUB),
                            @Spend(
                                    category = "Рыбалка", description = "Рыбалка на Неве", amount = 1000, currency = CurrencyValues.RUB)
                            ,
                            @Spend(
                                    category = "Активность", description = "Прыжок с парашютом", amount = 500, currency = CurrencyValues.RUB)
                    }
    )
    @ParameterizedTest
    @EnumSource(Browser.class)
    void checkBubblesInAnyOderTest(@ConvertWith(BrowserConverter.class) SelenideDriver driver, UserJson user) {
        driver.open(CFG.authUrl());
        new LoginPage(driver)
                .login(user.username(), user.testData().password());

        new MainPage(driver).getStatComponent()
                .waitForPieChartToLoad()
                .checkBubblesInAnyOrder(
                        new Bubble(Colour.orange, "Рыбалка 1000 ₽"),
                        new Bubble(Colour.yellow, "Обучение 79990 ₽"),
                        new Bubble(Colour.green, "Активность 500 ₽")
                );
    }

    @User(
            spendings =
                    {
                            @Spend(
                                    category = "Обучение", description = "Обучение Advanced 2.0", amount = 79990, currency = CurrencyValues.RUB),
                            @Spend(
                                    category = "Рыбалка",
                                    description = "Рыбалка на Неве",
                                    amount = 1000, currency = CurrencyValues.RUB),
                            @Spend(
                                    category = "Активность",
                                    description = "Прыжок с парашютом",
                                    amount = 500, currency = CurrencyValues.RUB),
                            @Spend(
                                    category = "Животные",
                                    description = "Собачий корм",
                                    amount = 3000, currency = CurrencyValues.RUB),
                    }
    )
    @ParameterizedTest
    @EnumSource(Browser.class)
    void checkBubblesContainsTest(@ConvertWith(BrowserConverter.class) SelenideDriver driver, UserJson user) {
        driver.open(CFG.authUrl());
        new LoginPage(driver)
                .login(user.username(), user.testData().password());

        new MainPage(driver).getStatComponent()
                .waitForPieChartToLoad()
                .checkBubblesContains(
                        new Bubble(Colour.yellow, "Обучение 79990 ₽"),
                        new Bubble(Colour.blue, "Рыбалка 1000 ₽")
                );
    }

    @User(
            spendings =
                    {
                            @Spend(
                                    category = "Обучение", description = "Обучение Advanced 2.0", amount = 79990, currency = CurrencyValues.RUB),
                            @Spend(
                                    category = "Рыбалка",
                                    description = "Рыбалка на Неве",
                                    amount = 1000, currency = CurrencyValues.RUB),
                            @Spend(
                                    category = "Активность",
                                    description = "Прыжок с парашютом",
                                    amount = 500, currency = CurrencyValues.RUB),
                            @Spend(
                                    category = "Животные",
                                    description = "Собачий корм",
                                    amount = 3000, currency = CurrencyValues.RUB),
                    }
    )
    @ParameterizedTest
    @EnumSource(Browser.class)
    void spendingTableShouldContainInfo(@ConvertWith(BrowserConverter.class) SelenideDriver driver, UserJson user) {
        driver.open(CFG.authUrl());
        new LoginPage(driver)
                .login(user.username(), user.testData().password());
        new MainPage(driver)
                .getSpendingTable()
                .editSpending("Обучение Advanced 2.0");


        new SpendingTable().checkSpendingTable(
                user.testData().spendings().toArray(SpendJson[]::new)
        );
    }

    @User(
            spendings = @Spend(
                    category = "Обучение", description = "Обучение Advanced 2.0", amount = 79990, currency = CurrencyValues.RUB)
    )
    @ScreenShotTest("img/expected/expected-empty-spendings.png")
    @ParameterizedTest
    @EnumSource(Browser.class)
    void shouldUpdateStatAfterSpendingIsRemoved(@ConvertWith(BrowserConverter.class) SelenideDriver driver, UserJson user, BufferedImage expected) throws IOException {
        driver.open(CFG.authUrl());
        new LoginPage(driver).login(user.username(), user.testData().password());

        new MainPage(driver)
                .getSpendingTable()
                .deleteSpending("Обучение Advanced 2.0")
                .checkTableSize(0);

        Selenide.refresh();

        new MainPage(driver).getStatComponent()
                .waitForPieChartToLoad()
                .checkBubblesContains(
                        null);
    }

    @User(
            spendings = @Spend(
                    category = "Обучение", description = "Обучение Advanced 2.0", amount = 79990, currency = CurrencyValues.RUB)
    )
    @ScreenShotTest("img/expected/expected-updated-spending.png")
    @ParameterizedTest
    @EnumSource(Browser.class)
    void shouldUpdateStatAfterSpendingIsUpdated(@ConvertWith(BrowserConverter.class) SelenideDriver driver, UserJson user, BufferedImage expected) throws IOException {
        final int newAmount = 5000;
        driver.open(CFG.authUrl());
        new LoginPage(driver).login(user.username(), user.testData().password());
        new MainPage(driver)
                .getSpendingTable()
                .editSpending("Обучение Advanced 2.0");
        new EditSpendingPage(driver)
                .setNewSpendingAmount(newAmount)
                .saveSpending();

        new MainPage(driver).statComponent()
                .waitForPieChartToLoad()
                .checkBubblesHasText("Обучение " + newAmount)
                .checkStatisticImage(expected);
    }

    @User(
            spendings = @Spend(
                    category = "Обучение", description = "Обучение Advanced 2.0", amount = 79990, currency = CurrencyValues.RUB)
    )
    @ScreenShotTest("img/expected/expected-stat.png")
    @ParameterizedTest
    @EnumSource(Browser.class)
    void shouldUpdateStatAfterCategoryIsArchived(@ConvertWith(BrowserConverter.class) SelenideDriver driver, UserJson user, BufferedImage expected) throws IOException {
        driver.open(CFG.authUrl());
        new LoginPage(driver)
                .login(user.username(), user.testData().password());

        new MainPage(driver)
                .goToProfilePage();
        new ProfilePage(driver)
                .updateCategory("Обучение");

        driver.open(CFG.frontUrl(), MainPage.class)
                .statComponent()
                .waitForPieChartToLoad()
                .checkBubblesHasText("Archived " + "79990")
                .checkStatisticImage(expected);

    }

    @User(
            spendings = @Spend(
                    category = "Обучение", description = "Обучение Advanced 2.0", amount = 50000, currency = CurrencyValues.RUB)
    )
    @ScreenShotTest(
            value = "img/expected/expected-stat.png",
            rewriteExpected = true
    )
    @ParameterizedTest
    @EnumSource(Browser.class)
    void overwriteScreenshotTest(@ConvertWith(BrowserConverter.class) SelenideDriver driver, UserJson user, BufferedImage expected) throws IOException {
        driver.open(CFG.authUrl());
        new LoginPage(driver)
                .login(user.username(), user.testData().password());
        new MainPage(driver).statComponent()
                .waitForPieChartToLoad()
                .checkStatisticImage(expected);
    }

    @User(
            spendings = {
                    @Spend(
                            category = "Обучение", description = "Обучение Advanced 2.0", amount = 79990, currency = CurrencyValues.RUB)
            }
    )
    @ParameterizedTest
    @EnumSource(Browser.class)
    void createSpendingTest(@ConvertWith(BrowserConverter.class) SelenideDriver driver, UserJson user) {
        driver.open(CFG.frontUrl());
        new LoginPage(driver)
                .successLogin(user.username(), USER_PW);
        new SpendingTable()
                .checkTableContainsSpending("Обучение Advanced 2.0");

    }
}

