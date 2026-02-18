package util;

import combat.Main;
import combatants.Combatant;

import java.util.ArrayList;

public class Locators {

    public static ArrayList<Combatant> getTargetList(boolean isForDamage) {
        if (Main.queue.getCurrentCombatant().isEnemy()) {
            if (isForDamage) {
                return Main.battle.friendlies();
            } else {
                return Main.battle.enemies();
            }
        }
        if (isForDamage) {
            return Main.battle.enemies();
        } else {
            return Main.battle.friendlies();
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
