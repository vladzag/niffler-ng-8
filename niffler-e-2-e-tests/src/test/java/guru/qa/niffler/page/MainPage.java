package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.universalComponents.SpendingTable;
import guru.qa.niffler.page.universalComponents.StatComponent;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class MainPage {

    private final SelenideElement header = $("#root header");
    private final SelenideElement headerMenu = $("ul[role='menu']");
    private final ElementsCollection tableRows = $("#spendings tbody").$$("tr");
    private final SelenideElement statComponent = $("#stat");
    private final SelenideElement spendingTable = $("#spendings");
    private final SelenideElement searchInput = $("input[placeholder='Search']");


    public FriendsPage friendsPage() {
        header.$("button").click();
        headerMenu.$$("li").find(text("Friends")).click();
        return new FriendsPage();
    }

    public PeoplePage allPeoplesPage() {
        header.$("button").click();
        headerMenu.$$("li").find(text("All People")).click();
        return new PeoplePage();
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

    public ProfilePage goToProfilePage() {
        $("#root header");
        $("button").click();
        $$("li").find(text("Profile")).click();
        return new ProfilePage();
    }

    public SpendingTable getSpendingTable() {
        spendingTable.scrollIntoView(true);
        return (SpendingTable) spendingTable;

    }
}
