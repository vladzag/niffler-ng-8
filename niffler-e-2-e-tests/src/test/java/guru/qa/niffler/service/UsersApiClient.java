package guru.qa.niffler.service;

import guru.qa.niffler.api.AuthApi;
import guru.qa.niffler.api.UserdataApi;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.UserJson;
import io.qameta.allure.Step;
import io.qameta.allure.okhttp3.AllureOkHttp3;
import okhttp3.OkHttpClient;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;

import static guru.qa.niffler.utils.RandomDataUtils.randomUsername;
import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ParametersAreNonnullByDefault
public class UsersApiClient implements UsersClient {

    private static final Config CFG = Config.getInstance();
    private static final String defaultPassword = "12345";
    private final OkHttpClient client = new OkHttpClient.Builder()
            .addNetworkInterceptor(new AllureOkHttp3()
                    .setRequestTemplate("my-http-request.ftl")
                    .setResponseTemplate("my-http-response.ftl"))
            .build();
    private final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(CFG.spendUrl())
            .client(client)
            .addConverterFactory(JacksonConverterFactory.create())
            .build();

    private final AuthApi authApi = retrofit.create(AuthApi.class);
    private final UserdataApi userdataApi = retrofit.create(UserdataApi.class);

    @Override
    @Step("Создать пользователя {username }с использованием API")
    public UserJson createUser(String username, String password) {
        try {
            authApi.requestRegisterForm().execute();
            authApi.register(
                    username,
                    password,
                    password,
                    null
            ).execute();
            UserJson createdUser = requireNonNull(userdataApi.currentUser(username).execute().body());
            return createdUser.withPassword(
                    defaultPassword
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Step("Создать входящие запросы дружбы у пользователя {targetuser} в количестве {count}")
    public void createIncomeInvitations(UserJson targetUser, int count) {
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                final String username = randomUsername();
                final Response<UserJson> response;
                final UserJson newUser;
                try {
                    newUser = createUser(username, defaultPassword);
                    response = userdataApi.sendInvitation(
                            newUser.username(),
                            targetUser.username()
                    ).execute();
                } catch (IOException e) {
                    throw new AssertionError(e);
                }
                assertEquals(200, response.code());
            }
        }
    }


    @Override
    @Step("Создать исходящие запросы дружбы у пользователя {targetuser} в количестве {count}")
    public void createOutcomeInvitations(UserJson targetUser, int count) {
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                final String username = randomUsername();
                final Response<UserJson> response;
                final UserJson newUser;
                try {
                    newUser = createUser(username, defaultPassword);
                    response = userdataApi.sendInvitation(
                            targetUser.username(),
                            newUser.username()
                    ).execute();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                assertEquals(200, response.code());
            }
        }
    }

    @Override
    @Step("Создать друзей пользователю {targetUser} в количестве {count}")
    public void createFriends(UserJson targetUser, int count) {
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                final String username = randomUsername();
                final Response<UserJson> response;
                final UserJson newUser;
                try {
                    newUser = createUser(username, defaultPassword);
                    userdataApi.sendInvitation(
                            newUser.username(),
                            targetUser.username()
                    ).execute();
                    response = userdataApi.acceptInvitation(targetUser.username(), username).execute();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                assertEquals(200, response.code());
            }
        }
    }
}