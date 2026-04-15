package combat_object.combatant;

import combat_object.CombatObject;
import lombok.experimental.*;
import txt_input.Key;
import util.TxtReader;

import java.util.ArrayList;
import java.util.EnumMap;

import static txt_input.Key.*;

@SuperBuilder
public class NPC extends Combatant implements CombatObject {

    public static NPC create(String name, int hpMax, int armorClass, boolean isEnemy) {
        return NPC.builder()
                .name(name)
                .maxHp(hpMax)
                .hp(hpMax)
                .armorClass(armorClass)
                .isEnemy(isEnemy)
                .build();
    }

    public static NPC create(String name, NPC source) {
        return create(name, source.maxHp, source.armorClass, source.isEnemy);
    }

    @Override
    public ArrayList<String> toTxt() {
        ArrayList<String> txt = super.toTxt();
        txt.add("");
        return txt;
    }

    public static NPC from(EnumMap<Key, Object> params, boolean isEnemy) {
        return NPC.create(
                (String) params.get(NAME),
                TxtReader.getHp((String) params.get(HP)),
                (int) params.get(AC),
                isEnemy
        );
    }

}
