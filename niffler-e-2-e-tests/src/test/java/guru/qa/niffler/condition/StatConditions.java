package guru.qa.niffler.condition;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementCondition;
import com.codeborne.selenide.WebElementsCondition;
import com.google.errorprone.annotations.CheckReturnValue;
import lombok.NonNull;
import org.apache.commons.lang.ArrayUtils;
import org.openqa.selenium.WebElement;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

import static com.codeborne.selenide.CheckResult.accepted;
import static com.codeborne.selenide.CheckResult.rejected;

public class StatConditions {
    public static WebElementCondition statBubbles(Colour expectedColour) {
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

    public static WebElementsCondition statBubbles(@NonNull Bubble... bubbles) {
        return new WebElementsCondition() {
            private final String[] expectedColors = Arrays.stream(bubbles).map(bubble -> bubble.colour().rgb).toArray(String[]::new);
            private final String[] expectedTexts = Arrays.stream(bubbles).map(Bubble::text).toArray(String[]::new);

            @Nonnull
            @CheckReturnValue
            @Override
            public CheckResult check(Driver driver, List<WebElement> elements) {
                if (ArrayUtils.isEmpty(bubbles)) {
                    throw new IllegalArgumentException("No expected bubbles given");
                }
                if (bubbles.length != elements.size()) {
                    String message = String.format("List size mismatch (expected: %s, actual: %s)", bubbles.length, elements.size());
                    return rejected(message, elements);
                }
                boolean passed = true;
                List<String> actualRgbaList = new ArrayList<>();
                List<String> actualTextList = new ArrayList<>();

                for (int i = 0; i < elements.size(); i++) {
                    final WebElement elementToCheck = elements.get(i);
                    final String colourToCheck = expectedColors[i];
                    final String textToCheck = expectedTexts[i];
                    final String rgba = elementToCheck.getCssValue("background-color");
                    final String text = elementToCheck.getText();
                    actualRgbaList.add(rgba);
                    actualTextList.add(text);
                    if (passed) {
                        passed = colourToCheck.equals(rgba) && textToCheck.equals(text);
                    }
                }

                if (!passed) {
                    final String actualRgba = actualRgbaList.toString();
                    final String actualText = actualTextList.toString();
                    String message = String.format("Bubble mismatch (expected color: %s, actual color: %s \n" +
                                    "expected text: %s, actual text: %s",
                            expectedColors, actualRgba, expectedTexts, actualText
                    );
                    return rejected(message, "Text: " + actualText + "\nColor: " + actualRgba);
                }

                return accepted();
            }

            @Override
            public String toString() {
                return "Text: " + Arrays.toString(expectedTexts) +
                        "\nColor:" + Arrays.toString(expectedColors);
            }
        };
    }

    public static WebElementsCondition statBubblesInAnyOrder(@Nonnull Bubble... bubbles) {
        return new WebElementsCondition() {
            private final Set<String> expectedColors = Arrays.stream(bubbles).map(bubble -> bubble.colour().rgb).collect(Collectors.toSet());
            private final Set<String> expectedTexts = Arrays.stream(bubbles).map(Bubble::text).collect(Collectors.toSet());

            @Nonnull
            @CheckReturnValue
            @Override
            public CheckResult check(Driver driver, List<WebElement> elements) {

                if (ArrayUtils.isEmpty(bubbles)) {
                    throw new IllegalArgumentException("No expected bubbles given");
                }

                if (bubbles.length != elements.size()) {
                    String message = String.format("List size mismatch (expected: %s, actual: %s)",
                            bubbles.length, elements.size());
                    return rejected(message, elements);
                }

                boolean passed = true;
                HashMap<String, String> actualBubbles = actualBubbles(elements);

                for (Bubble bubble : bubbles) {
                    if (passed) {
                        passed = actualBubbles.values().containsAll(expectedTexts) &&
                                actualBubbles.get(bubble.colour().rgb).equals(bubble.text());
                    }
                }

                if (!passed) {
                    final String actualRgba = actualBubbles.keySet().toString();
                    final String actualText = actualBubbles.values().toString();
                    String message = String.format("Bubble mismatch (expected color: %s, actual color: %s \n" +
                                    "expected text: %s, actual text: %s",
                            expectedColors, actualRgba, expectedTexts, actualText);

                    return rejected(message, "Text: " + actualText + "\nColor: " + actualRgba);
                }

                return accepted();
            }

            @Override
            public String toString() {
                return "Text: " + expectedTexts +
                        "\nColor:" + expectedColors;
            }
        };
    }

    public static WebElementsCondition statBubblesContains(@Nonnull Bubble... bubbles) {
        return new WebElementsCondition() {
            private final String[] expectedColors = Arrays.stream(bubbles).map(bubble -> bubble.colour().rgb).toArray(String[]::new);
            private final String[] expectedTexts = Arrays.stream(bubbles).map(Bubble::text).toArray(String[]::new);

            @Nonnull
            @CheckReturnValue
            @Override
            public CheckResult check(Driver driver, List<WebElement> elements) {

                if (ArrayUtils.isEmpty(bubbles)) {
                    throw new IllegalArgumentException("No expected bubbles given");
                }

                if (bubbles.length > elements.size()) {
                    String message = String.format("List size is more than expected (expected: %s, actual: %s)",
                            bubbles.length, elements.size());
                    return rejected(message, elements);
                }
                boolean passed = true;
                HashMap<String, String> actualBubbles = actualBubbles(elements);

                for (Bubble bubble : bubbles) {
                    if (passed) {
                        passed = actualBubbles.containsValue(bubble.text()) &&
                                actualBubbles.get(bubble.colour().rgb).equals(bubble.text());
                    }
                }

                if (!passed) {
                    final String actualRgba = actualBubbles.keySet().toString();
                    final String actualText = actualBubbles.values().toString();
                    String message1 = String.format("Bubble mismatch (expected color: %s, actual color: %s \n" +
                                    "expected text: %s, actual text: %s",
                            expectedColors, actualRgba, expectedTexts, actualText
                    );
                    return rejected(message1, "Text: " + actualText +
                            "\nColor: " + actualRgba);


                }
                return

                        accepted();
            }

            @Override
            public String toString() {
                return "Text: " + Arrays.toString(expectedTexts) +
                        "\nColor:" + Arrays.toString(expectedColors);
            }
        }

                ;
    }

    private static HashMap<String, String> actualBubbles(List<WebElement> elements) {
        HashMap<String, String> actualBubbles = new HashMap<>();

        for (int i = 0; i < elements.size(); i++) {
            final WebElement elementToCheck = elements.get(i);
            final String rgba = elementToCheck.getCssValue("background-color");
            final String text = elementToCheck.getText();
            actualBubbles.put(rgba, text);
        }
        return actualBubbles;
    }
}
