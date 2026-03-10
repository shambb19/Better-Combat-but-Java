package character_info;

public enum Stat {

    STR, DEX, CON, INT, WIS, CHA;

    Stat() {}

    public static Stat get(String name) {
        for (Stat stat : values()) {
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
