package _global_list;

import damage_implements.Implement;
import damage_implements.Spell;
import damage_implements.Weapon;
import txt_input.Reader5e;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DamageImplements {

    private static final ArrayList<Implement> DAMAGE_IMPLEMENTS = new ArrayList<>();

    public static final Weapon MANUAL_WEAPON = new Weapon("Manual", -1, -1, null);
    public static final Spell MANUAL_HIT = new Spell("Manual with Hit Roll", -1, -1, null, null);
    public static final Spell MANUAL_SAVE = new Spell("Manual with Save Throw", -1, -1, null, null);

    public static void init(URL url) {
        List<Implement> inputs = Reader5e.getInstancesFromCode(url, Implement.class);
        DAMAGE_IMPLEMENTS.addAll(inputs);
    }

    @SuppressWarnings("unchecked")
    public static <T> T get(String name, Class<T> type) {
        for (Object obj : DAMAGE_IMPLEMENTS) {
            String objName = obj.toString().trim();
            if (type.isInstance(obj) && name.equalsIgnoreCase(objName)) {
                return (T) obj;
            }
        }
        return null;
    }

    public static <T> ArrayList<T> toList(Class<T> type) {
        return new ArrayList<>(DAMAGE_IMPLEMENTS
                .stream()
                .filter(type::isInstance)
                .map(type::cast)
                .toList()
        );
    }

    public static void add(Implement obj) {
        switch (obj) {
            case Weapon ignored -> DAMAGE_IMPLEMENTS.add(obj);
            case Spell ignored -> DAMAGE_IMPLEMENTS.add(obj);
            default -> throw new IllegalArgumentException("unexpected type");
        }
    }

}
