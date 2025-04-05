package guru.qa.niffler.test.web.utils;

import com.github.javafaker.Faker;

import java.util.Locale;
import java.util.Random;

public class RandomDataUtils {

    private static final Random random = new Random();
    private static final Faker fakerRU = new Faker(new Locale("ru-RU"));
    private static final Faker fakerEN = new Faker(new Locale("en-GB"));

    public static String randomUsername() {
        String firstName = fakerEN.name().firstName();
        String middleName = fakerEN.name().firstName();
        String lastName = fakerEN.name().lastName();

        // Составление логина
        String userName = (firstName.charAt(0) + "" + middleName.charAt(0) + lastName).toLowerCase();

        // Ограничение длины логина 10 символами
        if (userName.length() > 10) {
            userName = userName.substring(0, 10);
        }
        return userName;
    }

    public static String randomName() {
        return fakerEN.name().firstName();
    }

    public static String randomSurname() {
        return fakerEN.name().lastName();
    }

    public static String randomCategoryName() {
        return fakerRU.animal().name();
    }

    public static String randomSentence(int wordsCount) {
        return fakerRU.lorem().sentence(wordsCount);
    }
}
