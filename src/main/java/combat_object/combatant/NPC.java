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

    /**
     * Returns an NPC object from the given parameters
     * @param name the npc's name
     * @param hpMax the npc's maximum hp
     * @param armorClass the npc's armor class
     * @param isEnemy whether the npc is an enemy
     * @return the new NPC object
     */
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

    /**
     * Returns a list of carbon copies of the given NPC object. There is
     * a number of carbon copies equal to the given quantity, and all copies
     * are identical, except for a number appended to its name. The original
     * NPC object is not included in the returned list.
     * @param source the NPC object to copy
     * @param qty the number of carbon copies to create
     * @return the list of carbon copies
     */
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

    @Override public String getHealthBarString() {
        if (isEnemy && isAlive()) {
            double ratio = getHpRatio();
            if (ratio > 0.7) return "Healthy";
            if (ratio > 0.50) return "Injured";
            if (ratio > 0.25) return "Bloodied";
            return "Critical";
        }
        return super.getHealthBarString();
    }

    /**
     * Returns an NPC object from the given parameters
     * @param params the NPC instance variables, encoded as Objects
     *               and decoded in the Key class
     * @param tags the tags the new NPC has
     * @param isEnemy whether the new NPC is an enemy
     * @return the new NPC object
     */
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