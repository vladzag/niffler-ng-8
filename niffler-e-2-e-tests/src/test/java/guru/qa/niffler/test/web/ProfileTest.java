package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.ProfilePage;
import org.junit.jupiter.api.Test;

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
}
