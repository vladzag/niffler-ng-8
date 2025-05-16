package guru.qa.niffler.condition;

import com.codeborne.selenide.*;
import guru.qa.niffler.model.SpendJson;
import org.apache.commons.lang.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.codeborne.selenide.CheckResult.accepted;
import static com.codeborne.selenide.CheckResult.rejected;
import static com.codeborne.selenide.Selenide.$$;

public class SpendConditions {

    public static WebElementsCondition spends(SpendJson... expectedSpends) {
        return new WebElementsCondition() {

            private String expectedSpend;

            @Nonnull
            @CheckReturnValue
            @Override
            public CheckResult check(Driver driver, @NotNull List<WebElement> elements) {

                if (ArrayUtils.isEmpty(expectedSpends)) {
                    throw new IllegalArgumentException("No expected spends given");
                }

                if (expectedSpends.length != elements.size()) {
                    String message = String.format("List size mismatch (expected: %s, actual: %s)",
                            expectedSpends.length, elements.size());
                    return rejected(message, elements);
                }

                ElementsCollection rows = $$(elements);

                for (int i = 0; i < rows.size(); i++) {
                    ElementsCollection cells = rows.get(i).$$("td");

                    if (!cells.get(1).getText().equals(expectedSpends[i].category().name())) {
                        expectedSpend = expectedSpends[i].category().name();
                        String message = String.format(
                                "Spend category mismatch (expected: %s, actual: %s)",
                                expectedSpend, cells.get(1).getText()
                        );
                        return rejected(message, cells.get(1).getText());

                    } else if (!cells.get(2).getText().equals(expectedSpends[i].amount().intValue() + " ₽")) {
                        expectedSpend = expectedSpends[i].amount().intValue() + " ₽";
                        String message = String.format(
                                "Spend amount mismatch (expected: %s, actual: %s)",
                                expectedSpend, cells.get(2).getText()
                        );
                        return rejected(message, cells.get(2).getText());

                    } else if (!cells.get(3).getText().equals(expectedSpends[i].description())) {
                        expectedSpend = expectedSpends[i].description();
                        String message = String.format(
                                "Spend description mismatch (expected: %s, actual: %s)",
                                expectedSpend, cells.get(3).getText()
                        );
                        return rejected(message, cells.get(3).getText());

                    } else if (!cells.get(4).getText().equals(dateFormat(expectedSpends[i].spendDate()))) {
                        expectedSpend = dateFormat(expectedSpends[i].spendDate());
                        String message = String.format(
                                "Spend date mismatch (expected: %s, actual: %s)",
                                expectedSpend, cells.get(4).getText()
                        );
                        return rejected(message, cells.get(4).getText());
                    }
                }
                return accepted();
            }

            @Override
            public String toString() {
                return expectedSpend;
            }
        };
    }

    private static String dateFormat(@Nonnull Date date) {
        final String DATE_FORMAT = "MMM d, yyyy";
        return new SimpleDateFormat(DATE_FORMAT).format(date);
    }

}