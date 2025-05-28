package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.page.component.SpendingTable;
import guru.qa.niffler.page.component.StatComponent;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class MainPage {
    public static String URL = Config.getInstance().frontUrl() + "main";


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

    @Step("Переход на страницу друзей")
    public void friendsPage() {
        header.$("button").click();
        headerMenu.$$("li").find(text("Friends")).click();
    }

    @Step("Переход на страницу всех людей")
    public void allPeoplesPage() {
        header.$("button").click();
        headerMenu.$$("li").find(text("All People")).click();
    }

    @Step("Редактирование расхода: {spendingDescription}")
    public void editSpending(String spendingDescription) {
        tableRows.find(text(spendingDescription)).$$("td").get(5).click();
    }

    @Step("Проверка наличия расхода в таблице: {spendingDescription}")
    public void checkThatTableContainsSpending(String spendingDescription) {
        tableRows.find(text(spendingDescription)).should(visible);
    }

    @Step("Проверка загрузки главной страницы")
    public MainPage checkThatPageLoaded() {
        statComponent.should(visible).shouldHave(text("Statistics"));
        spendingTable.should(visible).shouldHave(text("History of Spendings"));
        return this;
    }

    @Step("Проверка наличия расхода в таблице по категории {categoryName} и описанию {description}")
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

    @Step("Переход к компоненту статистики")
    public StatComponent statComponent() {
        return (StatComponent) statComponent;
    }

    @Step("Переход на страницу профиля")
    public void goToProfilePage() {
        $("#root header");
        $("button").click();
        $$("li").find(text("Profile")).click();
    }

    @Step("Получение таблицы расходов")
    public SpendingTable getSpendingTable() {
        spendingTable.scrollIntoView(true);
        return (SpendingTable) spendingTable;
    }

    @Step("Получение компонента статистики")
    public StatComponent getStatComponent() {
        spendingTable.scrollIntoView(true);
        return (StatComponent) statComponent;
    }
}
