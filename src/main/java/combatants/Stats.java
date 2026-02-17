package combatants;

import java.util.function.DoubleBinaryOperator;

public class Stats {

    public enum stat {STR, DEX, CON, INT, WIS, CHA}

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

    private final int proficiencyBonus;
    private final stat spellCastingAbilityModifier;

    public Stats(int proficiencyBonus, stat spellCastingAbilityModifier) {
        this.proficiencyBonus = proficiencyBonus;
        this.spellCastingAbilityModifier = spellCastingAbilityModifier;
    }

    public Stats(int STR, boolean strProf,
                 int DEX, boolean dexProf,
                 int CON, boolean conProf,
                 int INT, boolean intProf,
                 int WIS, boolean wisProf,
                 int CHA, boolean chaProf,
                 int proficiencyBonus,
                 stat spellCastingAbilityModifier) {
        this.strength = STR;
        this.dexterity = DEX;
        this.constitution = CON;
        this.intelligence = INT;
        this.wisdom = WIS;
        this.charisma = CHA;

        this.strProf = strProf;
        this.dexProf = dexProf;
        this.conProf = conProf;
        this.intProf = intProf;
        this.wisProf = wisProf;
        this.chaProf = chaProf;

        this.proficiencyBonus = proficiencyBonus;
        this.spellCastingAbilityModifier = spellCastingAbilityModifier;
    }

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

    public int prof() {
        return proficiencyBonus;
    }

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

    public int spellMod() {
        return mod(spellCastingAbilityModifier);
    }

}