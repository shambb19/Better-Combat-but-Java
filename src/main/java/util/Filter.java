package util;

import combat_object.combatant.Combatant;

import java.util.List;
import java.util.function.Function;

@lombok.experimental.UtilityClass
public class Filter {

    public <T extends Combatant> List<T> filteredByIsEnemy(List<T> query, boolean isEnemy) {
        return query.stream().filter(c -> c.isEnemy() == isEnemy).toList();
    }

    public <T> List<T> castTo(List<?> query, Class<T> type) {
        return query.stream().filter(type::isInstance).map(type::cast).toList();
    }

    public <T> List<T> filteredBy(List<T> query, Function<T, Boolean> condition) {
        return query.stream().filter(condition::apply).toList();
    }

    public <T> T firstWithToStringEquals(List<T> query, String equalTo) {
        return query.stream().filter(item -> item.toString().equalsIgnoreCase(equalTo))
                .findFirst().orElse(null);
    }

}