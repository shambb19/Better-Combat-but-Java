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

    @Override
    public String toString() {
        return name().toLowerCase();
    }

}