package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.page.component.SearchField;
import io.qameta.allure.Step;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.textsInAnyOrder;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

public class FriendsPage  extends BasePage {
  public static final String URL = Config.getInstance().frontUrl() + "people/friends";

  private final SelenideElement peopleTab = $("a[href='/people/friends']");
  private final SelenideElement allTab = $("a[href='/people/all']");
  private final SelenideElement requestsTable;
  private final SelenideElement friendsTable;
  private final SelenideElement popUp;

  private final SearchField searchField = new SearchField();

  public FriendsPage(SelenideDriver driver) {
    this.requestsTable = $("#requests");
    this.friendsTable = $("#friends");
    this.popUp = $("div[role='dialog']");
  }

  @Step("Инициализация страницы друзей с использованием элементов веб-интерфейса")
  public FriendsPage checkExistingFriends(String... expectedUsernames) {
    friendsTable.$$("tr").shouldHave(textsInAnyOrder(expectedUsernames));
    return this;
  }
  @Step("Проверка отсутствия существующих друзей в таблице")
  public FriendsPage checkNoExistingFriends() {
    friendsTable.$$("tr").shouldHave(size(0));
    return this;
  }

  @Step("Проверка наличия ожидаемых приглашений от пользователей: {expectedUsernames}")
  public FriendsPage checkExistingInvitations(String... expectedUsernames) {
    requestsTable.$$("tr").shouldHave(textsInAnyOrder(expectedUsernames));
    return this;
  }
@Step("Проверка, что у пользователя ожидаемое количество входящих запросов {amount}")
  public FriendsPage checkUserHasExpectedAmountOfIncomeInvitation(int amount) {
    requestsTable.$$("tr").shouldHave(size(amount));
    return this;
  }
  @Step("Проверка, что у пользователя ожидаемое количество ({amount}) друзей")
  public FriendsPage checkUserHasExpectedAmountOfFriends(int amount) {
    friendsTable.$$("tr").shouldHave(size(amount));
    return this;
  }

  @Step("Удаляем друга с именем {username}")
  public FriendsPage removeFriend(String username) {
    friendsTable.$$("tr")
            .find(text(username))
            .$("button").click();
    popUp.$(byText("Delete")).click();
    return this;
  }

  @Step("Принимаем запрос на дружбу от {username}")
  public FriendsPage acceptFriendInvitationFromUser(String username) {
    requestsTable.$$("tr")
            .find(text(username))
            .$(byText("Accept"))
            .click();
    return this;
  }

  @Step("Принимаем запрос на дружбу")
  public FriendsPage acceptFriendInvitation() {
    requestsTable.$$("tr")
            .first()
            .$(byText("Accept"))
            .click();
    return this;
  }

  @Step("Отклоняем запрос на дружбу от {username}")
  public FriendsPage declineFriendInvitationFromUser(String username) {
    requestsTable.$$("tr")
            .find(text(username))
            .$(byText("Decline"))
            .click();
    popUp.$(byText("Decline")).click();
    return this;
  }

  @Step("Отклоняем запрос на дружбу")
  public FriendsPage declineFriendInvitation() {
    requestsTable.$$("tr")
            .first()
            .$(byText("Decline"))
            .click();
    popUp.$(byText("Decline")).click();
    return this;
  }
}
