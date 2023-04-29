package ma.ac.uir.projets8.util;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Objects;

public class NullChecker {
    public static boolean allNull(Object target) {
        return Arrays.stream(target.getClass()
                        .getDeclaredFields())
                .peek(f -> f.setAccessible(true))
                .map(f -> getFieldValue(f, target))
                .allMatch(Objects::isNull);
    }

    public static boolean hasNull(Object target) {
        return Arrays.stream(target.getClass()
                        .getDeclaredFields())
                .peek(f -> f.setAccessible(true))
                .map(f -> getFieldValue(f, target))
                .anyMatch(Objects::isNull);
    }

    private static Object getFieldValue(Field field, Object target) {
        try {
            return field.get(target);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
