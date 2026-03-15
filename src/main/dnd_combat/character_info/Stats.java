package character_info;

import damage_implements.Weapon;
import util.Locators;
import util.TxtReader;

import java.util.function.DoubleUnaryOperator;

import static util.TxtReader.listTextAsArray;

public class Stats {

    private int level;

    private int strength;
    private int dexterity;
    private int constitution;
    private int intelligence;
    private int wisdom;
    private int charisma;

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

    /**
     * Sets the stat param to the value param.
     */
    public void put(AbilityModifier stat, int value) {
        switch (stat) {
            case STR -> strength = value;
            case DEX -> dexterity = value;
            case CON -> constitution = value;
            case INT -> intelligence = value;
            case WIS -> wisdom = value;
            case CHA -> charisma = value;
        }
    }

    public void put(String line) {
        String[] stats = listTextAsArray(line);

        for (String s : stats) {
            String key = TxtReader.key(s);
            String value = TxtReader.value(s);

            AbilityModifier stat = Locators.enumNameSearch(key, AbilityModifier.class);
            int valInt = Integer.parseInt(value);

            assert stat != null;
            put(stat, valInt);
        }
    }

    /**
     * @return the raw stat for the given param
     */
    public int get(AbilityModifier stat) {
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
    public int mod(AbilityModifier stat) {
        DoubleUnaryOperator modCalculator = x -> (x - 10) / 2;
        return (int) switch (stat) {
            case STR -> modCalculator.applyAsDouble(strength);
            case DEX -> modCalculator.applyAsDouble(dexterity);
            case CON -> modCalculator.applyAsDouble(constitution);
            case INT -> modCalculator.applyAsDouble(intelligence);
            case WIS -> modCalculator.applyAsDouble(wisdom);
            case CHA -> modCalculator.applyAsDouble(charisma);
        };
    }

    public Class5e class5e() {
        return characterClass;
    }

    /**
     * @return the stat field for which the combatant has a spell casting
     * ability modifier
     */
    public AbilityModifier spellMod() {
        return characterClass.spellMod();
    }

    /**
     * @return The value of the attacker's attack bonus for spells.
     */
    public int spellAttackBonus() {
        return mod(spellMod()) + proficiencyBonus;
    }

    public int saveDc() {
        return 8 + mod(spellMod()) + proficiencyBonus;
    }

    public int weaponAttackBonus(Weapon weapon) {
        int str = mod(AbilityModifier.STR);
        int dex = mod(AbilityModifier.DEX);
        return switch (weapon.stat()) {
            case STR -> str;
            case DEX -> dex;
            default -> Math.max(str, dex);
        };
    }

    public int level() {
        return level;
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
        StringBuilder string = new StringBuilder("stats: [");
        for (AbilityModifier stat : AbilityModifier.values()) {
            string.append(statString(stat));
        }
        return string.delete(string.length() - 2, string.length()).append("]").toString();
    }

    /**
     * @return The string for this specific stat using "stat" + "val" (i.e. str16)
     */
    private String statString(AbilityModifier stat) {
        return stat.name().toUpperCase() + ": " + get(stat) + ", ";
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