package txt_input_2;

public enum Key {

    NAME, HP, AC, LEVEL, CLASS, STATS, WEAPONS, SPELLS,
    DMG, STAT,
    SAVE_TYPE, EFFECT;

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
