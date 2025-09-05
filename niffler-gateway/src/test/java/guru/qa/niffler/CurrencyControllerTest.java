package guru.qa.niffler;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import guru.qa.niffler.grpc.Currency;
import guru.qa.niffler.grpc.CurrencyResponse;
import guru.qa.niffler.grpc.CurrencyValues;
import guru.qa.niffler.grpc.NifflerCurrencyServiceGrpc;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.wiremock.grpc.Jetty12GrpcExtensionFactory;
import org.wiremock.grpc.dsl.WireMockGrpcService;

import java.util.List;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.wiremock.grpc.dsl.WireMockGrpc.message;
import static org.wiremock.grpc.dsl.WireMockGrpc.method;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CurrencyControllerTest {

    private WireMockGrpcService wireMockGrpcService;
    private WireMock wireMock;

    @Autowired
    private MockMvc mockMvc;

    @RegisterExtension
    public static WireMockExtension wm =
            WireMockExtension.newInstance()
                    .options(
                            wireMockConfig()
                                    .port(8092)
                                    .withRootDirectory("src/test/resources/wiremock")
                                    .extensions(new Jetty12GrpcExtensionFactory()))
                    .build();

    @BeforeEach
    void init() {
        wireMock = wm.getRuntimeInfo().getWireMock();
        wireMockGrpcService =
                new WireMockGrpcService(
                        wireMock,
                        NifflerCurrencyServiceGrpc.SERVICE_NAME
                );
    }

    @AfterEach
    void tearDown() {
        wireMock.shutdown();
    }

    @Test
    void allCurrenciesShouldBeReturned() throws Exception {
        wireMockGrpcService
                .stubFor(
                        method("GetAllCurrencies")
                                .willReturn(message(
                                        CurrencyResponse.newBuilder()
                                                .addAllAllCurrencies(
                                                        List.of(
                                                                Currency.newBuilder()
                                                                        .setCurrency(CurrencyValues.RUB)
                                                                        .setCurrencyRate(0.015)
                                                                        .build(),
                                                                Currency.newBuilder()
                                                                        .setCurrency(CurrencyValues.KZT)
                                                                        .setCurrencyRate(0.0021)
                                                                        .build(),
                                                                Currency.newBuilder()
                                                                        .setCurrency(CurrencyValues.EUR)
                                                                        .setCurrencyRate(1.08)
                                                                        .build(),
                                                                Currency.newBuilder()
                                                                        .setCurrency(CurrencyValues.USD)
                                                                        .setCurrencyRate(1.0)
                                                                        .build()
                                                        )
                                                )
                                                .build()
                                )));


        mockMvc.perform(get("/api/currencies/all")
                        .with(jwt().jwt(c -> c.claim("sub", "duck")))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[0].currency", equalTo("RUB")))
                .andExpect(jsonPath("$[0].currencyRate", closeTo(0.015, 0.001)))
                .andExpect(jsonPath("$[1].currency", equalTo("KZT")))
                .andExpect(jsonPath("$[1].currencyRate", closeTo(0.0021, 0.001)))
                .andExpect(jsonPath("$[2].currency", equalTo("EUR")))
                .andExpect(jsonPath("$[2].currencyRate", closeTo(1.08, 0.001)))
                .andExpect(jsonPath("$[3].currency", equalTo("USD")))
                .andExpect(jsonPath("$[3].currencyRate", closeTo(1.0, 0.001)))
                .andDo(print());;
    }
}