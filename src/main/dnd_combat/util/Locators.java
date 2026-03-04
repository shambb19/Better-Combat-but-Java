package util;

import main.CombatMain;
import character_info.Combatant;

import java.util.ArrayList;

public class Locators {

    public static ArrayList<Combatant> getTargetList(boolean isForDamage) {
        if (CombatMain.QUEUE.getCurrentCombatant().isEnemy()) {
            if (isForDamage) {
                return CombatMain.BATTLE.friendlies();
            } else {
                return CombatMain.BATTLE.enemies();
            }
        }
        if (isForDamage) {
            return CombatMain.BATTLE.enemies();
        } else {
            return CombatMain.BATTLE.friendlies();
        }
    }

    public static Combatant getCombatantWithNameFrom(ArrayList<Combatant> source, String name) {
        for (Combatant combatant : source) {
            if (combatant.name().equals(name)) {
                return combatant;
            }
        }
        return null;
    }

}
