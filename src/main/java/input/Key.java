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
import java.util.function.Function;
import java.util.stream.Collectors;

import static util.Locators.enumNameSearch;
import static util.TxtReader.listTextAsArray;

@AllArgsConstructor
public enum Key {

    NAME("non-blank String", String::valueOf, o -> o instanceof String s && !s.isBlank()),
    HP("int/int with int > 0", String::valueOf, null),
    AC("int on [1, 30]", Integer::parseInt, o -> o instanceof Integer i && i > 0 && i <= 30),
    LEVEL("int on [1, 20]", Integer::parseInt, o -> o instanceof Integer i && i > 0 && i <= 20),
    CLASS("valid 5e class", value -> enumNameSearch(value, Class5e.class), Class5e.class::isInstance),
    STATS("properly formatted stat line", String::valueOf, null),
    WEAPONS("valid list of 5e weapons", value -> ImplementDecoder.implement(value, Weapon.class), null),
    SPELLS("valid list of 5e weapons", value -> ImplementDecoder.implement(value, Spell.class), null),

    WITH("valid list of defined combatants", String::valueOf, null),
    AGAINST("valid list of defined combatants", String::valueOf, null),

    DMG("String in ndn format", String::valueOf, null),
    STAT("valid stat object or null", value -> enumNameSearch(value, AbilityModifier.class), null),
    EFFECT("valid effect (see Effect.java)", value -> enumNameSearch(value, Effect.class), Effect.class::isInstance),
    CONCENTRATION("boolean", value -> value.trim().equals("true"), Boolean.class::isInstance);

    private static final Map<String, Key> LOOKUP =
            Arrays.stream(values())
                    .collect(Collectors.toMap(k -> k.name().toLowerCase(), k -> k));

    @Getter private final String requirement;
    private final ParameterFactory parameterFactory;
    private final Function<Object, Boolean> validator;

    public boolean isValid(Object query) {
        return validator == null || validator.apply(query);
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