package guru.qa.niffler.api.core;

import com.github.jknack.handlebars.internal.lang3.StringUtils;
import guru.qa.niffler.jupiter.extension.ApiLoginExtension;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

public class CodeInterceptor implements Interceptor {
    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        final Response response = chain.proceed(chain.request());

        if (response.isRedirect()) {
            String location = Objects.requireNonNull(response
                    .header("Location")
            );
            if (location.contains("code=")) {
                ApiLoginExtension.setCode(
                        StringUtils.substringAfter(location, "code=")
                );
            }
        }
        return response;
    }
}