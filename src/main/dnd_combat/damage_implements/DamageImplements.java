package damage_implements;

import txt_input.Txt5e;
import txt_input.Txt5eReader;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DamageImplements {

    private static final ArrayList<Object> damageImplements = new ArrayList<>();

    public static final Weapon MANUAL_WEAPON = new Weapon("Manual", -1, -1, null);
    public static final Spell MANUAL_HIT = new Spell("Manual with Hit Roll", -1, -1, null, null);
    public static final Spell MANUAL_SAVE = new Spell("Manual with Save Throw", -1, -1, null, null);

    public static void init(URL url) {
        Txt5e code = Txt5eReader.getCode(url);
        assert code != null;

        damageImplements.addAll(code.toList(Weapon.class));
        damageImplements.addAll(code.toList(Spell.class));

        List.of(MANUAL_WEAPON, MANUAL_HIT, MANUAL_SAVE)
                .forEach(manual -> {
                    if (!damageImplements.contains(manual)) {
                        damageImplements.add(manual);
                    }
                });
    }

    @SuppressWarnings("unchecked")
    public static <T> T get(String name, Class<T> type) {
        for (Object obj : damageImplements) {
            String objName = obj.toString().trim();
            if (type.isInstance(obj) && name.equalsIgnoreCase(objName)) {
                return (T) obj;
            }
        }
        return null;
    }

    public static <T> ArrayList<T> toList(Class<T> type) {
        return new ArrayList<>(damageImplements
                .stream()
                .filter(type::isInstance)
                .map(type::cast)
                .toList()
        );
    }

    public static void add(Object obj) {
        switch (obj) {
            case Weapon ignored -> damageImplements.add(obj);
            case Spell ignored -> damageImplements.add(obj);
            default -> throw new IllegalArgumentException("unexpected type");
        }
    }

}
