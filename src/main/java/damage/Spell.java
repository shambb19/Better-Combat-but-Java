package damage;

import combatants.Stats;

public enum Spell {

    CHILL_TOUCH("Chill Touch", "chill_touch", 1, 8, null, Effect.HEAL_BLOCK),
    POISON_SPRAY("Poison Spray", "poison_spray", 1, 12, Stats.stat.CON, Effect.NONE),
    SHOCKING_GRASP("Shocking Grasp", "shocking_grasp", 1, 8, null, Effect.NONE),
    RAY_OF_SICKNESS("Ray of Sickness", "ray_of_sickness", 2, 8, null, Effect.POISON),
    THUNDER_WAVE("Thunder Wave", "thunder_wave", 2, 8, Stats.stat.CON, Effect.HALF_DAMAGE),
    HELLISH_REBUKE("Hellish Rebuke", "hellish_rebuke", 2, 10, Stats.stat.DEX, Effect.HALF_DAMAGE),
    PHANTASMAL_FORCE("Phantasmal Force", "phantasmal_force", 2, 8, Stats.stat.INT, Effect.ILLUSION),

    BURNING_HANDS("Burning Hands", "burning_hands", 4, 6, Stats.stat.DEX, Effect.HALF_DAMAGE),
    HEX("Hex", "hex", 1, 6, Stats.stat.WIS, Effect.BONUS_DAMAGE),
        // The effects of hex technically last for one hour. I decided not to code this because that amounts to 600 turns of
        // combat, and I figured this limit would never reasonably be reached. Be advised.
    SCORCHING_RAY("Scorching Ray", "scorching_ray", 2, 6, null, Effect.SPLIT_ATTACK),
    ELDRITCH_BLAST("Eldritch Blast", "eldritch_blast", 1, 10, null, Effect.NONE),

    MANUAL_HIT("Manual with Hit Roll", "null", -1, -1, null, null),
    MANUAL_SAVE("Manual with Save DC", "null", -1, -1, null, null);


    private final String name;
    private final String nameRoot;
    private final int numDamageDice;
    private final int dieSize;
    private final Stats.stat savingThrow;
    private final Effect effect;

    Spell(String name, String nameRoot, int numDamageDice, int dieSize, Stats.stat savingThrow, Effect effect) {
        this.name = name;
        this.nameRoot = nameRoot;
        this.numDamageDice = numDamageDice;
        this.dieSize = dieSize;
        this.savingThrow = savingThrow;
        this.effect = effect;
    }

    public String getName() {
        return name;
    }

    public String getNameRoot() {
        return nameRoot;
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
        if (effect == null) {
            return false;
        }
        return effect.equals(Effect.HALF_DAMAGE);
    }

    public static Spell get(String nameRaw) {
        for (Spell spell : values()) {
            if (nameRaw.equals(spell.nameRoot)) {
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