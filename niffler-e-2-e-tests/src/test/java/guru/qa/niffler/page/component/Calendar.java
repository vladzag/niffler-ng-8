package guru.qa.niffler.page.component;

import com.codeborne.selenide.SelenideElement;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Optional;

import static com.codeborne.selenide.SetValueOptions.withDate;

public class Calendar {

    private final SelenideElement self;

    public Calendar(SelenideElement self) {
        this.self = self;
    }

    public Calendar selectDateInCalendar(Date date) {
        String dateFormat = Optional.ofNullable(
                self.$("input[name='date']").getAttribute("value")
        ).orElse("MM/DD/YYYY");
        LocalDate spendingDate = LocalDate.parse(date.toString(),
                DateTimeFormatter.ofPattern(dateFormat));
        self.$("input[name='date']").setValue(
                withDate(spendingDate)
        );
        return this;
    }
}
