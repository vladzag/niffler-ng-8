package guru.qa.niffler.jupiter.extension;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import guru.qa.niffler.api.core.ThreadSafeCookieStore;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.model.TestData;
import guru.qa.niffler.model.rest.CategoryJson;
import guru.qa.niffler.model.rest.SpendJson;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.service.impl.AuthApiClient;
import guru.qa.niffler.service.impl.SpendApiClient;
import guru.qa.niffler.service.impl.UsersApiClient;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;
import org.openqa.selenium.Cookie;

import java.util.List;
import java.util.stream.Collectors;

import static guru.qa.niffler.model.FriendshipStatus.*;


public class ApiLoginExtension implements BeforeEachCallback, ParameterResolver {

    private static final Config CFG = Config.getInstance();
    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(ApiLoginExtension.class);
    private final AuthApiClient authApiClient = new AuthApiClient();
    private final SpendApiClient spendApiClient = new SpendApiClient();
    private final UsersApiClient usersApiClient = new UsersApiClient();
    private final boolean setupBrowser;

    private ApiLoginExtension(boolean setupBrowser) {
        this.setupBrowser = setupBrowser;
    }

    public ApiLoginExtension() {
        this.setupBrowser = true;
    }

    public static ApiLoginExtension rest() {
        return new ApiLoginExtension(false);
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), ApiLogin.class)
                .ifPresent(apiLogin -> {
                    final UserJson userToLogin;
                    final UserJson userFromUserExtension = UserExtension.createdUser();
                    //Генерация нового пользователя
                    if("".equals(apiLogin.username()) || "".equals(apiLogin.password())) {
                        if (userFromUserExtension == null) {
                            throw new IllegalStateException("@User must be present in case @ApiLogin is empty");
                        }
                        userToLogin = userFromUserExtension;
                    } else {
                        //Пользователь без генерации
                        UserJson fakeUser = enrichUser(
                                apiLogin.username(),
                                apiLogin.password()
                        );

                        if (userFromUserExtension != null) {
                            throw new IllegalStateException("@User must not be present in case @ApiLogin contains username/password");
                        }
                        UserExtension.setUser(fakeUser);
                        userToLogin = fakeUser;
                    }

                    final String token = authApiClient.login(
                            userToLogin.username(),
                            userToLogin.testData().password()
                    );

                    setToken(token);

                    if (setupBrowser) {
                        Selenide.open(CFG.frontUrl());
                        Selenide.localStorage().setItem("id_token", getToken());
                        WebDriverRunner.getWebDriver().manage().addCookie(
                                new Cookie(
                                        "JSESSIONID",
                                        ThreadSafeCookieStore.INSTANCE.cookieValue("JSESSIONID")
                                )
                        );
                        Selenide.open(MainPage.URL, MainPage.class).checkThatPageLoaded();
                    }
                });

    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(String.class) &&
                AnnotationSupport.isAnnotated(parameterContext.getParameter(), Token.class);
    }

    @Override
    public String resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return getToken();
    }

    public static void setToken(String token) {
        TestMethodContextExtension.context().getStore(NAMESPACE).put("token", token);
    }

    public static String getToken() {
        return TestMethodContextExtension.context().getStore(NAMESPACE).get("token", String.class);
    }

    public static void setCode(String code) {
        TestMethodContextExtension.context().getStore(NAMESPACE).put("code", code);
    }

    public static String getCode() {
        return TestMethodContextExtension.context().getStore(NAMESPACE).get("code", String.class);
    }

    public static Cookie getJSessionIdCookie() {
        return  new Cookie(
                "JSESSIONID",
                ThreadSafeCookieStore.INSTANCE.cookieValue("JSESSIONID")
        );
    }

    private UserJson enrichUser(String username, String password) {
        List<CategoryJson> categories = spendApiClient.getAllCategories(username);
        List<SpendJson> spends = spendApiClient.allSpends(username);

        List<UserJson> listInFriendsTab = usersApiClient.friends(username);
        List<UserJson> listInAllTab = usersApiClient.allPeople(username);

        List<UserJson> friends = listInFriendsTab.stream()
                .filter(userJson -> userJson.friendshipStatus().equals(FRIEND))
                .collect(Collectors.toList());
        List<UserJson> incomeInvitations = listInFriendsTab.stream()
                .filter(userJson -> userJson.friendshipStatus().equals(INVITE_RECEIVED))
                .collect(Collectors.toList());
        List<UserJson> outcomeInvitations = listInAllTab.stream()
                .filter(userJson -> userJson.friendshipStatus() != null)
                .filter(userJson -> userJson.friendshipStatus().equals(INVITE_SENT))
                .collect(Collectors.toList());

        return new UserJson(
                username,
                new TestData(
                        password,
                        categories,
                        spends,
                        friends,
                        outcomeInvitations,
                        incomeInvitations
                )
        );
    }
}