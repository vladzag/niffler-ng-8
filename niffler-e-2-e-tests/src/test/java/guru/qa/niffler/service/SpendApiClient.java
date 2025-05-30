package guru.qa.niffler.service;

import guru.qa.niffler.api.SpendApi;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import io.qameta.allure.Step;
import io.qameta.allure.okhttp3.AllureOkHttp3;
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

    private final OkHttpClient client = new OkHttpClient.Builder().
            addNetworkInterceptor(new AllureOkHttp3()
                    .setRequestTemplate("my-http-request.ftl")
                    .setResponseTemplate("my-http-response.ftl"))
            .build();
    private final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(CFG.spendUrl())
            .client(client)
            .addConverterFactory(JacksonConverterFactory.create())
            .build();
    private final SpendApi spendApi = retrofit.create(SpendApi.class);

    @Step("Удалить расходы у пользователя {username}")
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
    @Step("Создать траты {spend}")
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
    @Step("Обновить трату {spend}")
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
    @Step("Создать категорию {category}")
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
    @Step("Найти категорию по {id}]")
    public Optional<CategoryJson> findCategoryById(UUID id) {
        throw new UnsupportedOperationException("NYI method findCategoryById");

    }
    @Step("Найти категорию по {id}]")
    public Optional<SpendJson> findById(UUID id) {
        throw new UnsupportedOperationException("NYI method findById");

    }
    @Step("Найти расход у пользователя: {username} с описанием {description}")
    public Optional<SpendJson> findByUsernameAndSpendDescription(String username, String description) {
        throw new UnsupportedOperationException("NYI method findByUsernameAndSpendDescription");
    }

@Step("Удалить трату {spend}")
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
    @Step("Удалить категорию")
    public void removeCategory(CategoryJson category) {
        throw new UnsupportedOperationException("NYI method for category removal");
    }

}
