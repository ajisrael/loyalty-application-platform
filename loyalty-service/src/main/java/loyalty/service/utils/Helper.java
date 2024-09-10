package loyalty.service.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class Helper {

    public static boolean isNullOrBlank(String string) {
        return string == null || string.isBlank();
    }

    public static void throwExceptionIfParameterIsEmpty(String string, String message) {
        if (isNullOrBlank(string)) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void throwExceptionIfParameterIsNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void throwExceptionIfParameterIsNegative(Integer integer, String message) {
        if (integer < 0) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void throwExceptionIfParameterIsNegative(Double doubleValue, String message) {
        if (doubleValue < 0.0) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void throwExceptionIfListParameterIsEmpty(List<?> objects, String message) {
        if (objects.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void throwExceptionIfEntityDoesNotExist(Object entity, String message) {
        if (entity == null) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void throwExceptionIfEntityDoesExist(Object entity, String message) {
        if (entity != null) {
            throw new IllegalArgumentException(message);
        }
    }

    public static String calculateSHA256Hash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(input.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            // Handle the exception accordingly
            e.printStackTrace();
            return null;
        }
    }
}
