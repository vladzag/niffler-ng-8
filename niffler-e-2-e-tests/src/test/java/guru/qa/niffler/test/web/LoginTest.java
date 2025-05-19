package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideDriver;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.utils.SelenideUtils;
import org.junit.jupiter.api.Test;

import static guru.qa.niffler.utils.RandomDataUtils.randomUsername;

@WebTest
public class LoginTest {

    private static final Config CFG = Config.getInstance();
    private final SelenideDriver driverChrome = new SelenideDriver(SelenideUtils.chromeConfig);

    @Test
    void mainPageShouldBeDisplayedAfterSuccessLogin() {
        SelenideDriver firefox = new SelenideDriver(SelenideUtils.firefoxConfig);

        driverChrome.open(CFG.frontUrl());
        firefox.open(CFG.frontUrl());

        new LoginPage(driverChrome)
                .successLogin("duck", "12345")
                .checkThatPageLoaded();
    }

    @Test
    void userShouldStayOnLoginPageAfterLoginWithBadCredentials() {
        LoginPage loginPage = Selenide.open(CFG.frontUrl(), LoginPage.class);
        loginPage.login(randomUsername(), "BAD");
        loginPage.checkError("Bad credentials");
    }
}
