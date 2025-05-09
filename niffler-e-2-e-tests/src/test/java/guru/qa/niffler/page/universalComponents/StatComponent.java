package guru.qa.niffler.page.universalComponents;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;

public class StatComponent {

    private final SelenideElement img = $("canvas[role='img']");

    private final ElementsCollection bubbles = $("#legend-container").$$("li");

    public SelenideElement pieChartImage() {
        return img;
    }

    public StatComponent checkBubblesHasText(String description) {
        bubbles.find(text(description))
                .should(visible);
        return this;
    }

    public StatComponent waitForPieChartToLoad() {
        img.is(image, Duration.ofSeconds(5));
        return this;
    }
}
