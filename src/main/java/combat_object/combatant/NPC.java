package combat_object.combatant;

import config.Config;
import input.TextReader;
import input.syntax.Key;
import input.syntax.Tag;
import swing.ColorStyles;

import java.awt.*;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
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

    public static List<NPC> createBulk(NPC source, int qty) {
        ArrayList<NPC> list = new ArrayList<>();
        for (int i = 1; i <= qty; i++) {
            NPC copy = create(source.name + " " + i, source.maxHp, source.armorClass, source.isEnemy);
            list.add(copy);
        }
        return list;
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