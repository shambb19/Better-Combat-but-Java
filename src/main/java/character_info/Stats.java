package character_info;

import util.Locators;
import util.TxtReader;

import java.util.function.DoubleUnaryOperator;

import static util.TxtReader.listTextAsArray;

public class Stats {

    private final Class5e characterClass;
    private int level;
    private int strength, dexterity, constitution,
            intelligence, wisdom, charisma;
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
            case OPTION -> Math.max(strength, dexterity);
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
    public int mod(AbilityModifier stat) {
        DoubleUnaryOperator modCalculator = x -> (x - 10) / 2;

        if (stat.equals(AbilityModifier.OPTION))
            return (int) Math.max(
                    modCalculator.applyAsDouble(strength),
                    modCalculator.applyAsDouble(dexterity)
            );

        return (int) modCalculator.applyAsDouble(get(stat));
    }

    public Class5e class5e() {
        return characterClass;
    }

    /**
     * @return The value of the attacker's attack bonus for spells.
     */
    public int spellAttackBonus() {
        return mod(characterClass.spellMod()) + proficiencyBonus;
    }

    public int saveDc() {
        return 8 + mod(characterClass.spellMod()) + proficiencyBonus;
    }

    public int level() {
        return level;
    }

    public int levelUp() {
        level++;
        setProf();
        return class5e().hpIncrement();
    }

    /**
     * @return the line of text to log stats in the .txt file for this combatant's party
     */
    @Override
    public String toString() {
        StringBuilder string = new StringBuilder("stats: [");
        for (AbilityModifier stat : AbilityModifier.values())
            string.append(statString(stat));

        return string.delete(string.length() - 2, string.length()).append("]").toString();
    }

    /**
     * @return The string for this specific stat using "STAT: val" (i.e. STR: 16)
     */
    private String statString(AbilityModifier stat) {
        return stat.name().toUpperCase() + ": " + get(stat) + ", ";
    }

    private void setProf() {
        proficiencyBonus = (level - 1) / 4 + 2;
    }

}