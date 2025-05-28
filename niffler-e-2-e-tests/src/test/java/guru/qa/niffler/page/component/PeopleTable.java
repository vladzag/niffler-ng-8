package guru.qa.niffler.page.component;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class PeopleTable {

    private final SelenideElement self = $("#all");
    private final SelenideElement prevBtn = $("button#page-prev");
    private final SelenideElement nextBtn = $("button#page-next");
    private final ElementsCollection sentInvites = $$(By.xpath("//tr[.//span [text() = 'Waiting...']]"));

    private final SearchField searchField = new SearchField();

    @Step("Проверяем, что запрос дружбы отправлен пользователю с именем: {username}")
    public void checkInvitationSentToUser(String username) {
        searchField.search(username);
        self.$$("tr").find(text(username)).shouldHave(text("Waiting..."));
    }

    @Step("Проверяем, что количество отправленных инвайтов - {amount}")
    public void checkAmountOfOutcomeInvitations(int amount) {
        sentInvites.shouldHave(size(amount));
    }

    @Step("Отправляем инвайт пользователю {username}")
    public void sendInvitationTo(String username) {
        searchField.search(username);
        self.$$("tr").find(text(username)).$(byText("Add friend")).click();
    }
}