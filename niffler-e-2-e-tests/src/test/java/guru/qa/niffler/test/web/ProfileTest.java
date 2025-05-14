package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.ScreenShotTest;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.ProfilePage;
import guru.qa.niffler.utils.ScreenDiffResult;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static guru.qa.niffler.utils.RandomDataUtils.randomName;
import static org.junit.jupiter.api.Assertions.assertFalse;

@WebTest
public class ProfileTest {

    private static final Config CFG = Config.getInstance();

    @User(
            categories = @Category(
                    archived = true
            )
    )
    @Test
    void archivedCategoryShouldPresentInCategoriesList(UserJson user) {
        final CategoryJson archivedCategory = user.testData().categories().getFirst();

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .successLogin(user.username(), user.testData().password())
                .checkThatPageLoaded();

        Selenide.open(CFG.frontUrl() + "profile", ProfilePage.class)
                .checkArchivedCategoryExists(archivedCategory.name());
    }

    @User(
            categories = @Category(
                    archived = false
            )
    )
    @Test
    void activeCategoryShouldPresentInCategoriesList(UserJson userJson) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .successLogin(userJson.username(), userJson.testData().password())
                .checkThatPageLoaded();

        Selenide.open(CFG.frontUrl() + "profile", ProfilePage.class)
                .checkCategoryExists(String.valueOf(userJson.testData().categories().getFirst()));
    }

    @User
    @ScreenShotTest("img/expected/expected-dog.png")
    void shouldUpdateProfileImageWhenUploadNewImage(UserJson user, BufferedImage expected) throws IOException {
        final String newName = randomName();

        ProfilePage profilePage = Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.testData().password())
                .checkThatPageLoaded()
                .goToProfilePage()
                .uploadPhotoFromClasspath("img/dog.jpeg")
                .setName(newName)
                .submitProfile()
                .checkAlertMessage("Profile successfully updated");

        Selenide.refresh();

        BufferedImage actual = ImageIO.read(profilePage.getProfilePic().screenshot());
        assertFalse(new ScreenDiffResult(
                actual,
                expected
        ));
    }

    @User
    @ScreenShotTest("img/expected/expected-scientist.png")
    void shouldUpdateProfileImageWhenUpdateImage(UserJson user, BufferedImage expected) throws IOException {
        final String newName = randomName();

        ProfilePage profilePage = Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.testData().password())
                .checkThatPageLoaded()
                .goToProfilePage()
                .uploadPhotoFromClasspath("img/dog.jpeg")
                .setName(newName)
                .submitProfile()
                .checkAlertMessage("Profile successfully updated");
        Selenide.refresh();

        profilePage
                .uploadPhotoFromClasspath("img/scientist.jpeg")
                .submitProfile()
                .checkAlertMessage("Profile successfully updated");

        Selenide.refresh();

        BufferedImage actual = ImageIO.read(profilePage.getProfilePic().screenshot());
        assertFalse(new ScreenDiffResult(
                actual,
                expected
        ));
    }
}