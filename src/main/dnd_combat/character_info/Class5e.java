package character_info;

public enum Class5e {

    BARBARIAN(null),
    BARD(Stats.stat.CHA),
    CLERIC(Stats.stat.WIS),
    DRUID(Stats.stat.WIS),
    FIGHTER(null),
    MONK(null),
    PALADIN(Stats.stat.CHA),
    RANGER(Stats.stat.WIS),
    ROGUE(null),
    SORCERER(Stats.stat.CHA),
    WARLOCK(Stats.stat.CHA),
    WIZARD(Stats.stat.INT);

    private final Stats.stat spellMod;

    Class5e(Stats.stat spellMod) {
        this.spellMod = spellMod;
    }

    public Stats.stat spellMod() {
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
