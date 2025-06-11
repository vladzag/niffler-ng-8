package guru.qa.niffler.jupiter.extension;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import guru.qa.niffler.api.core.ThreadSafeCookieStore;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.model.TestData;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.service.AuthApiClient;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;
import org.openqa.selenium.Cookie;

public class ApiLoginExtension implements BeforeEachCallback, ParameterResolver {
    private static final Config CFG = Config.getInstance();
    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(ApiLoginExtension.class);
    private final AuthApiClient authApiClient = new AuthApiClient();
    private final boolean setupBrowser;

    private ApiLoginExtension(boolean setupBrowser) {
        this.setupBrowser = setupBrowser;
    }

    public ApiLoginExtension() {
        this.setupBrowser = true;
    }

    public static ApiLoginExtension restApiLoginExtension() {
        return new ApiLoginExtension(false);
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), ApiLogin.class)
                .ifPresent(apiLogin -> {
                    final UserJson userToLogin;
                    final UserJson userFromUserExtension = UserExtension.createdUser();
                    //Пользователь без генерации
                    if ("".equals(apiLogin.username()) || "".equals(apiLogin.password())) {
                        if (userFromUserExtension == null) {
                            throw new IllegalStateException("@User must be present in case @ApiLogin is empty");
                        }
                        userToLogin = userFromUserExtension;
                    } else {
                        //Генерация нового пользователя
                        UserJson fakeUser = new UserJson(
                                apiLogin.username(),
                                new TestData(apiLogin.password())
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
        TestsMethodContextExtension.context().getStore(NAMESPACE).put("token", token);
    }

    public static String getToken() {
        return TestsMethodContextExtension.context().getStore(NAMESPACE).get("token", String.class);
    }

    public static void setCode(String code) {
        TestsMethodContextExtension.context().getStore(NAMESPACE).put("code", code);
    }

    public static String getCode() {
        return TestsMethodContextExtension.context().getStore(NAMESPACE).get("code", String.class);
    }

    public static Cookie getJSessionIdCookie() {
        return new Cookie(
                "JSESSIONID",
                ThreadSafeCookieStore.INSTANCE.cookieValue("JSESSIONID")
        );
    }
}