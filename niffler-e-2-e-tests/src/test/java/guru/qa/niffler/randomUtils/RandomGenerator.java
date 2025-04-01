
package guru.qa.niffler.randomUtils;

import com.github.javafaker.Faker;

import java.util.Locale;

public class RandomGenerator {

    static Faker fakerRu = new Faker(new Locale("ru-RU"));
    static Faker fakerEN = new Faker(new Locale("ru-RU"));

    public static String randomAlphanumeric5SymbolString() {
        return fakerEN.regexify("[a-zA-Z0-9]{5}");
    }

    public static String generateRandomText() {
        return fakerRu.letterify("??????????");
    }

    public static String generateRandomLogin() {
        String firstName = fakerEN.name().firstName();
        String middleName = fakerEN.name().firstName();
        String lastName = fakerEN.name().lastName();

        String userName = (firstName.charAt(0) + "" + middleName.charAt(0) + lastName).toLowerCase();

        if (userName.length() > 10) {
            userName = userName.substring(0, 10);
        }
        return userName;

    }
}

