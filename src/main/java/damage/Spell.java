package damage;

import combatants.Stats;

public enum Spell {

    CHILL_TOUCH("Chill Touch", 1, 8, null, Effect.HEAL_BLOCK),
    POISON_SPRAY("Poison Spray", 1, 12, Stats.stat.CON, null),
    SHOCKING_GRASP("Shocking Grasp", 1, 8, null, null),
    RAY_OF_SICKNESS("Ray of Sickness", 2, 8, null, Effect.POISON),
    THUNDER_WAVE("Thunder Wave", 2, 8, Stats.stat.CON, Effect.HALF_DAMAGE),
    HELLISH_REBUKE("Hellish Rebuke", 2, 10, Stats.stat.DEX, Effect.HALF_DAMAGE),
    PHANTASMAL_FORCE("Phantasmal Force", 2, 8, Stats.stat.INT, Effect.ILLUSION),

    BURNING_HANDS("Burning Hands", 4, 6, Stats.stat.DEX, Effect.HALF_DAMAGE),
    HEX("Hex", 1, 6, Stats.stat.WIS, Effect.BONUS_DAMAGE),
        // The effects of hex technically last for one hour. I decided not to code this because that amounts to 600 turns of
        // combat, and I figured this limit would never reasonably be reached. Be advised.
    SCORCHING_RAY("Scorching Ray", 2, 6, null, Effect.SPLIT_ATTACK),
    ELDRITCH_BLAST("Eldritch Blast", 1, 10, null, null),

    MANUAL_HIT("Manual with Hit Roll", -1, -1, null, null),
    MANUAL_SAVE("Manual with Save DC", -1, -1, null, null);


    private final String name;
    private final int numDamageDice;
    private final int dieSize;
    private final Stats.stat savingThrow;
    private final Effect effect;

    Spell(String name, int numDamageDice, int dieSize, Stats.stat savingThrow, Effect effect) {
        this.name = name;
        this.numDamageDice = numDamageDice;
        this.dieSize = dieSize;
        this.savingThrow = savingThrow;
        this.effect = effect;
    }

    public String getName() {
        return name;
    }

    public String getDamageString() {
        return numDamageDice + "d" + dieSize;
    }

    public int getNumDice() {
        return numDamageDice;
    }

    public int getDieSize() {
        return dieSize;
    }

    public boolean isManual() {
        return equals(MANUAL_SAVE) || equals(MANUAL_HIT);
    }

    public boolean hasSave() {
        return savingThrow != null;
    }

    public Stats.stat getSaveType() {
        return savingThrow;
    }

    public Effect getEffect() {
        return effect;
    }

    public boolean dealsHalfDamageAnyways() {
        return effect.equals(Effect.HALF_DAMAGE);
    }

    public static Spell get(String nameRaw) {
        String[] nameWords = nameRaw.split("_");
        StringBuilder name = new StringBuilder();
        for (String word : nameWords) {
            name.append(word).append(" ");
        }
        for (Spell spell : values()) {
            if (spell.getName().equalsIgnoreCase(name.toString())) {
                return spell;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return name;
    }
}