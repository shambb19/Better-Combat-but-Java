package input.syntax;


import combat_object.combatant.info.Class5e;
import combat_object.combatant.info.Stats;
import combat_object.implement.Implement;

import java.util.List;
import java.util.Objects;

public class ComplexValidator {

    public static boolean validateStats(Object line) {
        try {
            Stats.from(line, Class5e.BARD, 1);
            return true;
        } catch (RuntimeException ignored) {
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends Implement> boolean validateImplements(Object obj) {
        List<T> list = (List<T>) obj;
        return list.stream().allMatch(Objects::nonNull);
    }

}
