package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideDriver;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.ScreenShotTest;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.converter.BrowserConverter;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.page.ProfilePage;
import guru.qa.niffler.utils.Browser;
import guru.qa.niffler.utils.ScreenDiffResult;
import guru.qa.niffler.utils.SelenideUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.EnumSource;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static guru.qa.niffler.utils.RandomDataUtils.randomName;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class ProfileTest {

    private static final Config CFG = Config.getInstance();

    @User(
            categories = @Category(
                    archived = true
            )
    )
    @ParameterizedTest
    @EnumSource(Browser.class)
    void archivedCategoryShouldPresentInCategoriesList(@ConvertWith(BrowserConverter.class) SelenideDriver driver, UserJson user) {
        final CategoryJson archivedCategory = user.testData().categories().getFirst();

        driver.open(CFG.frontUrl());
        new LoginPage(driver)
                .successLogin(user.username(), user.testData().password())
                .checkThatPageLoaded();

        driver.open(CFG.frontUrl() + "profile", ProfilePage.class)
                .checkArchivedCategoryExists(archivedCategory.name());
    }

    @User(
            categories = @Category(
                    archived = false
            )
    )
    @ParameterizedTest
    @EnumSource(Browser.class)
    void activeCategoryShouldPresentInCategoriesList(@ConvertWith(BrowserConverter.class) SelenideDriver driver, UserJson userJson) {
        driver.open(CFG.frontUrl());
        new LoginPage(driver)
                .successLogin(userJson.username(), userJson.testData().password())
                .checkThatPageLoaded();

        driver.open(CFG.frontUrl() + "profile", ProfilePage.class)
                .checkCategoryExists(String.valueOf(userJson.testData().categories().getFirst()));
    }

    @User
    @ScreenShotTest("img/expected/expected-dog.png")
    @ParameterizedTest
    @EnumSource(Browser.class)
    void shouldUpdateProfileImageWhenUploadNewImage(@ConvertWith(BrowserConverter.class) SelenideDriver driver, UserJson user, BufferedImage expected) throws IOException {
        final String newName = randomName();

        driver.open(CFG.frontUrl());
        new LoginPage(driver)
                .login(user.username(), user.testData().password())
                .checkThatPageLoaded();
        new MainPage(driver)
                .goToProfilePage();
        new ProfilePage(driver)
                .uploadPhotoFromClasspath("img/dog.jpeg")
                .setName(newName)
                .submitProfile()
                .checkAlertMessage("Profile successfully updated");

        Selenide.refresh();

        new ProfilePage(driver).checkName(newName);

    }

    @User
    @ScreenShotTest("img/expected/expected-scientist.png")
    @ParameterizedTest
    @EnumSource(Browser.class)
    void shouldUpdateProfileImageWhenUpdateImage(@ConvertWith(BrowserConverter.class) SelenideDriver driver, UserJson user, BufferedImage expected) throws IOException {
        final String newName = randomName();

        driver.open(CFG.frontUrl());
        new LoginPage(driver)
                .login(user.username(), user.testData().password())
                .checkThatPageLoaded();
        new MainPage(driver)
                .goToProfilePage();
        new ProfilePage(driver)
                .uploadPhotoFromClasspath("img/dog.jpeg")
                .setName(newName)
                .submitProfile()
                .checkAlertMessage("Profile successfully updated");
        Selenide.refresh();

        new ProfilePage(driver)
                .uploadPhotoFromClasspath("img/scientist.jpeg")
                .submitProfile()
                .checkAlertMessage("Profile successfully updated");

        Selenide.refresh();

        new ProfilePage(driver).checkName(newName)
                .checkPhotoExist()
                .checkPhoto(expected);

    }
}