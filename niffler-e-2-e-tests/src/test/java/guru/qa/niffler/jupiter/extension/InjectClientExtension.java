package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.service.SpendClient;
import guru.qa.niffler.service.UsersClient;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;

import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.reflect.Field;

@ParametersAreNonnullByDefault
public class InjectClientExtension implements TestInstancePostProcessor {

    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext context) throws Exception {
        for (Field field : testInstance.getClass().getDeclaredFields()) {
            if (field.getType().isAssignableFrom(UsersClient.class)) {
                field.setAccessible(true);
                field.set(testInstance, UsersClient.getInstance());
            } else if (field.getType().isAssignableFrom(SpendClient.class)) {
                field.setAccessible(true);
                field.set(testInstance, SpendClient.getInstance());
            }
        }
    }
}