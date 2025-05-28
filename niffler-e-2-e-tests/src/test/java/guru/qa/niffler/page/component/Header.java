package guru.qa.niffler.page.component;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

public class Header {
    private final SelenideElement self = $("#root header");
    private final SelenideElement menuBtn = self.$("button");
    private final SelenideElement headerMenu = $("ul[role='menu']");

    @Step("Проверяем содержимое шапки страницы")
    public void checkHeaderText() {
        self.$("h1").shouldHave(text("Niffler"));
    }

    @Step("Переходим на страницу Friends")
    public void toFriendsPage() {
        menuBtn.click();
        headerMenu.$$("li").find(text("Friends")).click();
    }

    @Step("Переходим на страницу All People")
    public void toAllPeoplesPage() {
        menuBtn.click();
        headerMenu.$$("li").find(text("All People")).click();
    }

    @Step("Переходим на  страницу Profile")
    public void toProfilePage() {
        menuBtn.click();
        headerMenu.$$("li").find(text("Profile")).click();
    }

    @Step("Делаем Log out")
    public void signOut() {
        menuBtn.click();
        headerMenu.$$("li").find(text("Sign out")).click();
    }

    @Step("Добавляем новый Spend")
    public void addSpendingPage() {
        self.$("a[href='/spending']").click();
    }

    @Step("Переходим на  главную страницу")
    public void toMainPage() {
        self.$("a[href='/main']").click();
    }
}
