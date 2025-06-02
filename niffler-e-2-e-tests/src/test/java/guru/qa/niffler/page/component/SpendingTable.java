package guru.qa.niffler.page.component;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.condition.SpendConditions;
import guru.qa.niffler.model.DateAndTimePeriods;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.page.EditSpendingPage;
import io.qameta.allure.Step;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static com.codeborne.selenide.ClickOptions.usingJavaScript;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.openqa.selenium.Keys.ENTER;

public class SpendingTable {

    private final SelenideElement self = $("#spendings tbody");
    private final SelenideElement periodMenu = $("#period");
    private final SelenideElement currencyMenu = $("#currency");
    private final ElementsCollection menuItems = $$(".MuiList-padding li");
    private final SelenideElement deleteBtn = $("#delete");
    private final SelenideElement popup = $("div[role='dialog']");

    private final SelenideElement tableHeader = $(".MuiTableHead-root");
    private final ElementsCollection headerCells = tableHeader.$$(".MuiTableCell-root");

    private final ElementsCollection tableRows = $("tbody").$$("tr");

    private final SearchField searchField = new SearchField();
    private final ElementsCollection dropdownList = $$("ul[role='listbox']");

    @Step("Редактируем трату с описанием '{description}'")
    public void editSpending(String description) {
        searchInField(description);
        SelenideElement row = tableRows.find(text(description));
        row.$$("td").get(5).click();
    }

    @Step("Удаляем трату с описанием '{description}'")
    public SpendingTable deleteSpending(String description) {
        searchInField(description);
        SelenideElement row = tableRows.find(text(description));
        row.$$("td").get(0).click();
        deleteBtn.click();
        popup.$(byText("Delete")).click(usingJavaScript());
        return this;
    }

    private void searchInField(String description) {
        searchField.clearIfNotEmpty();
        searchField.search(description);
    }

    @Step("Проверяем количество трат в таблице - {expectedSize}")
    public SpendingTable checkTableSize(int expectedSize) {
        tableRows.should(size(expectedSize));
        return this;
    }

    @Step("Проверяем наличие трат в таблице - {expectedSpends}")
    public SpendingTable checkSpendingTable(SpendJson... expectedSpends) {
        tableRows.should(SpendConditions.spends(expectedSpends));
        return this;
    }

    @Step("Выбираем период '{period}' в таблице трат")
    public void selectPeriod(DateAndTimePeriods period) {
        periodMenu.click();
        dropdownList.find(text(period.name())).click();
    }


    @Step("Редактируем трату с описанием '{description}'")
    public void searchSpendingByDescription(String description) {
        searchField.search(description);
    }

    @Step("Проверяем наличие траты с категорией '{categoryName}' и с описанием - '{description}'")
    public void checkSpendingWith(String categoryName, String description) {
        searchField.search(description);
        tableRows.find(text(description)).$$("td").get(1).shouldHave(text(categoryName));
    }

    @Step("Проверяем наличие траты с описанием - '{spendingDescription}'")
    public void checkTableContainsSpending(String spendingDescription) {
        searchSpendingByDescription(spendingDescription);
        tableRows.find(text(spendingDescription)).shouldBe(visible);
    }

    @Step("Проверяем наличие трат по описанию - '{expectedSpends}'")
    public void checkTableContains(String... expectedSpends) {
        for (String spend: expectedSpends){
            tableRows.findBy(text(spend)).shouldBe(visible);
        }

//        Set<String> actualSpends = tableRows
//                .stream()
//                .map(SelenideElement::getText).collect(Collectors.toSet());
//        assertTrue(
//                Arrays.stream(expectedSpends).collect(Collectors.toSet())
//                        .containsAll(actualSpends)
//        );
    }
}
