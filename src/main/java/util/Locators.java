package util;

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

}
