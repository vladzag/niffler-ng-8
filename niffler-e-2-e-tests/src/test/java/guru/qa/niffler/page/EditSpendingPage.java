package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Selenide.$;

public class EditSpendingPage extends BasePage {

    private final SelenideElement descriptionInput;
    private final SelenideElement submitBtn;
    private final SelenideElement amountInput;

    public EditSpendingPage(SelenideDriver driver) {
        this.descriptionInput = $("#description");
        this.submitBtn = $("#save");
        this.amountInput = $("#amount");
    }

    @Step("Редактируем описание {description}")
    public EditSpendingPage editDescription(String description) {
        descriptionInput.clear();
        descriptionInput.setValue(description);
        return this;
    }

    @Step("Сохраняем изменения")
    public EditSpendingPage save() {
        submitBtn.click();
        return this;
    }

    @Step("Создаём новую трату на {amount}")
    public EditSpendingPage setNewSpendingAmount(double amount) {
        amountInput.clear();
        amountInput.setValue(String.valueOf(amount));
        return this;
    }

    @Step("Сохраняем трату")
    public EditSpendingPage saveSpending() {
        submitBtn.click();
        return this;
    }
}
