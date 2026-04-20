package input;

import _global_list.DamageImplements;
import combat_object.combatant.info.AbilityModifier;
import combat_object.combatant.info.Class5e;
import combat_object.damage_implements.Effect;
import combat_object.damage_implements.Implement;
import combat_object.damage_implements.Spell;
import combat_object.damage_implements.Weapon;
import lombok.*;
import util.TxtReader;

import java.util.*;
import java.util.stream.Collectors;

import static util.Locators.enumNameSearch;
import static util.TxtReader.listTextAsArray;

@AllArgsConstructor
public enum Key {

    NAME, HP,
    AC(Integer::parseInt),
    LEVEL(Integer::parseInt),
    CLASS(value -> enumNameSearch(value, Class5e.class)),
    STATS,
    WEAPONS(value -> ImplementDecoder.implement(value, Weapon.class)),
    SPELLS(value -> ImplementDecoder.implement(value, Spell.class)),

    WITH, AGAINST,

    DMG,
    STAT(value -> enumNameSearch(value, AbilityModifier.class)),
    EFFECT(value -> enumNameSearch(value, Effect.class)),
    CONCENTRATION(value -> value.trim().equals("true"));

    private static final Map<String, Key> LOOKUP =
            Arrays.stream(values())
                    .collect(Collectors.toMap(k -> k.name().toLowerCase(), k -> k));

    private final ParameterFactory parameterFactory;

    Key() {
        parameterFactory = String::valueOf;
    }

    public static Object value(String line) {
        Key key = get(line);
        String value = TxtReader.value(line);

        return key.parameterFactory.get(value);
    }

    public static boolean lineStartsWithKey(String str) {
        return get(str) != null;
    }

    public static Key get(String str) {
        return LOOKUP.get(TxtReader.key(str));
    }

    static final class ImplementDecoder {
        public static <T extends Implement> List<T> implement(String value, Class<T> type) {
            ArrayList<T> list = new ArrayList<>();

            if (value == null) return list;

            for (String name : listTextAsArray(value)) {
                T obj = DamageImplements.get(name, type);

                Optional.ofNullable(obj).ifPresent(list::add);
            }

            list.removeIf(Objects::isNull);
            return list;
        }
    }

    @FunctionalInterface
    interface ParameterFactory {
        Object get(String value);
    }

}