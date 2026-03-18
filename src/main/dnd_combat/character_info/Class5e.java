package character_info;

import org.apache.commons.lang3.text.WordUtils;

public enum Class5e {

    BARBARIAN(null, 7),
    BARD(AbilityModifier.CHA, 5),
    CLERIC(AbilityModifier.WIS, 5),
    DRUID(AbilityModifier.WIS, 5),
    FIGHTER(null, 6),
    MONK(null, 5),
    PALADIN(AbilityModifier.CHA, 6),
    RANGER(AbilityModifier.WIS, 6),
    ROGUE(null, 5),
    SORCERER(AbilityModifier.CHA, 4),
    WARLOCK(AbilityModifier.CHA, 5),
    WIZARD(AbilityModifier.INT, 4);

    private final AbilityModifier spellMod;
    private final int hpIncrement;

    Class5e(AbilityModifier spellMod, int hpIncrement) {
        this.spellMod = spellMod;
        this.hpIncrement = hpIncrement;
    }

    public AbilityModifier spellMod() {
        return spellMod;
    }

    public int hpIncrement() {
        return hpIncrement;
    }

    @Override
    @SuppressWarnings("deprecation")
    public String toString() {
        return WordUtils.capitalizeFully(name());
    }

}
