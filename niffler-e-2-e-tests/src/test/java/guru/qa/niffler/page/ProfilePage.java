package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.utils.ScreenDiffResult;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class ProfilePage {

    public static final String URL = Config.getInstance().frontUrl() + "profile";

    private final SelenideElement avatar;
    private final SelenideElement userName;
    private final SelenideElement nameInput;
    private final SelenideElement photoInput;
    private final SelenideElement submitButton;
    private final SelenideElement categoryInput;
    private final SelenideElement archivedSwitcher;
    private final ElementsCollection bubbles;
    private final ElementsCollection bubblesArchived;
    private final SelenideElement profilePic;
    private final SelenideElement editCategoryInput;


    public ProfilePage(SelenideDriver driver) {
        this.avatar = $("#image__input").parent().$("img");
        this.userName = $("#username");
        this.nameInput = $("#name");
        this.photoInput = $("input[type='file']");
        this.submitButton = $("button[type='submit']");
        this.categoryInput = $("input[name='category']");
        this.archivedSwitcher = $(".MuiSwitch-input");
        this.bubbles = $$(".MuiChip-filled.MuiChip-colorPrimary");
        this.bubblesArchived = $$(".MuiChip-filled.MuiChip-colorDefault");
        this.profilePic = $(By.xpath("//img[contains(@src, 'data:image/jpeg')]"));
        this.editCategoryInput = $("input[placeholder='Edit category']");

    }

    @Step("Установить имя: {name}")
    public ProfilePage setName(String name) {
        nameInput.clear();
        nameInput.setValue(name);
        return this;
    }

    @Step("Загрузить фото из пути класса: {path}")
    public ProfilePage uploadPhotoFromClasspath(String path) {
        photoInput.uploadFromClasspath(path);
        return this;
    }

    @Step("Добавить категорию: {category}")
    public ProfilePage addCategory(String category) {
        categoryInput.setValue(category).pressEnter();
        return this;
    }

    @Step("Проверить наличие категории: {category}")
    public ProfilePage checkCategoryExists(String category) {
        bubbles.find(text(category)).shouldBe(visible);
        return this;
    }

    @Step("Проверить наличие архивной категории: {category}")
    public ProfilePage checkArchivedCategoryExists(String category) {
        archivedSwitcher.click();
        bubblesArchived.find(text(category)).shouldBe(visible);
        return this;
    }

    @Step("Проверить имя пользователя: {username}")
    public ProfilePage checkUsername(String username) {
        this.userName.should(value(username));
        return this;
    }

    @Step("Проверить имя: {name}")
    public ProfilePage checkName(String name) {
        nameInput.shouldHave(value(name));
        return this;
    }

    @Step("Проверить наличие фото")
    public ProfilePage checkPhotoExist() {
        avatar.should(attributeMatching("src", "data:image.*"));
        return this;
    }

    @Step("Проверить, что ввод категории недоступен")
    public ProfilePage checkThatCategoryInputDisabled() {
        categoryInput.should(disabled);
        return this;
    }

    @Step("Отправить профиль")
    public ProfilePage submitProfile() {
        submitButton.click();
        return this;
    }

    @Step("Обновить категорию: {category}")
    public ProfilePage updateCategory(String category) {
        SelenideElement row = bubbles.find(text(category));
        row.sibling(0).$("button[aria-label='Archive category']").click();
        $(By.xpath("//button[text() = 'Archive']")).shouldBe(visible).click();
        return this;
    }

    @Step("Получить элемент изображения профиля")
    public SelenideElement getProfilePic() {
        return profilePic;
    }

    @Step("Проверить сообщение об ошибке: {errorMessage}")
    public ProfilePage checkAlertMessage(String errorMessage) {
        $(".form__error").shouldHave(text(errorMessage));
        return this;
    }

    @Step("Проверить сходство фото с ожидаемым изображением")
    public ProfilePage checkPhoto(BufferedImage expected) throws IOException {
        Selenide.sleep(1000);
        BufferedImage actualImage = ImageIO.read(Objects.requireNonNull(avatar.screenshot()));
        assertFalse(
                new ScreenDiffResult(
                        actualImage, expected
                ),
                "Несоответствие изображений"
        );
        return this;
    }
    @Step("Редактируем имя категории {newName}")
    public ProfilePage editCategoryName(String nameToUpdate, String newName) {
        bubbles.find(text(nameToUpdate))
                .sibling(0)
                .$("button[aria-label='Edit category']")
                .click();
        editCategoryInput.setValue(newName)
                .pressEnter();
        return this;
    }
}
