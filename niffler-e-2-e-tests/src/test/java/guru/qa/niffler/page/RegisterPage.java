package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

public class RegisterPage {

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
    this.errorContainer = driver.$(".form__error");}

  public RegisterPage fillRegisterPage(String login, String password, String passwordSubmit) {
    usernameInput.setValue(login);
    passwordInput.setValue(password);
    passwordSubmitInput.setValue(passwordSubmit);
    return this;
  }

  public void successSubmit() {
    submit();
    proceedLoginButton.click();
  }

  public void submit() {
    submitButton.click();
  }

  public RegisterPage checkAlertMessage(String errorMessage) {
    errorContainer.shouldHave(text(errorMessage));
    return this;
  }
}
