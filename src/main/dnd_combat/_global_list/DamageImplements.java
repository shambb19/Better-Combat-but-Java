package _global_list;

import damage_implements.Implement;
import damage_implements.Spell;
import damage_implements.Weapon;

import java.net.URL;
import java.util.ArrayList;

public class DamageImplements extends GlobalList<Implement> {

    public static final Weapon MANUAL_WEAPON = new Weapon("Manual", -1, -1, null);
    public static final Spell MANUAL_HIT = new Spell("Manual with Hit Roll", -1, -1, null, null);
    public static final Spell MANUAL_SAVE = new Spell("Manual with Save Throw", -1, -1, null, null);

    private static final DamageImplements INSTANCE = new DamageImplements();

    private DamageImplements() {
        addItem(MANUAL_WEAPON);
        addItem(MANUAL_HIT);
        addItem(MANUAL_SAVE);
    }

    public static void init(URL url) {
        INSTANCE.init(url, Implement.class);
    }

    public static <T extends Implement> T get(String name, Class<T> type) {
        return INSTANCE.getItem(name, type);
    }

    public static <T extends Implement> ArrayList<T> toList(Class<T> type) {
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
