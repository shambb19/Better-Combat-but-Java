package _global_list;

import combat_object.damage_implements.Implement;
import combat_object.damage_implements.Spell;
import combat_object.damage_implements.Weapon;
import lombok.experimental.*;
import util.Filter;

import java.util.List;

@FieldDefaults(makeFinal = true)
@ExtensionMethod(Filter.class)
public class DamageImplements extends GlobalList<Implement> {

    public static Weapon MANUAL_WEAPON = new Weapon.ManualWeapon("Manual");
    public static Spell MANUAL_HIT = new Spell.ManualSpell("Manual with Hit Roll");
    public static Spell MANUAL_SAVE = new Spell.ManualSpell("Manual with Save Throw");

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

}
