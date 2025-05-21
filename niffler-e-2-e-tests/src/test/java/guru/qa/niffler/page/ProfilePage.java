package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.utils.ScreenDiffResult;
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
    }

    public ProfilePage setName(String name) {
        nameInput.clear();
        nameInput.setValue(name);
        return this;
    }

    public ProfilePage uploadPhotoFromClasspath(String path) {
        photoInput.uploadFromClasspath(path);
        return this;
    }

    public ProfilePage addCategory(String category) {
        categoryInput.setValue(category).pressEnter();
        return this;
    }

    public ProfilePage checkCategoryExists(String category) {
        bubbles.find(text(category)).shouldBe(visible);
        return this;
    }

    public ProfilePage checkArchivedCategoryExists(String category) {
        archivedSwitcher.click();
        bubblesArchived.find(text(category)).shouldBe(visible);
        return this;
    }

    public ProfilePage checkUsername(String username) {
        this.userName.should(value(username));
        return this;
    }

    public ProfilePage checkName(String name) {
        nameInput.shouldHave(value(name));
        return this;
    }

    public ProfilePage checkPhotoExist() {
        avatar.should(attributeMatching("src", "data:image.*"));
        return this;
    }

    public ProfilePage checkThatCategoryInputDisabled() {
        categoryInput.should(disabled);
        return this;
    }

    public ProfilePage submitProfile() {
        submitButton.click();
        return this;
    }

    public ProfilePage updateCategory(String category) {
        SelenideElement row = bubbles.find(text(category));
        row.sibling(0).$("button[aria-label='Archive category']").click();
        $(By.xpath("//button[text() = 'Archive']")).shouldBe(visible).click();
        return this;
    }

    public SelenideElement getProfilePic() {
        return profilePic;
    }

    public ProfilePage checkAlertMessage(String errorMessage) {
        $(".form__error").shouldHave(text(errorMessage));
        return this;
    }

    public ProfilePage checkPhoto(BufferedImage expected) throws IOException {
        Selenide.sleep(1000);
        BufferedImage actualImage = ImageIO.read(Objects.requireNonNull(avatar.screenshot()));
        assertFalse(
                new ScreenDiffResult(
                        actualImage, expected
                ),
                "Screen comparison failure"
        );
        return this;
    }

}
