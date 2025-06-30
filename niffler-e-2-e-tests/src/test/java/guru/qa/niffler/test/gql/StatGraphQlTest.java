package guru.qa.niffler.test.gql;

import com.apollographql.apollo.api.ApolloResponse;
import com.apollographql.java.client.ApolloCall;
import com.apollographql.java.rx2.Rx2Apollo;
import guru.qa.StatQuery;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.jupiter.annotation.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StatGraphQlTest extends BaseGraphQlTest{

    @User
    @ApiLogin
    @Test
    void statTest (@Token String bearerToken) {
        ApolloCall<StatQuery.Data> statCall = apolloClient.query(StatQuery.builder()
                .filterCurrency(null)
                .statCurrency(null)
                .filterPeriod(null)
                .build()
        ).addHttpHeader("authorization", bearerToken);

        ApolloResponse<StatQuery.Data> response = Rx2Apollo.single(statCall).blockingGet();
        final StatQuery.Data data = response.dataOrThrow();
        StatQuery.Stat result = data.stat;
        assertEquals(
                0,
                result.total
        );
    }

}