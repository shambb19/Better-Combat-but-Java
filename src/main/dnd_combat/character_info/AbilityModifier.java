package character_info;

public enum AbilityModifier {

    STR, DEX, CON, INT, WIS, CHA;

    AbilityModifier() {}

    public static AbilityModifier get(String name) {
        for (AbilityModifier stat : values()) {
            if (name.equalsIgnoreCase(stat.name())) {
                return stat;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }

}
