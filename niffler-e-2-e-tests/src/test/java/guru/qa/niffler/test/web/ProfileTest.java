package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.ProfilePage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(BrowserExtension.class)
public class ProfileTest {

    private static final Config CFG = Config.getInstance();

    @User(categories = @Category(username = "duck", archived = true))
    @Test
    void archivedCategoryShouldPresentInCategoriesList(CategoryJson category) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .successLogin("duck", "12345")
                .checkThatPageLoaded();

        Selenide.open(CFG.frontUrl() + "profile", ProfilePage.class)
                .checkArchivedCategoryExists(category.name());
    }

    @User(categories = @Category(username = "duck", archived = true))
    @Test
    void activeCategoryShouldPresentInCategoriesList(CategoryJson category) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .successLogin("duck", "12345")
                .checkThatPageLoaded();

        Selenide.open(CFG.frontUrl() + "profile", ProfilePage.class)
                .checkCategoryExists(category.name());
    }
}