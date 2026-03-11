package util;

import _main.CombatMain;
import character_info.combatant.Combatant;
import scenario_info.Scenario;

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
            System.out.println(combatant.name().trim());
            System.out.println(name.trim());
            if (combatant.name().trim().equalsIgnoreCase(name.trim())) {
                return combatant;
            }
        }
        return null;
    }

    public static Scenario getScenarioWithNameFrom(ArrayList<Scenario> source, String name) {
        for (Scenario scenario : source) {
            if (scenario.name().equals(name)) {
                return scenario;
            }
        }
        return null;
    }

}
