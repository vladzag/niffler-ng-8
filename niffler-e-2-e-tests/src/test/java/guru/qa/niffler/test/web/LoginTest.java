package guru.qa.niffler.test.web;

import com.codeborne.selenide.SelenideDriver;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.converter.BrowserConverter;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.utils.Browser;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.EnumSource;

import static com.codeborne.selenide.Condition.text;
import static guru.qa.niffler.utils.RandomDataUtils.randomUsername;

public class LoginTest {


    private static final Config CFG = Config.getInstance();

    @RegisterExtension
    private static final BrowserExtension browserExtension = new BrowserExtension();

    @ParameterizedTest
    @EnumSource(Browser.class)
    void mainPageShouldBeDisplayedAfterSuccessLogin(@ConvertWith(BrowserConverter.class) SelenideDriver driver) {
        browserExtension.add(driver);
        driver.open(LoginPage.URL);

        new LoginPage(driver)
                .successLogin("duck", "12345")
                .checkThatPageLoaded();
    }

    @ParameterizedTest
    @EnumSource(Browser.class)
    void userShouldStayOnLoginPageAfterLoginWithBadCredentials(@ConvertWith(BrowserConverter.class) SelenideDriver driver) {
        browserExtension.add(driver);
        driver.open(LoginPage.URL);
        new LoginPage(driver).login(randomUsername(), "BAD").checkError("Bad credentials");

        driver.$(".logo-section__text").should(text("Niffler"));

    }
}
