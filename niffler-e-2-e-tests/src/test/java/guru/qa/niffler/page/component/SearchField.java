package guru.qa.niffler.page.component;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.not;
import static com.codeborne.selenide.Selenide.$;

public class SearchField {

    private final SelenideElement self = $("input[aria-label='search']");

    @Step("В поиск вводим '{query}'")
    public void search(String query) {
        clearIfNotEmpty();
        self.setValue(query).pressEnter();
    }

    void clearIfNotEmpty() {
        if (self.is(not(empty))) {
            self.clear();
        }
    }
}