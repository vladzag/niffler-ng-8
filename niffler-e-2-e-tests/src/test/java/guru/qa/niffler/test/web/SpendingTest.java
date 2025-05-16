package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.condition.Bubble;
import guru.qa.niffler.condition.Colour;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.ScreenShotTest;
import guru.qa.niffler.jupiter.annotation.Spend;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.page.universalComponents.SpendingTable;
import guru.qa.niffler.page.universalComponents.StatComponent;
import guru.qa.niffler.utils.ScreenDiffResult;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;

@WebTest
public class SpendingTest {

    private static final Config CFG = Config.getInstance();

    @User(
            spendings = @Spend(
                    category = "Обучение",
                    description = "Обучение Advanced 2.0",
                    amount = 79990,
                    currency = CurrencyValues.RUB
            )
    )
    @Test
    void categoryDescriptionShouldBeChangedFromTable(UserJson userJson) {
        final String newDescription = "Обучение Niffler Next Generation";

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .successLogin(userJson.username(), userJson.testData().password())
                .editSpending(userJson.testData().spendings().getFirst().description())
                .editDescription(newDescription)
                .save();

        new MainPage().checkThatTableContainsSpending(newDescription);
    }

//    @User(
//            spendings = @Spend(
//                    category = "Обучение", description = "Обучение Advanced 2.0", amount = 79990, currency = CurrencyValues.RUB)
//    )
//    @ScreenShotTest("img/expected-stat.png")
//    void checkStatComponentTest(UserJson user, BufferedImage expected) throws IOException {
//        Selenide.open(CFG.frontUrl(), LoginPage.class)
//                .fillLoginPage(user.username(), user.testData().password())
//                .submit(new MainPage());
//
//        BufferedImage actual = ImageIO.read($("canvas[role='img']").screenshot());
//        assertFalse(new ScreenDiffResult(
//                expected,
//                actual
//        ));
//    }

    @ScreenShotTest("img/expected/expected-stat.png")
    void checkStatComponentTest(UserJson user, BufferedImage expected) throws IOException {
        MainPage mainPage = Selenide.open(CFG.authUrl(), LoginPage.class)
                .login(user.username(), user.testData().password());

        mainPage.statComponent()
                .waitForPieChartToLoad();

        BufferedImage actual = ImageIO.read(mainPage.statComponent().pieChartImage().screenshot());

        assertFalse(new ScreenDiffResult(
                        expected,
                        actual),
                "Screen comparison failure"
        );

        mainPage.getStatComponent().checkBubbles(new Bubble(Colour.yellow, "Обучение"));
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
    @Test
    void checkBubblesTest(UserJson user) throws InterruptedException {
        StatComponent statComponent = Selenide.open(CFG.authUrl(), LoginPage.class)
                .login(user.username(), user.testData().password())
                .getStatComponent();

        statComponent.checkBubbles(
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
    @Test
    void checkBubblesInAnyOderTest(UserJson user) {
        StatComponent statComponent = Selenide.open(CFG.authUrl(), LoginPage.class)
                .login(user.username(), user.testData().password())
                .getStatComponent();


        statComponent.checkBubblesInAnyOrder(
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
    @Test
    void checkBubblesContainsTest(UserJson user) {
        StatComponent statComponent = Selenide.open(CFG.authUrl(), LoginPage.class)
                .login(user.username(), user.testData().password())
                .getStatComponent();


        statComponent.checkBubblesContains(
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
    @Test
    void spendingTableShouldContainInfo(UserJson user)  {
        SpendingTable spendings = Selenide.open(CFG.authUrl(), LoginPage.class)
                .login(user.username(), user.testData().password())
                .getSpendingTable();

        spendings.checkSpendingTable(
                user.testData().spendings().toArray(SpendJson[]::new)
        );    }

    @User(
            spendings = @Spend(
                    category = "Обучение", description = "Обучение Advanced 2.0", amount = 79990, currency = CurrencyValues.RUB)
    )
    @ScreenShotTest("img/expected/expected-empty-spendings.png")
    void shouldUpdateStatAfterSpendingIsRemoved(UserJson user, BufferedImage expected) throws IOException {
        MainPage mainPage = Selenide.open(CFG.authUrl(), LoginPage.class)
                .login(user.username(), user.testData().password());

        mainPage
                .getSpendingTable()
                .deleteSpending("Обучение Advanced 2.0")
                .checkTableSize(0);

        Selenide.refresh();

        BufferedImage actual = ImageIO.read(
                mainPage.statComponent().pieChartImage().screenshot()
        );
        assertFalse(new ScreenDiffResult(
                expected,
                actual
        ));
    }

    @User(
            spendings = @Spend(
                    category = "Обучение", description = "Обучение Advanced 2.0", amount = 79990, currency = CurrencyValues.RUB)
    )
    @ScreenShotTest("img/expected/expected-updated-spending.png")
    void shouldUpdateStatAfterSpendingIsUpdated(UserJson user, BufferedImage expected) throws IOException {
        final int newAmount = 5000;
        MainPage mainPage = Selenide.open(CFG.authUrl(), LoginPage.class)
                .login(user.username(), user.testData().password());

        mainPage
                .getSpendingTable()
                .editSpending("Обучение Advanced 2.0")
                .setNewSpendingAmount(newAmount)
                .saveSpending();

        mainPage.statComponent()
                .waitForPieChartToLoad()
                .checkBubblesHasText("Обучение " + newAmount);

        BufferedImage actual = ImageIO.read(
                mainPage.statComponent().pieChartImage().screenshot()
        );
        assertFalse(new ScreenDiffResult(
                expected,
                actual
        ));
    }

    @User(
            spendings = @Spend(
                    category = "Обучение", description = "Обучение Advanced 2.0", amount = 79990, currency = CurrencyValues.RUB)
    )
    @ScreenShotTest("img/expected/expected-stat.png")
    void shouldUpdateStatAfterCategoryIsArchived(UserJson user, BufferedImage expected) throws IOException {
        MainPage mainPage = Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.testData().password());

        mainPage
                .goToProfilePage()
                .updateCategory("Обучение");

        Selenide.open(CFG.frontUrl(), MainPage.class)
                .statComponent()
                .waitForPieChartToLoad()
                .checkBubblesHasText("Archived " + "79990");

        BufferedImage actual = ImageIO.read(
                mainPage.statComponent().pieChartImage().screenshot()
        );
        assertFalse(new ScreenDiffResult(
                expected,
                actual
        ));
    }

    @User(
            spendings = @Spend(
                    category = "Обучение", description = "Обучение Advanced 2.0", amount = 50000, currency = CurrencyValues.RUB)
    )
    @ScreenShotTest(
            value = "img/expected/expected-stat.png",
            rewriteExpected = true
    )
    void overwriteScreenshotTest(UserJson user, BufferedImage expected) throws IOException {
        MainPage mainPage = Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.testData().password());

        mainPage.statComponent()
                .waitForPieChartToLoad();

        BufferedImage actual = ImageIO.read(mainPage.statComponent().pieChartImage().screenshot());
        assertFalse(new ScreenDiffResult(
                actual,
                expected
        ));
    }

}

