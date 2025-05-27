package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class LoginPage {

    private final SelenideElement usernameInput;
    private final SelenideElement passwordInput;
    private final SelenideElement submitButton ;
    private final SelenideElement registerButton;
    private final SelenideElement errorContainer;

    public LoginPage(SelenideDriver driver) {
        this.usernameInput = driver.$("input[name='username']");
        this.passwordInput = driver.$("input[name='password']");
        this.submitButton = driver.$("button[type='submit']");
        this.registerButton = driver.$("a[href='/register']");
        this.errorContainer = driver.$(".form__error");

    }


    public void doRegister() {
        registerButton.click();
    }

    public LoginPage successLogin(String username, String password) {
        login(username, password);
        return this;
    }

    public LoginPage login(String username, String password) {
        usernameInput.setValue(username);
        passwordInput.setValue(password);
        submitButton.click();
        return this;
    }

    public LoginPage checkError(String error) {
        errorContainer.shouldHave(text(error));
        return this;
    }

    public LoginPage checkThatPageLoaded() {
        usernameInput.should(visible);
        passwordInput.should(visible);
        return this;
    }
}
