package util;

import __main.manager.EffectManager;
import __main.manager.EncounterManager;
import combat_object.combatant.Combatant;
import combat_object.damage_implements.Effect;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class Locators {

    public static List<Combatant> getTargetList(boolean isForDamage) {
        boolean isEnemy = EncounterManager.getCurrentCombatant().isEnemy();

        List<Combatant> fullList = ((isEnemy == isForDamage) ? EncounterManager.getFriendlies() : EncounterManager.getEnemies())
                .stream().filter(c -> !c.equals(EncounterManager.getCurrentCombatant())).toList();

        if (isForDamage)
            return fullList;
        else
            return fullList.stream()
                    .filter(c -> c.getMaxHp() != c.getHp() && !EffectManager.hasEffect(c, Effect.HEAL_BLOCK))
                    .toList();
    }

    public static <T> T getWithNameFromDirectory(List<T> source, Object obj) {
        return Filter.firstWithToStringEquals(source, obj.toString());
    }

    public static <T extends Enum<T>> T enumNameSearch(String name, Class<T> enumClass) {
        var enumList = Arrays.asList(enumClass.getEnumConstants());
        return Filter.firstWithToStringEquals(enumList, name);
    }

    public static Component componentFromCardLayoutWithKey(JPanel cardPanel, String key) {
        var panels = Arrays.asList(cardPanel.getComponents());

        var allNotNull = Filter.matchingCondition(panels, comp -> comp.getName() != null);

        return Filter.firstWithToStringEquals(allNotNull, key);
    }

}
