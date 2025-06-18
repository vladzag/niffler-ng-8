package guru.qa.niffler.test.fake;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.condition.Color;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.ScreenShotTest;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.page.FriendsPage;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.service.impl.AuthApiClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class OAuthTest {

    private static final Config CFG = Config.getInstance();
    private final AuthApiClient apiClient = new AuthApiClient();

    @Test
    //@User(friends = 1)
    @ApiLogin(username = "maria", password = "12345")
    void authTest(@Token String token, UserJson user) {
        System.out.println(user);
        Assertions.assertNotNull(token);
    }

    @ApiLogin(
            username = "fe.torphy",
            password = "12345"
    )
    @ScreenShotTest(value = "img/expected-stat-archived.png")
    void testApiLoginWithCategoriesAndSpends(UserJson user, BufferedImage expected) throws IOException {
        Selenide.open(MainPage.URL, MainPage.class)
                .getStatComponent()
                .checkStatisticBubblesContains("Поездки 9500 ₽", "Archived 3100 ₽")
                .checkStatisticImage(expected)
                .checkBubbles(Color.yellow, Color.green);

        System.out.println(user);
    }

    @Test
    @ApiLogin(
            username = "ivan",
            password = "12345"
    )
    void testApiLoginWithFriend(UserJson user) {
        Selenide.open(FriendsPage.URL, FriendsPage.class)
                .checkExistingFriendsCount(1);
        System.out.println(user);
    }

    @Test
    @ApiLogin(
            username = "bee",
            password = "12345"
    )
    void incomeInvitationBePresentInFriendsTable(UserJson user) {
        final String incomeInvitationUsername = user.testData().incomeInvitationsUsernames()[0];

        Selenide.open(FriendsPage.URL, FriendsPage.class)
                .checkExistingInvitations(incomeInvitationUsername);
        System.out.println(user);
    }
}