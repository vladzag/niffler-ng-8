package guru.qa.niffler.condition;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementCondition;
import com.codeborne.selenide.WebElementsCondition;
import lombok.NonNull;
import org.apache.commons.lang.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.codeborne.selenide.CheckResult.accepted;
import static com.codeborne.selenide.CheckResult.rejected;

public class StatConditions {
    public static WebElementCondition colour(Colour expectedColour) {
        return new WebElementCondition("colour") {
            @Override
            public CheckResult check(Driver driver, WebElement webElement) {
                final String rgba = webElement.getCssValue("background-color");

                return new CheckResult(
                        expectedColour.rgb.equals(rgba),
                        rgba
                );
            }
        };
    }

    public static WebElementsCondition colour(@NonNull Colour... expectedColours) {
        return new WebElementsCondition() {
            private final String expectedRgba = Arrays.stream(expectedColours).map(c -> c.rgb).toList().toString();


            @NotNull
            @Override
            public CheckResult check(Driver driver, List<WebElement> elements) {
                if (ArrayUtils.isEmpty(expectedColours)) {
                    throw new IllegalArgumentException("No expected colours given");
                }
                if (expectedColours.length != elements.size()) {
                    String message = String.format("List size mismatch (expected: %s, actual: %s)", expectedColours.length, elements.size());
                    return rejected(message, elements);
                }
                boolean passed = true;
                List<String> actualRgbaList = new ArrayList<>();
                for (int i = 0; i < elements.size(); i++) {
                    final WebElement elementToCheck = elements.get(i);
                    final Colour colourToCheck = expectedColours[i];
                    final String rgba = elementToCheck.getCssValue("background-color");
                    actualRgbaList.add(rgba);
                    if (passed) {
                        passed = colourToCheck.equals(rgba);
                    }
                }

                if (!passed){
                    final String actualRgba = actualRgbaList.toString();
                    String message = String.format(
                            "List colours mismatch (expected: %s, actual: %s)",expectedRgba, actualRgba);
                    return rejected(message, actualRgba);


                }
                return accepted();
            }

            @Override
            public String toString() {
                return expectedRgba;
            }
        };


    }
}
