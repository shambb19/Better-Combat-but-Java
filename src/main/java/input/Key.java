package input;

import _global_list.DamageImplements;
import combat_object.combatant.info.AbilityModifier;
import combat_object.combatant.info.Class5e;
import combat_object.implement.*;
import lombok.*;
import lombok.experimental.*;
import util.StringUtil;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static input.TextReader.listTextAsArray;
import static util.Locators.enumNameSearch;

@AllArgsConstructor
@ExtensionMethod(TextReader.class)
public enum Key {

    // ----- COMBATANTS ----- //
    NAME("non-blank String", String::valueOf, o -> o instanceof String s && !s.isBlank(), null),
    HP("int/int with int > 0", String::valueOf, null, "8/8"),
    AC("int on [1, 30]", StringUtil::toInt, o -> o instanceof Integer i && i > 0 && i <= 30, 1),
    LEVEL("int on [1, 20]", StringUtil::toInt, o -> o instanceof Integer i && i > 0 && i <= 20, 1),
    CLASS("valid 5e class", value -> enumNameSearch(value, Class5e.class), Class5e.class::isInstance, null),
    STATS("properly formatted stat line", String::valueOf, null, null),
    WEAPONS("valid list of 5e weapons", value -> ImplementDecoder.implement(value, Weapon.class), null, new ArrayList<>()),
    SPELLS("valid list of 5e weapons", value -> ImplementDecoder.implement(value, Spell.class), null, new ArrayList<>()),
    GUNS("valid list of 1848 guns", value -> ImplementDecoder.implement(value, Gun.class), null, new ArrayList<>()),

    // ----- SCENARIOS ----- //
    WITH("valid list of defined combatants", String::valueOf, null, null),
    AGAINST("valid list of defined combatants", String::valueOf, null, null),

    // ----- SPELLS/WEAPONS ----- //
    DMG("String in ndn format", String::valueOf, null, "1d4"),
    STAT("valid stat object or null", value -> enumNameSearch(value, AbilityModifier.class), null, null),
    EFFECT("valid effect (see Effect.java)", value -> enumNameSearch(value, Effect.class), Effect.class::isInstance, Effect.NONE),
    CONCENTRATION("boolean", value -> value.trim().equals("true"), Boolean.class::isInstance, false),

    // ----- GUNS ----- //
    RANGE_DC("String in n/n format", String::valueOf, null, "10/10"),
    MISFIRE("int on [1,5]", StringUtil::toInt, o -> o instanceof Integer i && i > 0 && i <= 5, 1),
    COVER("int on [0, 20]", StringUtil::toInt, o -> o instanceof Integer i && i > 0 && i <= 20, 5),
    SHOTS("int > 0", StringUtil::toInt, o -> o instanceof Integer i && i > 0, 1),
    HEAVY("boolean", value -> value.trim().equals("true"), Boolean.class::isInstance, false),
    EXPLOSIVE("boolean", value -> value.trim().equals("true"), Boolean.class::isInstance, false);

    private static final Map<String, Key> LOOKUP =
            Arrays.stream(values())
                    .collect(Collectors.toMap(k -> k.name().toLowerCase(), k -> k));

    @Getter private final String requirement;
    private final ParameterFactory parameterFactory;
    private final Function<Object, Boolean> validator;
    @Getter private final Object defaultValue;

    public boolean isValid(Object query) {
        return validator == null || validator.apply(query);
    }

    public static Object value(String line) {
        Key key = get(line);
        String value = TextReader.value(line);

        return key.parameterFactory.get(value);
    }

    public static boolean lineStartsWithKey(String str) {
        if (str.isComments() || str.isBlank()) return false;

        return LOOKUP.containsKey(str.key());
    }

    public static Key get(String str) {
        return LOOKUP.get(str.key());
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