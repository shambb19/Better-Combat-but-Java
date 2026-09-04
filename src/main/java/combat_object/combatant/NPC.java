package combat_object.combatant;

import config.Config;
import input.TextReader;
import input.syntax.Key;
import input.syntax.Tag;
import swing.ColorStyles;

import java.awt.*;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Set;

import static input.syntax.Key.*;

@lombok.experimental.SuperBuilder
public class NPC extends Combatant {

    public static NPC create(String name, int hpMax, int armorClass, boolean isEnemy) {
        return NPC.builder()
                .name(name)
                .maxHp(hpMax)
                .hp(hpMax)
                .armorClass(armorClass)
                .isEnemy(isEnemy)
                .isArmored(false)
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

    public static NPC from(EnumMap<Key, Object> params, Set<Tag> tags, boolean isEnemy) {
        validateAll(params, "NPC");
        Config.getRuleset().validateCombatant(params, tags);

        return NPC.builder()
                .name((String) params.get(NAME))
                .maxHp(TextReader.getHp(params.get(HP), true))
                .hp(TextReader.getHp(params.get(HP), false))
                .armorClass((int) params.get(AC))
                .isEnemy(isEnemy)
                .isArmored(tags.contains(Tag.ARMORED))
                .build();
    }

}