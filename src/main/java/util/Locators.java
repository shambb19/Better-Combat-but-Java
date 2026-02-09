package util;

import combat.Main;
import combatants.Combatant;

import java.util.ArrayList;

public class Locators {

    public static Combatant locateCombatant(ArrayList<Combatant> team, String target) {
        for (Combatant combatant : team) {
            if (combatant.getName().equals(target)) {
                return combatant;
            }
        }
        return null;
    }

    public static ArrayList<Combatant> getTargetList(boolean isForDamage) {
        if (Main.queue.getCurrentCombatant().isEnemy()) {
            if (isForDamage) {
                return Main.battle.getFriendlies();
            } else {
                return Main.battle.getEnemies();
            }
        }
        if (isForDamage) {
            return Main.battle.getEnemies();
        } else {
            return Main.battle.getFriendlies();
        }
    }

    public static Combatant getCombatantWithNameFrom(ArrayList<Combatant> source, String name) {
        for (Combatant combatant : source) {
            if (combatant.getName().equals(name)) {
                return combatant;
            }
        }
        return null;
    }

}
