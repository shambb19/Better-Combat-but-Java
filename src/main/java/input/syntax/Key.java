package input.syntax;

import _global_list.DamageImplements;
import combat_object.combatant.info.AbilityModifier;
import combat_object.combatant.info.Class5e;
import combat_object.implement.*;
import config.Config;
import config.ruleset.Ruleset;
import exception.InvalidParameterException;
import input.TextReader;
import lombok.*;
import lombok.experimental.*;
import util.StringUtil;

import java.util.*;
import java.util.function.Function;

import static input.TextReader.listTextAsArray;
import static util.Locators.enumNameSearch;

@AllArgsConstructor @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@ExtensionMethod(TextReader.class) @Getter
public enum Key {

    //
    // ----- COMBATANTS ----- //
    //
    NAME("non-blank String",
            String::valueOf, o -> o instanceof String s && !s.isBlank(),
            null, RequirementType.STANDARD_ONLY,
            "name: "),

    HP("int/int with int > 0",
            String::valueOf, null,
            "8/8", RequirementType.STANDARD_ONLY,
            "hp: "),

    AC("int on [1, 30]",
            StringUtil::toInt, o -> o instanceof Integer i && i > 0 && i <= 30,
            1, RequirementType.STANDARD_ONLY,
            "ac: "),

    LEVEL("int on [1, 20]",
            StringUtil::toInt, o -> o instanceof Integer i && i > 0 && i <= 20,
            1, RequirementType.TRUE,
            "level: "),

    CLASS("5e class allowed in selected ruleset",
            value -> enumNameSearch(value, Class5e.class),
            c -> c instanceof Class5e c5 && Config.getRuleset().getAllowedClasses().contains(c5),
            null, RequirementType.TRUE,
            "class: "),

    STATS("properly formatted stat line",
            String::valueOf, ComplexValidator::validateStats,
            null, RequirementType.TRUE,
            "stats: [STR: , DEX: , CON: , INT: , WIS: , CHA: ]"),

    WEAPONS("valid list of 5e weapons",
            value -> ImplementDecoder.decodeImplements(value, Weapon.class), ComplexValidator::validateImplements,
            new ArrayList<>(), RequirementType.FALSE,
            "weapons: []"),

    SPELLS("valid list of 5e weapons",
            value -> ImplementDecoder.decodeImplements(value, Spell.class), ComplexValidator::validateImplements,
            new ArrayList<>(), RequirementType.FALSE,
            "spells: []"),

    GUNS("valid list of 1848 guns",
            value -> ImplementDecoder.decodeImplements(value, Gun.class), ComplexValidator::validateImplements,
            new ArrayList<>(), RequirementType.FALSE,
            "guns: []"),

    //
    // ----- SCENARIOS ----- //
    //
    WITH("valid list of defined combatants",
            String::valueOf, null,
            null, RequirementType.FALSE,
            "with: []"),

    AGAINST("valid list of defined combatants",
            String::valueOf, null,
            null, RequirementType.TRUE,
            "against: []"),

    //
    // ----- SPELLS/WEAPONS ----- //
    //
    DMG("String in ndn format",
            String::valueOf, null,
            "1d4", RequirementType.TRUE,
            "dmg: "),

    STAT("valid stat object or null",
            value -> enumNameSearch(value, AbilityModifier.class), null,
            null, RequirementType.FALSE,
            "stat: "),

    EFFECT("valid effect (see Effect.java)",
            value -> enumNameSearch(value, Effect.class), Effect.class::isInstance,
            Effect.NONE, RequirementType.FALSE,
            "+effect: "),

    CONCENTRATION("boolean",
            value -> value.trim().equals("true"), Boolean.class::isInstance,
            false, RequirementType.FALSE,
            "+concentration"),

    // ----- GUNS ----- //
    HIT("String in n/n format",
            String::valueOf, null,
            "10/10", RequirementType.TRUE,
            "hit: "),

    MISFIRE("int on [1,5]",
            StringUtil::toInt, o -> o instanceof Integer i && i > 0 && i <= 5,
            1, RequirementType.TRUE,
            "misfire: "),

    COVER("int on [0, 20]",
            StringUtil::toInt, o -> o instanceof Integer i && i > 0 && i <= 20,
            5, RequirementType.TRUE,
            "cover: "),

    SHOTS("int > 0",
            StringUtil::toInt, o -> o instanceof Integer i && i > 0,
            1, RequirementType.TRUE,
            "shots: "),

    HEAVY("boolean",
            value -> value.trim().equals("true"), Boolean.class::isInstance,
            false, RequirementType.FALSE,
            "+heavy"),

    EXPLOSIVE("boolean",
            value -> value.trim().equals("true"), Boolean.class::isInstance,
            false, RequirementType.FALSE,
            "+explosive");

    static final Map<String, List<Key>> headerParameterMap = Map.of(
            ".party", List.of(HP, AC, LEVEL, CLASS, STATS, WEAPONS, SPELLS, GUNS),
            ".npc", List.of(HP, AC),
            ".enemy", List.of(HP, AC),
            ".scenario", List.of(WITH, AGAINST),
            ".weapon", List.of(DMG, STAT),
            ".spell", List.of(DMG, STAT, EFFECT, CONCENTRATION),
            ".gun", List.of(HIT, MISFIRE, COVER, SHOTS, HEAVY, EXPLOSIVE)
    );

    String requirement;
    @Getter(AccessLevel.NONE) ParameterFactory parameterFactory;
    @Getter(AccessLevel.NONE) Function<Object, Boolean> validator;
    Object defaultValue;
    RequirementType requirementType;
    String autofillLine;

    public boolean isValueInvalid(Object query) {
        return validator != null && !validator.apply(query);
    }

    public static Object getAppropriateValueFromLine(String line) {
        Key key = getKeyFromString(line);
        String value = TextReader.value(line);

        return key.parameterFactory.get(value);
    }

    public static boolean lineStartsWithKey(String str) {
        if (str.isComments() || str.isBlank()) return false;

        return enumNameSearch(str.key(), Key.class) != null;
    }

    public static Key getKeyFromString(String query) {
        return enumNameSearch(query.key(), Key.class);
    }

    public static void validateAll(EnumMap<Key, Object> params, String source) {
        params.forEach((k, value) -> {
            if (k.isValueInvalid(value))
                throw new InvalidParameterException(source, k.name().toLowerCase(), value, k.getRequirement());
        });
    }

    public static List<Key> getRequiredParametersFor(String header, Ruleset activeRuleset) {
        return getAllParametersFor(header).stream().filter(k -> k.requirementType.validator.apply(activeRuleset)).toList();
    }

    public static List<Key> getAllParametersFor(String header) {
        return headerParameterMap.get(header);
    }

    @UtilityClass static final class ImplementDecoder {
        <T extends Implement> List<T> decodeImplements(String value, Class<T> type) {
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

    @FunctionalInterface interface ParameterFactory {
        Object get(String value);
    }

    @AllArgsConstructor public enum RequirementType {
        TRUE(r -> true), FALSE(r -> false),
        STANDARD_ONLY(r -> r.equals(Ruleset.STANDARD_RULESET));

        final Function<Ruleset, Boolean> validator;
    }

}