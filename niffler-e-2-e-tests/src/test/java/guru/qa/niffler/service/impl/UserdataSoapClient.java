package guru.qa.niffler.service.impl;

import guru.qa.niffler.api.UserdataSoapApi;
import guru.qa.niffler.api.core.RestClient;
import guru.qa.niffler.api.core.converter.SoapConverterFactory;
import guru.qa.niffler.config.Config;
import io.qameta.allure.Step;
import jaxb.userdata.*;
import okhttp3.logging.HttpLoggingInterceptor;
import org.jetbrains.annotations.NotNull;
import retrofit2.Response;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;

@ParametersAreNonnullByDefault
public class UserdataSoapClient extends RestClient {

    private static final Config CFG = Config.getInstance();

    private final UserdataSoapApi userdataSoapApi;

    public UserdataSoapClient() {
        super(CFG.userdataUrl(), false, SoapConverterFactory.create("niffler-userdata"), HttpLoggingInterceptor.Level.BODY);
        this.userdataSoapApi = create(UserdataSoapApi.class);
    }

    @Step("Get Current user info using SOAP")
    @NotNull
    public UserResponse currentUser(CurrentUserRequest currentUserRequest) throws IOException {

        return userdataSoapApi.currentUser(currentUserRequest).execute().body();

    }

    @NotNull
    @Step("Get Page list of users")
    public UsersResponse allUsersPage(AllUsersPageRequest request) {
        Response<UsersResponse> response;

        try {
            response = userdataSoapApi.allUsersPage(request).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assert response.body() != null;
        return response.body();
    }

    @NotNull
    @Step("Get Page list of friends")
    public UsersResponse friendsPage(FriendsPageRequest request) {
        Response<UsersResponse> response;

        try {
            response = userdataSoapApi.friendsPage(request).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assert response.body() != null;
        return response.body();
    }

    @NotNull
    @Step("Get list of friends")
    public UsersResponse friends(FriendsRequest request) {
        Response<UsersResponse> response;

        try {
            response = userdataSoapApi.friends(request).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assert response.body() != null;
        return response.body();
    }

    @NotNull
    @Step("Remove friend")
    public void removeFriend(RemoveFriendRequest request) {
        try {
            userdataSoapApi.removeFriend(request).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    @Step("Accept friend")
    public UserResponse acceptFriend(AcceptInvitationRequest request) {
        Response<UserResponse> response;

        try {
            response = userdataSoapApi.acceptFriend(request).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assert response.body() != null;
        return response.body();
    }

    @NotNull
    @Step("Decline friend")
    public UserResponse declineFriend(DeclineInvitationRequest request) {
        Response<UserResponse> response;

        try {
            response = userdataSoapApi.declineFriend(request).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assert response.body() != null;
        return response.body();
    }

    @NotNull
    @Step("Add friend")
    public UserResponse addFriend(SendInvitationRequest request) {
        Response<UserResponse> response;

        try {
            response = userdataSoapApi.addFriend(request).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assert response.body() != null;
        return response.body();
    }
}