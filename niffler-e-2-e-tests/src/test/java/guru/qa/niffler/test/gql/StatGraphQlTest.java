package guru.qa.niffler.test.gql;

import com.apollographql.apollo.api.ApolloResponse;
import com.apollographql.java.client.ApolloCall;
import com.apollographql.java.rx2.Rx2Apollo;
import guru.qa.CategoriesQuery;
import guru.qa.StatQuery;
import guru.qa.niffler.jupiter.annotation.*;
import guru.qa.niffler.model.CurrencyValues;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StatGraphQlTest extends BaseGraphQlTest {

    @User
    @ApiLogin
    @Test
    void statTest(@Token String bearerToken) {
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

    @User(
            categories = {
                    @Category(name = "Médicaments", archived = true),
                    @Category(name = "Sport"),
                    @Category(name = "Répas")
            },
            spendings = {
                    @Spending(
                            category = "Médicaments",
                            description = "Comprimés",
                            amount = 300,
                            currency = CurrencyValues.USD
                    ),
                    @Spending(
                            category = "Sport",
                            description = "Ski pass",
                            amount = 500,
                            currency = CurrencyValues.EUR
                    ),
                    @Spending(
                            category = "Répas",
                            description = "Camembert",
                            amount = 5000,
                            currency = CurrencyValues.KZT
                    )
            }
    )
    @ApiLogin
    @Test
    void archivedCategoriesShouldBeReturnedFromGateway(@Token String bearerToken) {
        ApolloCall<CategoriesQuery.Data> categoriesCall = apolloClient.query(CategoriesQuery.builder().build())
                .addHttpHeader("authorization", bearerToken);

        ApolloResponse<CategoriesQuery.Data> categoriesResponse = Rx2Apollo.single(categoriesCall).blockingGet();
        final CategoriesQuery.Data categoriesData = categoriesResponse.dataOrThrow();
        List<CategoriesQuery.Category> categories = categoriesData.user.categories;
        assertTrue(categories.stream()
                        .filter(cat -> cat.name.equals("Médicaments"))
                        .findFirst().get().archived,
                "У пользователя отсутствует архивная категория 'Médicaments'");

        ApolloCall<StatQuery.Data> statCall = apolloClient.query(StatQuery.builder()
                .filterCurrency(null)
                .statCurrency(null)
                .filterPeriod(null)
                .build()
        ).addHttpHeader("authorization", bearerToken);

        ApolloResponse<StatQuery.Data> statResponse = Rx2Apollo.single(statCall).blockingGet();
        final StatQuery.Data data = statResponse.dataOrThrow();
        List<StatQuery.StatByCategory> statBycategories = data.stat.statByCategories;
        assertTrue(
                statBycategories.stream()
                        .anyMatch(cat -> cat.categoryName.equals("Archived")),
                "В статистике отсутствует архивная категория 'Archived'"
        );
    }

    @User(
            categories = {
                    @Category(name = "Médicaments", archived = true),
                    @Category(name = "Sport"),
                    @Category(name = "Répas"),
                    @Category(name = "Zoo")
            },
            spendings = {
                    @Spending(
                            category = "Médicaments",
                            description = "Comprimés",
                            amount = 300,
                            currency = CurrencyValues.USD
                    ),
                    @Spending(
                            category = "Sport",
                            description = "Ski pass",
                            amount = 500,
                            currency = CurrencyValues.EUR
                    ),
                    @Spending(
                            category = "Répas",
                            description = "Camembert",
                            amount = 5000,
                            currency = CurrencyValues.KZT
                    ),
                    @Spending(
                            category = "Zoo",
                            description = "Nourriture pour chien",
                            amount = 2000,
                            currency = CurrencyValues.RUB
                    )
            }
    )
    @ApiLogin
    @ParameterizedTest
    @EnumSource(CurrencyValues.class)
    void shouldBeAbleToFilterStatByCurrency(CurrencyValues currency, @Token String bearerToken) {
        ApolloCall<StatQuery.Data> statCall = apolloClient.query(StatQuery.builder()
                .filterCurrency(guru.qa.type.CurrencyValues.safeValueOf(currency.toString()))
                .statCurrency(guru.qa.type.CurrencyValues.safeValueOf(currency.toString()))
                .filterPeriod(null)
                .build()
        ).addHttpHeader("authorization", bearerToken);

        ApolloResponse<StatQuery.Data> statResponse = Rx2Apollo.single(statCall).blockingGet();
        final StatQuery.Data data = statResponse.dataOrThrow();
        StatQuery.StatByCategory statBycategories = data.stat.statByCategories.getFirst();
        assertEquals(currency.name(), statBycategories.currency.rawValue);
    }
}