package util;

import __main.CombatMain;
import character_info.combatant.Combatant;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class Locators {

    public static List<Combatant> getTargetList(boolean isForDamage) {
        boolean isEnemy = CombatMain.getCurrentCombatant().isEnemy();
        if (isEnemy == isForDamage) {
            return CombatMain.getFriendlies();
        }
        return CombatMain.getEnemies();
    }

    public static <T> T getWithNameFromDirectory(List<T> source, Object obj) {
        String name = obj.toString().trim();
        for (T t : source) {
            if (t.toString().trim().equals(name)) {
                return t;
            }
        }
        return null;
    }

    public static <T extends Enum<T>> T enumNameSearch(String name, Class<T> enumClass) {
        if (name == null) {
            return null;
        }
        for (T t : enumClass.getEnumConstants()) {
            String tStr = t.toString().trim();
            if (tStr.equalsIgnoreCase(name.trim())) {
                return t;
            }
        }
        return null;
    }

    public static Component componentFromCardLayoutWithKey(JPanel cardPanel, String key) {
        for (Component component : cardPanel.getComponents()) {
            if (component.getName() == null) {
                continue;
            }
            if (component.getName().equalsIgnoreCase(key)) {
                return component;
            }
        }
        return null;
    }

}
