package _global_list;

import combat_object.implement.Gun;
import combat_object.implement.Implement;
import combat_object.implement.Spell;
import combat_object.implement.Weapon;
import lombok.*;
import lombok.experimental.*;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@ExtensionMethod(util.Filterable.class)
public class DamageImplements extends GlobalList<Implement> {

    static Weapon MANUAL_WEAPON = Weapon.createManual("Manual");
    static Spell MANUAL_HIT = Spell.createManual("Manual with Hit Roll");
    static Spell MANUAL_SAVE = Spell.createManual("Manual with Save Throw");
    static Gun MANUAL_GUN = Gun.createManual("Manual");

    static DamageImplements INSTANCE = new DamageImplements();

    private DamageImplements() {
        List.of(MANUAL_WEAPON, MANUAL_HIT, MANUAL_SAVE).forEach(this::add);
    }

    public static void init() {
        INSTANCE.init(Resource.WEAPON_CODE.getUrl(), Weapon.class);
        INSTANCE.init(Resource.SPELL_CODE.getUrl(), Spell.class);
        INSTANCE.init(Resource.GUN_CODE.getUrl(), Gun.class);
    }

    public static <T extends Implement> T get(String name, Class<T> type) {
        return INSTANCE.list.of().castTo(type).firstWithToStringEquals(name);
    }

    public static <T extends Implement> List<T> toList(Class<T> type) {
        return INSTANCE.list.of().castToAsList(type);
    }

    public static <T extends Implement> Implement createHeader(Class<T> type) {
        Map<Class<? extends Implement>, Function<String, ? extends Implement>> headerMap = Map.of(
                Weapon.class, Weapon::createManual,
                Spell.class, Spell::createManual,
                Gun.class, Gun::createManual
        );
        String headerText = String.format("── %ss ──", type.getSimpleName());
        return headerMap.get(type).apply(headerText);
    }

    public static List<? extends Implement> getManualEntries(Class<? extends Implement> type) {
        Map<Class<? extends Implement>, List<? extends Implement>> entriesMap = Map.of(
                Weapon.class, List.of(MANUAL_WEAPON),
                Spell.class, List.of(MANUAL_HIT, MANUAL_SAVE),
                Gun.class, List.of(MANUAL_GUN)
        );
        return entriesMap.get(type);
    }

}
