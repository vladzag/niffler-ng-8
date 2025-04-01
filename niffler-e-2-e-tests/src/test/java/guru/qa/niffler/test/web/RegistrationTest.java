package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.Test;

import static guru.qa.niffler.randomUtils.RandomGenerator.generateRandomLogin;
import static guru.qa.niffler.randomUtils.RandomGenerator.randomAlphanumeric5SymbolString;

public class RegistrationTest {

    private static final Config CFG = Config.getInstance();

    @Test
    void shouldRegisterNewUser() {
        String newUsername = generateRandomLogin();
        String password = randomAlphanumeric5SymbolString();
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doRegister()
                .fillRegisterPage(newUsername, password, password)
                .successSubmit()
                .successLogin(newUsername, password)
                .checkThatPageLoaded();
    }

    @Test
    void shouldNotRegisterUserWithExistingUsername() {
        String existingUsername = "vladzag";
        String password = randomAlphanumeric5SymbolString();

        LoginPage loginPage = Selenide.open(CFG.frontUrl(), LoginPage.class);
        loginPage.doRegister()
                .fillRegisterPage(existingUsername, password, password)
                .submit();
        loginPage.checkError("Username `" + existingUsername + "` already exists");
    }

    @Test
    void shouldShowErrorIfPasswordAndConfirmPasswordAreNotEqual() {
        String newUsername = generateRandomLogin();
        String password = randomAlphanumeric5SymbolString();

        LoginPage loginPage = Selenide.open(CFG.frontUrl(), LoginPage.class);
        loginPage.doRegister()
                .fillRegisterPage(newUsername, password, "bad password submit")
                .submit();
        loginPage.checkError("Passwords should be equal");
    }
}