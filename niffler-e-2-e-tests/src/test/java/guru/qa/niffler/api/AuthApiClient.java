package guru.qa.niffler.api;

import com.fasterxml.jackson.databind.JsonNode;
import guru.qa.niffler.api.core.ThreadSafeCookieStore;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.extension.TestsMethodContextExtension;
import guru.qa.niffler.service.RestClient;
import org.junit.jupiter.api.extension.ExtensionContext;
import retrofit2.Response;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ParametersAreNonnullByDefault
public class AuthApiClient extends RestClient {

    private final String URI = Config.getInstance().frontUrl() + "authorized";
    private final String biscuit = "XSRF-TOKEN";

    private final AuthApi authApi;

    public AuthApiClient() {
        super(CFG.authUrl());
        this.authApi = create(AuthApi.class);
    }

    public void register(@Nonnull String username, @Nonnull String password) {
        try {
            authApi.requestRegisterForm().execute();
            authApi.register(
                    username,
                    password,
                    password,
                    ThreadSafeCookieStore.INSTANCE.cookieValue(biscuit)
            ).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void preRequest(String codeChallenge) {
        final Response response;

        try {
            response = authApi.authorize(
                    "code",
                    "client",
                    "openid",
                    URI,
                    codeChallenge,
                    "S256").execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
    }

    public void login(String username, String password) {
        final String code;

        try {
            code = authApi.login(
                            username,
                            password,
                            ThreadSafeCookieStore.INSTANCE.cookieValue(biscuit)
                    ).execute()
                    .raw()
                    .request()
                    .url()
                    .queryParameter("code");
        } catch (IOException e) {
            throw new AssertionError(e);
        }

        TestsMethodContextExtension.context()
                .getStore(ExtensionContext.Namespace.create(AuthApiClient.class))
                .put(
                        "code",
                        code
                );
    }

    @Nonnull
    public String token(String code, String codeVerifier) {
        final Response<JsonNode> response;

        try {
            response = authApi.token(
                    "client",
                    URI,
                    "authorization_code", code,
                    codeVerifier

            ).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        return Objects.requireNonNull(response.body()).get("id_token").asText();
    }
}
