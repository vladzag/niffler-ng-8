package guru.qa.niffler.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public abstract class BasePage<T extends BasePage<?>> {

    private final SelenideElement alert = $(".MuiAlert-message");

    public T checkAlertMessage(String text) {
        alert.should(Condition.text(text));
        return (T) this;
    }

    ;
}
