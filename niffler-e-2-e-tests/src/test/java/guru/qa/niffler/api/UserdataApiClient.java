package guru.qa.niffler.api;

import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.api.core.RestClient;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;

@ParametersAreNonnullByDefault
public class UserdataApiClient extends RestClient {
    private final UserdataApi userdataApi;
    private final AuthApiClient authApiClient = new AuthApiClient();

    public UserdataApiClient() {
        super(CFG.userdataUrl());
        this.userdataApi = create(UserdataApi.class);
    }

    @Nullable
    public UserJson currentUser(String username) {
        try {
            return userdataApi.currentUser(username)
                    .execute()
                    .body();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
