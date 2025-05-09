package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class EditSpendingPage {

    private final SelenideElement descriptionInput = $("#description");
    private final SelenideElement submitBtn = $("#save");
    private final SelenideElement amountInput = $("#amount");

    public EditSpendingPage editDescription(String description) {
        descriptionInput.clear();
        descriptionInput.setValue(description);
        return this;
    }

    public EditSpendingPage save() {
        submitBtn.click();
        return this;
    }

    public EditSpendingPage setNewSpendingAmount(double amount) {
        amountInput.clear();
        amountInput.setValue(String.valueOf(amount));
        return this;
    }

    public EditSpendingPage saveSpending() {
        submitBtn.click();
        return this;
    }
}
