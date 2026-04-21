package combat_object.combatant;

import __main.exception.InvalidParameterException;
import format.ColorStyles;
import input.Key;
import util.TxtReader;

import java.awt.*;
import java.util.ArrayList;
import java.util.EnumMap;

import static input.Key.*;

@lombok.experimental.SuperBuilder
public class NPC extends Combatant {

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

    public Color getCombatantColor() {
        return isEnemy ? ColorStyles.ENEMY : ColorStyles.FRIENDLY;
    }

    @Override
    public ArrayList<String> toTxt() {
        ArrayList<String> txt = super.toTxt();
        txt.add("");
        return txt;
    }

    public static NPC from(EnumMap<Key, Object> params, boolean isEnemy) {
        params.forEach((key, value) -> {
            if (!key.isValid(value)) throw new InvalidParameterException("NPC", key, value);
        });

        return NPC.create(
                (String) params.get(NAME),
                TxtReader.getHp((String) params.get(HP)),
                (int) params.get(AC),
                isEnemy
        );
    }

}
