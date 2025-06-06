package guru.qa.niffler.test.web;

import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.UsersApiClient;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("EMPTY_DB")
public class EmptyDBTest {
    UsersApiClient usersApiClient = new UsersApiClient();

    @User
    @Test
    public void shouldReturnEmptyListWhenDBIsEmpty(UserJson user) {
        List<UserJson> users = usersApiClient.getAll(user.username());
        assertTrue(users.isEmpty());
    }
}
