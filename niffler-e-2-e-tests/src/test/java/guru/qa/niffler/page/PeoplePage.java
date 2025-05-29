package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.config.Config;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class PeoplePage extends BasePage {

    public static final String URL = Config.getInstance().frontUrl() + "people/all";

    private final SelenideElement peopleTab = $("a[href='/people/friends']");
    private final SelenideElement allTab = $("a[href='/people/all']");
    private final SelenideElement peopleTable;
    private final SelenideElement searchInput = $("input[placeholder='Search']");
    private final ElementsCollection sentInvites = $$(By.xpath("//tr[.//span [text() = 'Waiting...']]"));

    public PeoplePage(SelenideDriver driver) {
        this.peopleTable = $("#all");
    }


    @Step("Проверка, что приглашение было отправлено пользователю: {0}")
    public PeoplePage checkInvitationSentToUser(String username) {
        SelenideElement friendRow = peopleTable.$$("tr").find(text(username));
        friendRow.shouldHave(text("Waiting..."));
        return this;
    }
}
