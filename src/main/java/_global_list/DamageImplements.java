package _global_list;

import combat_object.implement.Gun;
import combat_object.implement.Implement;
import combat_object.implement.Spell;
import combat_object.implement.Weapon;
import lombok.*;
import lombok.experimental.*;

import java.util.List;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@ExtensionMethod(util.Filterable.class)
public class DamageImplements extends GlobalList<Implement> {

    static DamageImplements INSTANCE = new DamageImplements();

    private DamageImplements() {
        List.of(Weapon.MANUAL, Spell.HIT_MANUAL, Spell.SAVE_MANUAL).forEach(this::add);
    }

    public static void init() {
        INSTANCE.init(Resource.WEAPON_CODE.getUrl(), Weapon.class);
        INSTANCE.init(Resource.SPELL_CODE.getUrl(), Spell.class);
        INSTANCE.init(Resource.GUN_CODE.getUrl(), Gun.class);
    }

    public static <T extends Implement> T get(String name, Class<T> type) {
        return INSTANCE.list.of(type).firstWithToStringEquals(name);
    }

    public static <T extends Implement> List<T> toList(Class<T> type) {
        return INSTANCE.list.of(type).toList();
    }

}
