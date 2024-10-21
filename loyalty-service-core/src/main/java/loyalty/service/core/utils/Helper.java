package loyalty.service.core.utils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static loyalty.service.core.constants.ExceptionMessages.EMAIL_CANNOT_BE_EMPTY;
import static loyalty.service.core.constants.ExceptionMessages.INVALID_EMAIL_FORMAT;

public class Helper {
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    private Helper() {
        throw new IllegalStateException("utility class");
    }

    public static boolean isNullOrBlank(String string) {
        return string == null || string.isBlank();
    }

    // TODO: Check all usages of this method to see if they should be throwing a custom exception
    public static void throwExceptionIfParameterIsNullOrBlank(String string, String message) {
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

    public static void throwExceptionIfParameterIsNegativeOrZero(Integer integer, String message) {
        if (integer <= 0) {
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

    public static void throwExceptionIfEmailIsInvalid(String email) {
        throwExceptionIfParameterIsNullOrBlank(email, EMAIL_CANNOT_BE_EMPTY);
        Matcher matcher = EMAIL_PATTERN.matcher(email);
        if (!matcher.matches()) {
            throw new IllegalArgumentException(String.format(INVALID_EMAIL_FORMAT, email));
        }
    }
}
