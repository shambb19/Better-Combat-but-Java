package _global_list;

import character_info.AbilityModifier;
import damage_implements.Implement;
import damage_implements.Spell;
import damage_implements.Weapon;

import java.util.List;

public class DamageImplements extends GlobalList<Implement> {

    public static final Weapon MANUAL_WEAPON = new Weapon("Manual", 1, 100, AbilityModifier.OPTION);
    public static final Spell MANUAL_HIT = new Spell("Manual with Hit Roll", 1, 100, null, null);
    public static final Spell MANUAL_SAVE = new Spell("Manual with Save Throw", 1, 100, null, null);

    private static final DamageImplements INSTANCE = new DamageImplements();

    private DamageImplements() {
        addItem(MANUAL_WEAPON);
        addItem(MANUAL_HIT);
        addItem(MANUAL_SAVE);
    }

    public static void init() {
        INSTANCE.init(Resource.WEAPON_CODE.url(), Weapon.class);
        INSTANCE.init(Resource.SPELL_CODE.url(), Spell.class);
    }

    public static <T extends Implement> T get(String name, Class<T> type) {
        return INSTANCE.getItem(name, type);
    }

    public static <T extends Implement> List<T> toList(Class<T> type) {
        return INSTANCE.castToList(type);
    }

    public static void add(Implement obj) {
        try {
            INSTANCE.addItem(obj);
        } catch (Exception e) {
            System.err.println("Unexpected class for 'obj' in DamageImplements.add(obj)");
        }
    }

}
