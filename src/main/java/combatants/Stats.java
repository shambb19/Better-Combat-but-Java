package combatants;

import java.util.function.DoubleBinaryOperator;

public class Stats {

    public enum stat {STR, DEX, CON, INT, WIS, CHA}

    private final int strength;
    private final int dexterity;
    private final int constitution;
    private final int intelligence;
    private final int wisdom;
    private final int charisma;

    private final boolean strProf;
    private final boolean dexProf;
    private final boolean conProf;
    private final boolean intProf;
    private final boolean wisProf;
    private final boolean chaProf;

    private final int proficiencyBonus;
    private final stat spellCastingAbilityModifier;

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