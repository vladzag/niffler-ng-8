package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.config.Config;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;

public class RegisterPage extends BasePage {
    public static final String URL = Config.getInstance().authUrl() + "register";

    private final SelenideElement usernameInput;
    private final SelenideElement passwordInput;
    private final SelenideElement passwordSubmitInput;
    private final SelenideElement submitButton;
    private final SelenideElement proceedLoginButton;
    private final SelenideElement errorContainer;

    public RegisterPage(SelenideDriver driver) {
        this.usernameInput = driver.$("input[name='username']");
        this.passwordInput = driver.$("input[name='password']");
        this.passwordSubmitInput = driver.$("input[name='passwordSubmit']");
        this.submitButton = driver.$("button[type='submit']");
        this.proceedLoginButton = driver.$(".form_sign-in");
        this.errorContainer = driver.$(".form__error");
    }

    @Step("Заполнение формы регистрации пользователем с логином: {0}, паролем: {1} и подтверждением пароля: {2}")
    public RegisterPage fillRegisterPage(String login, String password, String passwordSubmit) {
        usernameInput.setValue(login);
        passwordInput.setValue(password);
        passwordSubmitInput.setValue(passwordSubmit);
        return this;
    }

    @Step("Успешное подтверждение регистрации")
    public void successSubmit() {
        submit();
        proceedLoginButton.click();
    }

    @Step("Подтверждение данных формы регистрации")
    public void submit() {
        submitButton.click();
    }

    @Step("Проверка наличия сообщения об ошибке: {0}")
    public RegisterPage checkAlertMessage(String errorMessage) {
        errorContainer.shouldHave(text(errorMessage));
        return this;
    }

}
