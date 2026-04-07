package util;

import java.util.List;
import java.util.function.Function;

public class Filter {

    public static <T> List<T> matchingClass(List<?> source, Class<T> type) {
        return source.stream()
                .filter(type::isInstance)
                .map(type::cast)
                .toList();
    }

    public static <T> List<T> matchingCondition(List<T> source, Function<T, Boolean> condition) {
        return source.stream().filter(condition::apply).toList();
    }

    public static <T> T firstWithToStringEquals(List<T> source, String equalTo) {
        return source.stream()
                .filter(item -> item.toString().equalsIgnoreCase(equalTo))
                .findFirst()
                .orElse(null);
    }

}
