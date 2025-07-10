package guru.qa.niffler.test.grpc;

import com.google.protobuf.Empty;
import guru.qa.niffler.grpc.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

public class CurrencyGrpcTest extends BaseGrpcTest {

    @Test
    void allCurrenciesShouldReturned() {
        final CurrencyResponse response = blockingStub.getAllCurrencies(Empty.getDefaultInstance());
        final List<Currency> allCurrenciesList = response.getAllCurrenciesList();
        Assertions.assertEquals(4, allCurrenciesList.size());
    }

    static Stream<Arguments> calculatedRateDataProvider() {
        return Stream.of(Arguments.arguments(CurrencyValues.USD, 1000.00, 15.0),
                Arguments.arguments(CurrencyValues.KZT, 2000.00, 14285.71),
                Arguments.arguments(CurrencyValues.EUR, 500, 6.94)
        );
    }

    @ParameterizedTest
    @MethodSource("calculatedRateDataProvider")
    void shouldCalculateAmountForSpendsInRUB(CurrencyValues targetCurrency,
                                             double amount,
                                             double expectedValue) {
        final CalculateRequest request = CalculateRequest.newBuilder()
                .setAmount(amount)
                .setSpendCurrency(CurrencyValues.RUB)
                .setDesiredCurrency(targetCurrency)
                .build();
        final CalculateResponse calculateResponse = blockingStub.calculateRate(request);
        Assertions.assertEquals(BigDecimal.valueOf(expectedValue), calculateResponse.getCalculatedAmount());
    }

    static Stream<Arguments> calculatedRateForMinSpendDataProvider() {
        return Stream.of(
                Arguments.arguments(CurrencyValues.USD, 0.0),
                Arguments.arguments(CurrencyValues.KZT, 0.07),
                Arguments.arguments(CurrencyValues.EUR, 0.0)
        );
    }

    @ParameterizedTest
    @MethodSource("calculatedRateForMinSpendDataProvider")
    void shouldCalculateAmountForMinSpendsInRUB(CurrencyValues targetCurrency,
                                                double expectedValue) {
        final double amount = 0.01;
        final CalculateRequest request = CalculateRequest.newBuilder()
                .setAmount(amount)
                .setSpendCurrency(CurrencyValues.RUB)
                .setDesiredCurrency(targetCurrency)
                .build();
        final CalculateResponse calculateResponse = blockingStub.calculateRate(request);
        Assertions.assertEquals(expectedValue, calculateResponse.getCalculatedAmount());
    }

}