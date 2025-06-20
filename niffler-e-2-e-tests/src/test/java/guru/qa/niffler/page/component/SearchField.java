package guru.qa.niffler.page.component;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.not;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class SearchField extends BaseComponent<SearchField> {

    public SearchField(@Nonnull SelenideElement self) {
        super(self);
    }

    public SearchField() {
        super($("input[aria-label='search']"));
    }

    private final SelenideElement clearSearchInputBtn = $("#input-clear");

    @Nonnull
    @Step("Search data in table by value '{0}'")
    public SearchField search(String query) {
        clearIfNotEmpty();
        self.setValue(query).pressEnter();
        return this;
    }

    @Nonnull
    @Step("Clear search field")
    public SearchField clearIfNotEmpty() {
        if (self.is(not(empty))) {
            clearSearchInputBtn.click();
            self.should(empty);
        }
        return this;
    }
}