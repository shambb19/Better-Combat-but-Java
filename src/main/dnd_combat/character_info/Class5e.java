package character_info;

public enum Class5e {

    BARBARIAN(null),
    BARD(AbilityModifier.CHA),
    CLERIC(AbilityModifier.WIS),
    DRUID(AbilityModifier.WIS),
    FIGHTER(null),
    MONK(null),
    PALADIN(AbilityModifier.CHA),
    RANGER(AbilityModifier.WIS),
    ROGUE(null),
    SORCERER(AbilityModifier.CHA),
    WARLOCK(AbilityModifier.CHA),
    WIZARD(AbilityModifier.INT);

    private final AbilityModifier spellMod;

    Class5e(AbilityModifier spellMod) {
        this.spellMod = spellMod;
    }

    public AbilityModifier spellMod() {
        return spellMod;
    }

    public static Class5e withName(String name) {
        for (Class5e class5e : values()) {
            if (name.equalsIgnoreCase(class5e.name())) {
                return class5e;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        String first = name().substring(0, 1);
        String rest = name().substring(1);

        return first + rest.toLowerCase();
    }

}
