package guru.qa.niffler.test.web;

import guru.qa.niffler.api.AuthApiClient;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.UserJson;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import guru.qa.niffler.jupiter.extension.TestsMethodContextExtension;


import static guru.qa.niffler.utils.OauthUtils.generateCodeChallenge;
import static guru.qa.niffler.utils.OauthUtils.generateCodeVerifier;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class OAuthTest {
    private final AuthApiClient authApi = new AuthApiClient();

    @User
    @Test
    public void oAuthTest(UserJson user) {
        final String codeVerifier = generateCodeVerifier();
        final String codeChallenge = generateCodeChallenge(codeVerifier);

        authApi.preRequest(codeChallenge);
        authApi.login(user.username(), user.testData().password());

        String code = (String) TestsMethodContextExtension.context()
                .getStore(ExtensionContext.Namespace.create(AuthApiClient.class))
                .get("code");

        String token = authApi.token(
                code,
                codeVerifier
        );

        assertNotNull(token);
        System.out.println(token);
    }

}