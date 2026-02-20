package combatants;

import combat.Main;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Highlights {

    private static ArrayList<Combatant> everyone;

    public static String get() {
        everyone = new ArrayList<>();
        everyone.addAll(Main.battle.friendlies());
        everyone.addAll(Main.battle.enemies());

        Map<Combatant, String> highlights = new HashMap<>();

        largestPercent(highlights);
        mostDamage(highlights);
        mostHealsGiven(highlights);
        mostHealed(highlights);
        hits(highlights, true);
        hits(highlights, false);
        mostInspiration(highlights);

        StringBuilder str = new StringBuilder();
        highlights.forEach((combatant, highlight) -> str.append(highlight).append("\n"));
        return str.toString();
    }

    private static void mostDamage(Map<Combatant, String> highlights) {
        Combatant mostDamage = everyone.getFirst();
        for (Combatant combatant : everyone) {
            if (combatant.getTotalDamageDealt() > mostDamage.getTotalDamageDealt()) {
                mostDamage = combatant;
            }
        }
        String highlight = "American High School-er: " + mostDamage.name() +
                " dealt " + mostDamage.getTotalDamageDealt() + " damage!";
        highlights.put(mostDamage, highlight);
    }

    private static void mostHealsGiven(Map<Combatant, String> highlights) {
        Combatant mostHeals = everyone.getFirst();
        for (Combatant combatant : everyone) {
            if (combatant.getTotalHealsGiven() < mostHeals.getTotalHealsGiven()) {
                mostHeals = combatant;
            }
        }
        String highlight = "Wannabe Nursing Major: " + mostHeals.name() +
                " healed teammates for " + mostHeals.getTotalHealsGiven() + " hp!";
        if (mostHeals.getTotalHealsGiven() > 0) {
            highlights.put(mostHeals, highlight);
        }
    }

    private static void mostHealed(Map<Combatant, String> highlights) {
        Combatant mostHealed = everyone.getFirst();
        for (Combatant combatant : everyone) {
            if (combatant.getTotalHealsReceived() < mostHealed.getTotalHealsReceived()) {
                mostHealed = combatant;
            }
        }
        String highlight = "Health Junkie: " + mostHealed.name() +
                " was healed for " + mostHealed.getTotalHealsReceived() + " hp!";
        if (mostHealed.getTotalHealsReceived() > 0) {
            highlights.put(mostHealed, highlight);
        }
    }

    private static void largestPercent(Map<Combatant, String> highlights) {
        int totalDamage = 0;
        for (Combatant combatant : everyone) {
            if (combatant.isEnemy()) {
                totalDamage += combatant.getTotalDamageDealt();
            }
        }

        Combatant largestPercentCombatant = everyone.getFirst();
        double largestPercent = (double) largestPercentCombatant.getTotalDamageDealt() / totalDamage;
        for (Combatant combatant : everyone) {
            if (!combatant.isEnemy()) {
                double percent = (double) combatant.getTotalDamageDealt() / totalDamage;
                if (percent > largestPercent) {
                    largestPercent = percent;
                }
            }
        }
        String percentRounded = new DecimalFormat("##").format(100 * largestPercent);
        String highlight = "MVP: " + largestPercentCombatant.name() +
                " damaged " + percentRounded + "% of the enemy team's total health!";
        highlights.put(largestPercentCombatant, highlight);
    }

    private static void hits(Map<Combatant, String> highlights, boolean isMost) {
        Combatant mostHits = everyone.getFirst();
        double mostHitsPercent = getHitPercent(everyone.getFirst());
        Combatant leastHits = everyone.getFirst();
        double leastHitsPercent = getHitPercent(everyone.getFirst());

        for (Combatant combatant : everyone) {
            int totalAttacks = combatant.getTotalAttackSuccesses() + combatant.getTotalAttackFails();
            double percent = (double) combatant.getTotalAttackSuccesses() / totalAttacks;
            if (percent > mostHitsPercent) {
                mostHitsPercent = percent;
                mostHits = combatant;
            } else if (percent < leastHitsPercent) {
                leastHitsPercent = percent;
                leastHits = combatant;
            }
        }
        Combatant combatant;
        String highlight;
        String percentRounded;
        if (isMost) {
            percentRounded = new DecimalFormat("##").format(100 * mostHitsPercent);
            combatant = mostHits;
            highlight = "Han Rollo: " + combatant.name() +
                    " hit " + percentRounded + "% of their attacks!";
        } else {
            percentRounded = new DecimalFormat("##").format(100 * leastHitsPercent);
            combatant = leastHits;
            highlight = "Dice Death Penalty: " + combatant.name() +
                    " hit only " + percentRounded + "% of their attacks.";
        }
        highlights.put(combatant, highlight);
    }

    private static void mostInspiration(Map<Combatant, String> highlights) {
        Combatant mostInspiration = everyone.getFirst();
        for (Combatant combatant : everyone) {
            if (combatant.getInspirationRolls() > mostInspiration.getInspirationRolls()) {
                mostInspiration = combatant;
            }
        }
        String highlight = "Gambling Addict: " + mostInspiration.name() +
                "Contributed " + mostInspiration.getInspirationRolls() + " points to the inspiration total!";
        if (mostInspiration.getInspirationRolls() > 4) {
            highlights.put(mostInspiration, highlight);
        }
    }

    private static double getHitPercent(Combatant combatant) {
        return (double) combatant.getTotalAttackSuccesses()
                / (combatant.getTotalAttackSuccesses() + combatant.getTotalAttackFails());
    }
}
