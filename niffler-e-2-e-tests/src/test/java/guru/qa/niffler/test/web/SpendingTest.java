package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.ScreenShotTest;
import guru.qa.niffler.jupiter.annotation.Spend;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.utils.ScreenDiffResult;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static com.codeborne.selenide.Selenide.$;
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
}

