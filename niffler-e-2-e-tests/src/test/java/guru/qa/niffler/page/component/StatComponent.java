package guru.qa.niffler.page.component;

import com.codeborne.selenide.*;
import guru.qa.niffler.condition.Bubble;
import guru.qa.niffler.condition.Colour;
import guru.qa.niffler.condition.StatConditions;
import guru.qa.niffler.utils.ScreenDiffResult;
import lombok.NonNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.Duration;
import java.util.Objects;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class StatComponent {

    private final SelenideElement img;

    private final ElementsCollection bubbles;

    public StatComponent(SelenideDriver driver) {
        this.img = $("canvas[role='img']");
        this.bubbles = $("#legend-container").$$("li");
    }

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

    public StatComponent checkBubbles(Bubble... expectedBubbles) {
        bubbles.should(StatConditions.statBubbles(expectedBubbles));
        return this;
    }

    public StatComponent checkBubblesInAnyOrder(Bubble... expectedBubbles) {
        bubbles.should(StatConditions.statBubblesInAnyOrder(expectedBubbles));
        return this;
    }

    public StatComponent checkBubblesContains(Bubble... expectedBubbles) {
        bubbles.should(StatConditions.statBubblesContains(expectedBubbles));
        return this;
    }

    public StatComponent checkBubblesContainsText(String... strings) {
        bubbles.should(CollectionCondition.texts(strings));
        return this;
    }

    public StatComponent checkStatisticImage(BufferedImage expectedImage) throws IOException {
        Selenide.sleep(2000);
        assertFalse(
                new ScreenDiffResult(
                        chartScreenshot(),
                        expectedImage
                ), "Screen comparison failure"
        );
        return this;
    }

    public StatComponent checkBubbles(Colour... expectedColors) {
        bubbles.should(colour(expectedColors));
        return this;
    }
}
