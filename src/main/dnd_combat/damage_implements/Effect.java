package damage_implements;

public enum Effect {

    HEAL_BLOCK,
    HALF_DAMAGE,
    POISON,
    ILLUSION,
    BONUS_DAMAGE,
    //TODO add code for SPLIT_ATTACK (probably in gui.popup.damage.SpellPanel)
    // should prompt for a number of targets on [1, numRays]
    SPLIT_ATTACK,
    ADVANTAGE_SOON,
    NONE;

    Effect() {}

    public static Effect withRawName(String rawName) {
        for (Effect effect : values()) {
            if (effect.name().equals(rawName)) {
                return effect;
            }
        }
        return null;
    }

    public static String getRawNamesString() {
        StringBuilder str = new StringBuilder("Effects (enter exact): ");

        for (Effect effect : values()) {
            str.append(effect.name()).append(", ");
        }

        return str.toString();
    }

}