package util;

import __main.manager.EncounterManager;
import character_info.combatant.Combatant;

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
            return fullList.stream().filter(combatant -> combatant.maxHp() != combatant.hp()).toList();
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
