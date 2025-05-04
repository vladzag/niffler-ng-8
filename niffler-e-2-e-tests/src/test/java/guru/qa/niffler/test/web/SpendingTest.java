package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.Spend;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import org.junit.jupiter.api.Test;

@WebTest
public class SpendingTest {

  private static final Config CFG = Config.getInstance();

  @User(
      spendings = @Spend(
          category = "Обучение",
          description = "Обучение Advanced 2.0",
          amount = 79990,
          currency = CurrencyValues.RUB
      )
  )
  @Test
  void categoryDescriptionShouldBeChangedFromTable(UserJson userJson) {
    final String newDescription = "Обучение Niffler Next Generation";

    Selenide.open(CFG.frontUrl(), LoginPage.class)
        .successLogin(userJson.username(), userJson.testData().password())
        .editSpending(userJson.testData().spendings().getFirst().description())
        .editDescription(newDescription)
        .save();

    new MainPage().checkThatTableContainsSpending(newDescription);
  }
}

