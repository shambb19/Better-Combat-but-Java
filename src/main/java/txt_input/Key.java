package txt_input;

import character_info.AbilityModifier;
import character_info.Class5e;
import damage_implements.Effect;
import util.TxtReader;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import static util.Locators.enumNameSearch;

public enum Key {

    NAME, HP,
    AC(Integer::parseInt),
    LEVEL(Integer::parseInt),
    CLASS(value -> enumNameSearch(value, Class5e.class)),
    STATS,
    WEAPONS(Decoder::weapons),
    SPELLS(Decoder::spells),

    WITH, AGAINST,

    DMG,
    STAT(value -> enumNameSearch(value, AbilityModifier.class)),
    EFFECT(value -> enumNameSearch(value, Effect.class));

    private static final Map<String, Key> LOOKUP =
            Arrays.stream(values())
                    .collect(Collectors.toMap(k -> k.keyName, k -> k));

    private final String keyName;
    private final ParameterFactory parameterFactory;

    Key(ParameterFactory parameterFactory) {
        this.keyName = name().toLowerCase();
        this.parameterFactory = parameterFactory;
    }

    Key() {
        keyName = name().toLowerCase();
        parameterFactory = String::valueOf;
    }

    public static Object value(String line) {
        Key key = get(line);
        String value = TxtReader.value(line);

        assert key != null;
        return key.parameterFactory.get(value);
    }

    public static boolean lineStartsWithKey(String str) {
        return get(str) != null;
    }

    public static Key get(String str) {
        return LOOKUP.get(TxtReader.key(str));
    }

}

@FunctionalInterface
interface ParameterFactory {
    Object get(String value);
}