package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideDriver;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.jupiter.converter.BrowserConverter;
import guru.qa.niffler.jupiter.extension.UsersQueueExtension;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.FriendsPage;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.PeoplePage;
import guru.qa.niffler.utils.Browser;
import guru.qa.niffler.utils.SelenideUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.EnumSource;

import static guru.qa.niffler.jupiter.extension.UsersQueueExtension.UserType.Type.WITH_INCOME_REQUEST;

@WebTest
public class FriendsTest {

    public static final String USER_PW = "12345";
    private final SelenideDriver driver = new SelenideDriver(SelenideUtils.chromeConfig);


    @User(friends = 1)
    @ParameterizedTest
    @EnumSource(Browser.class)
    @ApiLogin
    void friendShouldBePresentInFriendsTable(@ConvertWith(BrowserConverter.class) SelenideDriver driver, UserJson user) {
        Selenide.open(FriendsPage.URL, FriendsPage.class)
                .checkUserHasExpectedAmountOfFriends(1);
    }

    @User
    @ParameterizedTest
    @EnumSource(Browser.class)
    @ApiLogin
    void friendsTableShouldBeEmptyForNewUser(@ConvertWith(BrowserConverter.class) SelenideDriver driver, UserJson user) {
        Selenide.open(FriendsPage.URL, FriendsPage.class)
                .checkUserHasExpectedAmountOfFriends(0);
    }

    @User(incomeInvitations = 1)
    @ParameterizedTest
    @EnumSource(Browser.class)
    @ApiLogin
    void incomeInvitationBePresentInFriendsTable(@ConvertWith(BrowserConverter.class) SelenideDriver driver, UserJson user) {
        Selenide.open(FriendsPage.URL, FriendsPage.class)
                .checkExistingInvitations(user.testData().outcomeInvitations().getFirst().username());
    }

    @User(outcomeInvitations = 1)
    @ParameterizedTest
    @EnumSource(Browser.class)
    @ApiLogin
    void outcomeInvitationBePresentInAllPeoplesTable(@ConvertWith(BrowserConverter.class) SelenideDriver driver, UserJson user) {
        Selenide.open(PeoplePage.URL, PeoplePage.class)
                .checkInvitationSentToUser(user.testData().outcomeInvitations().getFirst().username());
    }
    @User(incomeInvitations = 1)
    @ParameterizedTest
    @EnumSource(Browser.class)
    @ApiLogin
    void shouldBeAbleToAcceptFriendRequest(@ConvertWith(BrowserConverter.class) @UsersQueueExtension.UserType(WITH_INCOME_REQUEST) UserJson user) {
        Selenide.open(FriendsPage.URL, FriendsPage.class)
                .acceptFriendInvitation()
                .checkUserHasExpectedAmountOfFriends(1);
    }

    @User(incomeInvitations = 1)
    @ParameterizedTest
    @EnumSource(Browser.class)
    @ApiLogin
    void shouldBeAbleToDeclineFriendRequest(@ConvertWith(BrowserConverter.class) @UsersQueueExtension.UserType(WITH_INCOME_REQUEST) UserJson user) {
        Selenide.open(FriendsPage.URL, FriendsPage.class)
                .declineFriendInvitation()
                .checkUserHasExpectedAmountOfFriends(0);
    }
}
