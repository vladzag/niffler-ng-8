package guru.qa.niffler.page.universalComponents;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.EditSpendingPage;

import static com.codeborne.selenide.ClickOptions.usingJavaScript;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static org.openqa.selenium.Keys.ENTER;

public class SpendingTable {

    private final SelenideElement periodMenu = $("#period");
    private final SelenideElement currencyMenu = $("#currency");
    private final ElementsCollection menuItems = $$(".MuiList-padding li");
    private final SelenideElement deleteBtn = $("#delete");
    private final SelenideElement popup = $("div[role='dialog']");

    private final SelenideElement tableHeader = $(".MuiTableHead-root");
    private final ElementsCollection headerCells = tableHeader.$$(".MuiTableCell-root");

    private final ElementsCollection tableRows = $("tbody").$$("tr");

    private final SelenideElement searchField = $("input[aria-label='search']");

    public EditSpendingPage editSpending(String description) {
        searchInField(description);
        SelenideElement row = tableRows.find(text(description));
        row.$$("td").get(5).click();
        return new EditSpendingPage();
    }

    public SpendingTable deleteSpending(String description) {
        searchInField(description);
        SelenideElement row = tableRows.find(text(description));
        row.$$("td").get(0).click();
        deleteBtn.click();
        popup.$(byText("Delete")).click(usingJavaScript());
        return this;
    }

    private void searchInField(String description) {
        searchField.clear();
        searchField.setValue(description).sendKeys(ENTER);
    }

    public SpendingTable checkTableSize(int expectedSize) {
        tableRows.should(size(expectedSize));
        return this;
    }
}
