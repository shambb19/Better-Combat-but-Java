package character_info;

public enum Class5e {

    BARBARIAN(null),
    BARD(Stat.CHA),
    CLERIC(Stat.WIS),
    DRUID(Stat.WIS),
    FIGHTER(null),
    MONK(null),
    PALADIN(Stat.CHA),
    RANGER(Stat.WIS),
    ROGUE(null),
    SORCERER(Stat.CHA),
    WARLOCK(Stat.CHA),
    WIZARD(Stat.INT);

    private final Stat spellMod;

    Class5e(Stat spellMod) {
        this.spellMod = spellMod;
    }

    public Stat spellMod() {
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
