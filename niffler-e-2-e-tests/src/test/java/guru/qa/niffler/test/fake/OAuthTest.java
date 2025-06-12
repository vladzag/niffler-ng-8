package guru.qa.niffler.test.fake;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import guru.qa.niffler.api.core.ThreadSafeCookieStore;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.service.AuthApiClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Cookie;

public class OAuthTest {
    private final AuthApiClient authApiClient = new AuthApiClient();
    private static final Config CFG = Config.getInstance();

    @Test
    void OauthTest() {
        String token = authApiClient.login("duck", "12345");
        Assertions.assertNotNull(token);

        Selenide.open(CFG.frontUrl());
        Selenide.localStorage().setItem("id_token", token);
        WebDriverRunner.getWebDriver().manage().addCookie(
                new Cookie(
                        "JSESSIONID",
                        ThreadSafeCookieStore.INSTANCE.cookieValue("JSESSIONID")
                )
        );
        Selenide.open(MainPage.URL, MainPage.class).checkThatPageLoaded();
    }
}
