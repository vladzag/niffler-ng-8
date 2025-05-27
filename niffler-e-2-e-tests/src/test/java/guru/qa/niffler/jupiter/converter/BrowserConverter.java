package guru.qa.niffler.jupiter.converter;

import com.codeborne.selenide.SelenideDriver;
import guru.qa.niffler.utils.SelenideUtils;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.ArgumentConverter;

import static guru.qa.niffler.utils.Browser.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BrowserConverter implements ArgumentConverter {

    @Override
    public SelenideDriver convert(Object source, ParameterContext context) throws ArgumentConversionException {
        assertEquals(SelenideDriver.class, context.getParameter().getType(), "Can only convert to SelenideDriver");

        if (CHROME == source) {
            return new SelenideDriver(SelenideUtils.chromeConfig);
        } else if (FIREFOX == source) {
            return new SelenideDriver(SelenideUtils.firefoxConfig);
        } else if (SAFARI == source) {
            return new SelenideDriver(SelenideUtils.safariConfig);
        } else {
            throw new ArgumentConversionException("Unsupported browser type: " + source);
        }
    }
}