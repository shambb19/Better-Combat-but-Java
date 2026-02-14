package scenarios;

import combatants.Combatant;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public enum Battle {

    EXAMPLE(
            "Example",
            new Scenario(
                    List.of(
                        new Combatant("Frodo", 20, 16, false),
                        new Combatant("Samwise", 14, 11, false),
                        new Combatant("Aragorn", 26, 16, false),
                        new Combatant("Legolas", 20, 12, false),
                        new Combatant("Gimli", 30, 18, false)
                    ),
                    List.of(
                        new Combatant("Orc 1", 12, 14, true),
                        new Combatant("Orc 2", 14, 14, true),
                        new Combatant("Uruk-hai", 26, 18, true),
                        new Combatant("Nazgul", 40, 16, true)
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
                    Party.ENZA.get()
                ),
                List.of(
                    new Combatant("George", 400, 20, true)
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
            if (combatant.lifeStatus().isConscious()) {
                return false;
            }
        }
        return true;
    }

    public boolean areAllFriendliesDefeated() {
        for (Combatant combatant : getFriendlies()) {
            if (combatant.lifeStatus().isConscious()) {
                return false;
            }
        }
        return true;
    }

    public String percentToVictory() {
        int healthSumMax = 0;
        int healthSumFinal = 0;
        for (Combatant enemy : getEnemies()) {
            healthSumMax += enemy.maxHp();
            healthSumFinal += enemy.hp();
        }
        double percentDecimal = (double) (healthSumMax - healthSumFinal) / healthSumMax;
        return new DecimalFormat("##").format(100 * percentDecimal) + "%";
    }

    public String getFinalHealths() {
        StringBuilder string = new StringBuilder();
        for (Combatant partyMember : getFriendlies()) {
            string.append(partyMember.name()).append(": ");
            if (partyMember.lifeStatus().isConscious()) {
                string.append(partyMember.getHealthString()).append("\n");
            } else if (partyMember.lifeStatus().isAlive()) {
                string.append("Unconscious\n");
            } else {
                string.append("Dead\n");
            }
        }
        return string.toString();
    }

}
