package damage;

import combatants.Stats;

public enum Spell {

    CHILL_TOUCH("Chill Touch", 1, 8, null, Effect.NECROTIC), //blocks healing until user next turn
    POISON_SPRAY("Poison Spray", 1, 12, Stats.stat.CON, Effect.POISON),
    SHOCKING_GRASP("Shocking Grasp", 1, 8, null, Effect.LIGHTNING),
    RAY_OF_SICKNESS("Ray of Sickness", 2, 8, null, Effect.POISON),
    THUNDER_WAVE("Thunder Wave", 2, 8, Stats.stat.CON, Effect.THUNDER),
    HELLISH_REBUKE("Hellish Rebuke", 2, 10, Stats.stat.DEX, Effect.FIRE), //halved damage if fail
    PHANTASMAL_FORCE("Phantasmal Force", 2, 8, Stats.stat.INT, Effect.PSYCHIC),

    BURNING_HANDS("Burning Hands", 4, 6, Stats.stat.DEX, Effect.FIRE),
    HEX("Hex", 1, 6, Stats.stat.WIS, Effect.NECROTIC),
    SCORCHING_RAY("Scorching Ray", 2, 6, null, Effect.FIRE),
    ELDRITCH_BLAST("Eldritch Blast", 1, 10, null, Effect.FORCE),

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

    @Override
    public String toString() {
        return name;
    }
}