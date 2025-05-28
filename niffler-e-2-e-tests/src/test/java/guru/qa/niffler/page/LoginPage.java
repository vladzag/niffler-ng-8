package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.config.Config;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class LoginPage {
    public static final String URL = Config.getInstance().authUrl() + "login";


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

    @Step("Регистрация нового пользователя")
    public void doRegister() {
        registerButton.click();
    }

    @Step("Успешный вход в систему для пользователя {username}")
    public LoginPage successLogin(String username, String password) {
        login(username, password);
        return this;
    }

    @Step("Вход в систему с использованием имени пользователя {username} и пароля")
    public LoginPage login(String username, String password) {
        usernameInput.setValue(username);
        passwordInput.setValue(password);
        submitButton.click();
        return this;
    }

    @Step("Проверка ошибки: {error}")
    public LoginPage checkError(String error) {
        errorContainer.shouldHave(text(error));
        return this;
    }

    @Step("Проверка загрузки страницы авторизации")
    public LoginPage checkThatPageLoaded() {
        usernameInput.should(visible);
        passwordInput.should(visible);
        return this;
    }
}
