package txt_input;

public enum Key {

    NAME, HP, AC, LEVEL, CLASS, STATS, WEAPONS, SPELLS,
    WITH, AGAINST,
    DMG, STAT, EFFECT;

    private final String keyName;

    Key() {
        this.keyName = name().toLowerCase();
    }

    public static boolean lineStartsWithKey(String str) {
        for (Key key : values()) {
            if (str.startsWith(key.keyName)) {
                return true;
            }
        }
        return false;
    }

}
