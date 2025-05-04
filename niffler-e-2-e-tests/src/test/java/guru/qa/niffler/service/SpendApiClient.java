package guru.qa.niffler.service;

import guru.qa.niffler.api.SpendApi;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import okhttp3.OkHttpClient;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SpendApiClient implements SpendClient {
    private static final Config CFG = Config.getInstance();

    private final OkHttpClient client = new OkHttpClient.Builder().build();
    private final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(CFG.spendUrl())
            .client(client)
            .addConverterFactory(JacksonConverterFactory.create())
            .build();
    private final SpendApi spendApi = retrofit.create(SpendApi.class);


    public void removeSpends(String username, List<String> ids) {
        final Response<String> response;
        try {
            response = spendApi.removeSpends(username, ids)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
    }

    @Override
    public SpendJson createSpend(SpendJson spend) {
        final Response<SpendJson> response;
        try {
            response = spendApi.addSpend(spend)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(201, response.code());
        return response.body();
    }

    @Override
    public SpendJson update(SpendJson spend) {
        final Response<SpendJson> response;
        try {
            response = spendApi.editSpend(spend)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        return response.body();
    }

    public CategoryJson createCategory(CategoryJson category) {
        final Response<CategoryJson> response;
        try {
            response = spendApi.addCategory(category)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        return response.body();
    }

    @Override
    public Optional<CategoryJson> findCategoryById(UUID id) {
        throw new UnsupportedOperationException("NYI method findCategoryById");

    }

    public Optional<SpendJson> findById(UUID id) {
        throw new UnsupportedOperationException("NYI method findById");

    }

    public Optional<SpendJson> findByUsernameAndSpendDescription(String username, String description) {
        throw new UnsupportedOperationException("NYI method findByUsernameAndSpendDescription");
    }

    public void remove(SpendJson spend) {
        final Response<String> response;
        try {
            response = spendApi.removeSpends(spend.username(), List.of(String.valueOf(spend.id())))
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
    }

    @Override
    public void removeCategory(CategoryJson category) {
        throw new UnsupportedOperationException("Can`t remove category");
    }

}
