package _global_list;

import combat_object.combatant.AbilityModifier;
import combat_object.damage_implements.Implement;
import combat_object.damage_implements.Spell;
import combat_object.damage_implements.Weapon;
import lombok.experimental.*;
import util.Filter;

import java.util.List;
import java.util.stream.Stream;

@FieldDefaults(makeFinal = true)
@ExtensionMethod(Filter.class)
public class DamageImplements extends GlobalList<Implement> {

    public static Implement MANUAL_WEAPON
            = new Implement("Manual", 1, 100, AbilityModifier.OPTION, true);
    public static Implement MANUAL_HIT
            = new Implement("Manual with Hit Roll", 1, 100, null, true);
    public static Implement MANUAL_SAVE
            = new Implement("Manual with Save Throw", 1, 100, null, true);

    private static final DamageImplements INSTANCE = new DamageImplements();

    private DamageImplements() {
        List.of(MANUAL_WEAPON, MANUAL_HIT, MANUAL_SAVE).forEach(this::add);
    }

    public static void init() {
        INSTANCE.init(Resource.WEAPON_CODE.getUrl(), Weapon.class);
        INSTANCE.init(Resource.SPELL_CODE.getUrl(), Spell.class);
    }

    public static <T extends Implement> T get(String name, Class<T> type) {
        return INSTANCE.list.matchingClass(type).firstWithToStringEquals(name);
    }

    public static <T extends Implement> List<T> toList(Class<T> type) {
        return INSTANCE.list.matchingClass(type);
    }

    public static boolean isManual(String name) {
        return Stream.of(MANUAL_WEAPON, MANUAL_HIT, MANUAL_SAVE).anyMatch(m -> m.getName().equals(name));
    }

}
