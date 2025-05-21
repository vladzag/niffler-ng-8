package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.universalComponents.SpendingTable;
import guru.qa.niffler.page.universalComponents.StatComponent;
import org.apache.kafka.common.metrics.Stat;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class MainPage {

    private final SelenideElement header;
    private final SelenideElement headerMenu;
    private final ElementsCollection tableRows;
    private final SelenideElement statComponent;
    private final SelenideElement spendingTable;
    private final SelenideElement searchInput;

    public MainPage(SelenideDriver driver) {
        this.header = $("#root header");
        this.headerMenu = $("ul[role='menu']");
        this.tableRows = $("#spendings tbody").$$("tr");
        this.statComponent = $("#stat");
        this.spendingTable = $("#spendings");
        this.searchInput = $("input[placeholder='Search']");
    }

    public void friendsPage() {
        header.$("button").click();
        headerMenu.$$("li").find(text("Friends")).click();
    }

    public void allPeoplesPage() {
        header.$("button").click();
        headerMenu.$$("li").find(text("All People")).click();

    }

    public EditSpendingPage editSpending(String spendingDescription) {
        tableRows.find(text(spendingDescription)).$$("td").get(5).click();
        return new EditSpendingPage();
    }

    public void checkThatTableContainsSpending(String spendingDescription) {
        tableRows.find(text(spendingDescription)).should(visible);
    }

    public MainPage checkThatPageLoaded() {
        statComponent.should(visible).shouldHave(text("Statistics"));
        spendingTable.should(visible).shouldHave(text("History of Spendings"));
        return this;
    }

    public MainPage checkThatTableContainsSpendingWithName(String categoryName, String description) {
        SelenideElement spendRow = $(By.xpath(
                String.format("//tbody[.//td[2]/span[text() = '%1$s'] and .//td[4]/span[text() = '%2$s']]",
                        categoryName,
                        description
                )));

        if (spendRow.exists() && spendRow.isDisplayed()) {
            return this;
        } else {
            searchInput.click();
            searchInput.sendKeys(description);
            searchInput.submit();
            spendRow.shouldBe(visible);
            return this;
        }
    }

    public StatComponent statComponent() {
        return (StatComponent) statComponent;
    }

    public void goToProfilePage() {
        $("#root header");
        $("button").click();
        $$("li").find(text("Profile")).click();
    }

    public SpendingTable getSpendingTable() {
        spendingTable.scrollIntoView(true);
        return (SpendingTable) spendingTable;

    }

    public StatComponent getStatComponent() {
        spendingTable.scrollIntoView(true);
        return (StatComponent) statComponent;
    }


}
