package character_info;

import util.TxtReader;

import java.util.function.DoubleBinaryOperator;

public class Stats {

    public enum stat {STR, DEX, CON, INT, WIS, CHA}

    private int level;

    private int strength;
    private int dexterity;
    private int constitution;
    private int intelligence;
    private int wisdom;
    private int charisma;

    private boolean strProf;
    private boolean dexProf;
    private boolean conProf;
    private boolean intProf;
    private boolean wisProf;
    private boolean chaProf;

    private final Class5e characterClass;
    private int proficiencyBonus;

    /**
     * Stores str, dex, con, int, wis, cha stats, skill proficiency,
     * proficiency bonuses, spell casting ability modifiers, and calculates
     * them on demand.
     */
    public Stats(Class5e characterClass, int level) {
        this.characterClass = characterClass;
        this.level = level;

        setProf();
    }

    public void manualAdjust(String code) {
        String[] codes = code.split("\\.");

        if (codes[0].equals("level")) {
            level = Integer.parseInt(codes[1]);
            setProf();
            return;
        }

        stat stat = TxtReader.mod(codes[0]);
        String val = codes[1];

        int statVal;
        boolean statProf;

        assert stat != null;
        if (val.startsWith("prof=")) {
            statVal = get(stat);
            statProf = val.endsWith("true");
        } else {
            statVal = Integer.parseInt(val);
            statProf = isProf(stat);
        }
        put(stat, statVal, statProf);
    }

    /**
     * Sets the stat param to the value param.
     * @param prof if true, logs proficiency in the stat param.
     */
    public void put(stat stat, int value, boolean prof) {
        switch (stat) {
            case STR -> {
                strength = value;
                strProf = prof;
            }
            case DEX -> {
                dexterity = value;
                dexProf = prof;
            }
            case CON ->  {
                constitution = value;
                conProf = prof;
            }
            case INT -> {
                intelligence = value;
                intProf = prof;
            }
            case WIS -> {
                wisdom = value;
                wisProf = prof;
            }
            case CHA -> {
                charisma = value;
                chaProf = prof;
            }
        }
    }

    /**
     * @return the raw stat for the given param
     */
    public int get(stat stat) {
        return switch (stat) {
            case STR -> strength;
            case DEX -> dexterity;
            case CON -> constitution;
            case INT -> intelligence;
            case WIS -> wisdom;
            case CHA -> charisma;
        };
    }

    /**
     * @return true if the combatant has proficiency in the
     * stat param
     */
    public boolean isProf(stat stat) {
        return switch (stat) {
            case STR -> strProf;
            case DEX -> dexProf;
            case CON -> conProf;
            case INT -> intProf;
            case WIS -> wisProf;
            case CHA -> chaProf;
        };
    }

    /**
     * @return the combatant's proficiency bonus
     */
    public int prof() {
        return proficiencyBonus;
    }

    /**
     * @return the modifier for the stat param using the (stat - 10)/2 rounded down
     * calculations, and adding proficiency bonus if present.
     */
    @SuppressWarnings("all")
    public int mod(stat stat) {
        DoubleBinaryOperator modCalculator = (x, y) -> ((x - 10) / 2) + y;
        int y = switch (stat) {
            case STR -> (strProf) ? proficiencyBonus : 0;
            case DEX -> (dexProf) ? proficiencyBonus : 0;
            case CON -> (conProf) ? proficiencyBonus : 0;
            case INT -> (intProf) ? proficiencyBonus : 0;
            case WIS -> (wisProf) ? proficiencyBonus : 0;
            case CHA -> (chaProf) ? proficiencyBonus : 0;
        };
        return (int) switch (stat) {
            case STR -> modCalculator.applyAsDouble(strength, y);
            case DEX -> modCalculator.applyAsDouble(dexterity, y);
            case CON -> modCalculator.applyAsDouble(constitution, y);
            case INT -> modCalculator.applyAsDouble(intelligence, y);
            case WIS -> modCalculator.applyAsDouble(wisdom, y);
            case CHA -> modCalculator.applyAsDouble(charisma, y);
        };
    }

    public Class5e class5e() {
        return characterClass;
    }

    /**
     * @return the stat field for which the combatant has a spell casting
     * ability modifier
     */
    public stat spellMod() {
        return characterClass.spellMod();
    }

    /**
     * @return The value of the attacker's attack bonus for spells.
     */
    public int spellAttackBonus() {
        return mod(spellMod()) + proficiencyBonus;
    }

    // TODO finish implementing
    public void levelUp() {
        level++;
        setProf();
    }

    /**
     * @return the line of text to log stats in the .txt file for this combatant's party
     */
    @Override
    public String toString() {
        StringBuilder string = new StringBuilder("stats=");
        for (stat stat : stat.values()) {
            string.append(statString(stat));
        }
        return string.deleteCharAt(string.length() - 1).toString();
    }

    /**
     * @return The string for this specific stat using stat(val<?>+<?>)
     */
    private String statString(stat stat) {
        StringBuilder string = new StringBuilder(stat.name().toLowerCase());
        string.append("(").append(get(stat));
        if (isProf(stat)) {
            string.append("+");
        }
        return string.append(")/").toString();
    }

    private void setProf() {
        if (level < 5) {
            proficiencyBonus = 2;
        } else if (level < 9) {
            proficiencyBonus = 3;
        } else if (level < 13) {
            proficiencyBonus = 4;
        } else if (level < 17) {
            proficiencyBonus = 5;
        } else {
            proficiencyBonus = 6;
        }
    }

}