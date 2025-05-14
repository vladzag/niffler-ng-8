package guru.qa.niffler.page.universalComponents;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.condition.Colour;
import guru.qa.niffler.condition.StatConditions;
import lombok.NonNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.Duration;
import java.util.Objects;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static guru.qa.niffler.condition.StatConditions.colour;

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

    private final SelenideElement chart = $("canvas[role='img']");

    @NonNull
    private BufferedImage chartScreenshot() throws IOException {
        return ImageIO.read(Objects.requireNonNull(chart).screenshot());
    }

    public StatComponent checkBubbles(Colour... expectedColours){
        bubbles.should(StatConditions.colour(expectedColours));
        return this;
    };
}
