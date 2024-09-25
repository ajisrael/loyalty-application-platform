package loyalty.service.core.utils;

import java.util.List;

public class Helper {

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
}
