package guru.qa.niffler.utils;

import lombok.SneakyThrows;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class OauthUtils {

    private static SecureRandom secureRandom = new SecureRandom();


    public static String generateCodeVerifier() {
        byte[] codeVerifier = new byte[32];
        secureRandom.nextBytes(codeVerifier);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(codeVerifier);
    }

    @SneakyThrows
    public static String generateCodeChallenge(String codeVerifier) {
        MessageDigest messageDigest;
        byte[] bytes;
        try {
            bytes = codeVerifier.getBytes(Charset.forName("US-ASCII"));
            messageDigest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        messageDigest.update(bytes, 0, bytes.length);
        byte[] digest = messageDigest.digest();

        return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
    }
}