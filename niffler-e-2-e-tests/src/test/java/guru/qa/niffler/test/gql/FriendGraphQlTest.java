package guru.qa.niffler.test.gql;

import com.apollographql.apollo.api.ApolloResponse;
import com.apollographql.java.client.ApolloCall;
import com.apollographql.java.rx2.Rx2Apollo;
import guru.qa.FriendsCategoryQuery;
import guru.qa.FriendsOfFriendsQuery;
import guru.qa.FriendsOfFriendsRecursiveQuery;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.jupiter.annotation.User;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FriendGraphQlTest extends BaseGraphQlTest{

    private static final String CATEGORIES_ACCESS_ERROR_MSG = "Can`t query categories for another user";
    private static final String RECURSIVE_FRIENDS_SUB_QUERY_ERROR_MSG = "Can`t fetch over 2 friends sub-queries";
    private static final String FRIENDS_SUB_QUERY_ERROR_MSG = "Can`t fetch over 1 friends sub-queries";

    @User(
            spendings = @Spending(
                    category = "Обучение",
                    description = "Обучение Advanced 2.0",
                    amount = 79990
            ),
            friends = 1)
    @ApiLogin
    @Test
    void friendsCategoriesShouldNotBeReturned(@Token String bearerToken) {
        ApolloCall<FriendsCategoryQuery.Data> userCall = apolloClient.query(FriendsCategoryQuery.builder()
                .build()
        ).addHttpHeader("authorization", bearerToken);

        ApolloResponse<FriendsCategoryQuery.Data> response = Rx2Apollo.single(userCall).blockingGet();
        assertEquals(
                CATEGORIES_ACCESS_ERROR_MSG,
                Objects.requireNonNull(response.errors).getFirst().getMessage()
        );
    }

    @User(
            friends = 1)
    @ApiLogin
    @Test
    void friendsOfFriendsShouldNotBeReturnedRecursively(@Token String bearerToken) {
        ApolloCall<FriendsOfFriendsRecursiveQuery.Data> userCall = apolloClient.query(FriendsOfFriendsRecursiveQuery.builder()
                .build()
        ).addHttpHeader("authorization", bearerToken);

        ApolloResponse<FriendsOfFriendsRecursiveQuery.Data> response = Rx2Apollo.single(userCall).blockingGet();
        assertEquals(
                FRIENDS_SUB_QUERY_ERROR_MSG,
                Objects.requireNonNull(response.errors).getFirst().getMessage()
        );
    }

    @User(
            friends = 1)
    @ApiLogin
    @Test
    void friendsOfFriendsShouldNotBeReturned(@Token String bearerToken) {
        ApolloCall<FriendsOfFriendsQuery.Data> userCall = apolloClient.query(FriendsOfFriendsQuery.builder()
                .build()
        ).addHttpHeader("authorization", bearerToken);

        ApolloResponse<FriendsOfFriendsQuery.Data> response = Rx2Apollo.single(userCall).blockingGet();
        assertEquals(
                FRIENDS_SUB_QUERY_ERROR_MSG,
                Objects.requireNonNull(response.errors).getFirst().getMessage()
        );
    }
}