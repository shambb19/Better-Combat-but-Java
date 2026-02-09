package scenarios;

import combatants.Combatant;
import combatants.NPC;
import combatants.PC;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public enum Battle {

    EXAMPLE(
            "Example",
            new Scenario(
                    List.of(
                        new PC("Frodo", 20, 16, false),
                        new PC("Samwise", 14, 11, false),
                        new PC("Aragorn", 26, 16, false),
                        new PC("Legolas", 20, 12, false),
                        new PC("Gimli", 30, 18, false)
                    ),
                    List.of(
                        new NPC("Orc 1", 12, 14, true),
                        new NPC("Orc 2", 14, 14, true),
                        new NPC("Uruk-hai", 26, 18, true),
                        new NPC("Nazgul", 40, 16, true)
                    )
            )
    ),
    EXAMPLE_CAMPAIGN(
        "Example2",
        new Scenario(
                List.of(
                    Party.KARIS.get()        ,
                    Party.BELLADONNA.get(),
                    Party.BRAXTON.get(),
                    Party.DREXEN.get(),
                    Party.ROLLO.get(),
                    Party.EZEKIEL.get(),
                    Party.ENZA.getNPC()
                ),
                List.of(
                    new NPC("George", 400, 20, true)
                )
        )
    );

    private final String name;
    private final Scenario scenario;

    Battle(String name, Scenario scenario) {
        this.name = name;
        this.scenario = scenario;
    }

    public ArrayList<Combatant> getFriendlies() {
        return scenario.getFriendlies();
    }

    public ArrayList<Combatant> getEnemies() {
        return scenario.getEnemies();
    }

    public static Battle get(String name) {
        for (int i = 0; i < values().length; i++) {
            if (values()[i].name.equals(name)) {
                return values()[i];
            }
        }
        return null;
    }

    public boolean areAllEnemiesDefeated() {
        for (Combatant combatant : getEnemies()) {
            if (combatant.getLifeStatus().isConscious()) {
                return false;
            }
        }
        return true;
    }

    public boolean areAllFriendliesDefeated() {
        for (Combatant combatant : getFriendlies()) {
            if (combatant.getLifeStatus().isConscious()) {
                return false;
            }
        }
        return true;
    }

    public String percentToVictory() {
        int healthSumMax = 0;
        int healthSumFinal = 0;
        for (Combatant enemy : getEnemies()) {
            healthSumMax += enemy.getMaximumHealth();
            healthSumFinal += enemy.getCurrentHealth();
        }
        double percentDecimal = (double) (healthSumMax - healthSumFinal) / healthSumMax;
        return new DecimalFormat("##").format(100 * percentDecimal) + "%";
    }

    public String getFinalHealths() {
        StringBuilder string = new StringBuilder();
        for (Combatant partyMember : getFriendlies()) {
            string.append(partyMember.getName()).append(": ");
            if (partyMember.getLifeStatus().isConscious()) {
                string.append(partyMember.getHealthString()).append("\n");
            } else if (partyMember.getLifeStatus().isAlive()) {
                string.append("Unconscious\n");
            } else {
                string.append("Dead\n");
            }
        }
        return string.toString();
    }

}
