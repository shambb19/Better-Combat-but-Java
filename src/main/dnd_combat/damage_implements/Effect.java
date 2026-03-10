package damage_implements;

public enum Effect {

    HEAL_BLOCK(null),
    HALF_DAMAGE(null),
    POISON(null),
    ILLUSION(null),
    BONUS_DAMAGE(null),
    //TODO add code for SPLIT_ATTACK (probably in gui.popup.damage.SpellPanel)
    // should prompt for a number of targets on [1, numRays]
    SPLIT_ATTACK(null),
    NONE(null);

    private final String description;

    Effect(final String description) {
        this.description = description;
    }

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