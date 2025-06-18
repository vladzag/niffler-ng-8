package guru.qa.niffler.test.web;

import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.service.impl.UsersApiClient;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;

@Tag("FULL_DB")
public class UsersApiTest {
/*
    UsersApiClient usersApiClient = new UsersApiClient();

    @User
    @Test
    public void shouldReturnUsersWhenDBIsNotEmpty(UserJson user) {
        List<UserJson> users = usersApiClient.getAll(user.username());
        assertFalse(users.isEmpty());
    }*/
}
