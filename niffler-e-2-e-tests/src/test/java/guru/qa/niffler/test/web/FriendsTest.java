package guru.qa.niffler.test.web;

import com.codeborne.selenide.SelenideDriver;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.jupiter.converter.BrowserConverter;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.FriendsPage;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.PeoplePage;
import guru.qa.niffler.utils.Browser;
import guru.qa.niffler.utils.SelenideUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.EnumSource;

@WebTest
public class FriendsTest {

    private static final Config CFG = Config.getInstance();
    public static final String USER_PW = "12345";
    private final SelenideDriver driver = new SelenideDriver(SelenideUtils.chromeConfig);


    @User(friends = 1)
    @ParameterizedTest
    @EnumSource(Browser.class)
    void friendShouldBePresentInFriendsTable(@ConvertWith(BrowserConverter.class) SelenideDriver driver, UserJson user) {
        driver.open(CFG.frontUrl(), LoginPage.class)
                .successLogin(user.username(), USER_PW)
                .checkThatPageLoaded();
        new FriendsPage(driver)
                .checkUserHasExptectedAmountOfFriendsFriend(1);
    }

    @User
    @ParameterizedTest
    @EnumSource(Browser.class)
    void friendsTableShouldBeEmptyForNewUser(@ConvertWith(BrowserConverter.class) SelenideDriver driver, UserJson user) {
        driver.open(CFG.frontUrl(), LoginPage.class)
                .successLogin(user.username(), USER_PW)
                .checkThatPageLoaded();
        new FriendsPage(driver)
                .checkUserHasExptectedAmountOfFriendsFriend(0);
    }

    @User(incomeInvitations = 1)
    @ParameterizedTest
    @EnumSource(Browser.class)
    void incomeInvitationBePresentInFriendsTable(@ConvertWith(BrowserConverter.class) SelenideDriver driver, UserJson user) {
        driver.open(CFG.frontUrl(), LoginPage.class)
                .successLogin(user.username(), USER_PW)
                .checkThatPageLoaded();
        new FriendsPage(driver)
                .checkExistingInvitations(user.testData().outcomeInvitations().getFirst().username());
    }

    @User(outcomeInvitations = 1)
    @ParameterizedTest
    @EnumSource(Browser.class)
    void outcomeInvitationBePresentInAllPeoplesTable(@ConvertWith(BrowserConverter.class) SelenideDriver driver, UserJson user) {
        driver.open(CFG.frontUrl(), LoginPage.class)
                .successLogin(user.username(), USER_PW)
                .checkThatPageLoaded();
        new PeoplePage(driver)
                .checkInvitationSentToUser(user.testData().outcomeInvitations().getFirst().username());
    }
}
