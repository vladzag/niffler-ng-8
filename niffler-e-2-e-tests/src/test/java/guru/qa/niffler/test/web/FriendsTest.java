package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.Test;

@WebTest
public class FriendsTest {

    private static final Config CFG = Config.getInstance();
    public static final String USER_PW = "12345";

    @Test
    @User(friends = 1)
    void friendShouldBePresentInFriendsTable(UserJson user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .successLogin(user.username(), USER_PW)
                .checkThatPageLoaded()
                .friendsPage()
                .checkUserHasExptectedAmountOfFriendsFriend(1);
    }

    @User
    @Test
    void friendsTableShouldBeEmptyForNewUser(UserJson user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .successLogin(user.username(), USER_PW)
                .checkThatPageLoaded()
                .friendsPage()
                .checkUserHasExptectedAmountOfFriendsFriend(0);
    }

    @Test
    @User(incomeInvitations = 1)
    void incomeInvitationBePresentInFriendsTable(UserJson user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .successLogin(user.username(), USER_PW)
                .checkThatPageLoaded()
                .friendsPage()
                .checkExistingInvitations(user.testData().outcomeInvitations().getFirst().username());
    }

    @Test
    @User(outcomeInvitations = 1)
    void outcomeInvitationBePresentInAllPeoplesTable(UserJson user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .successLogin(user.username(), USER_PW)
                .checkThatPageLoaded()
                .allPeoplesPage()
                .checkInvitationSentToUser(user.testData().outcomeInvitations().getFirst().username());
    }
}
