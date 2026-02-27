package damage;

public enum Effect {

    HEAL_BLOCK,
    HALF_DAMAGE,
    POISON,
    ILLUSION,
    BONUS_DAMAGE,
    //TODO add code for SPLIT_ATTACK (probably in gui.popup.damage.SpellPanel)
    SPLIT_ATTACK(3),
    NONE;

    private final int numRays;

    Effect(int numRays) {
        this.numRays = numRays;
    }

    Effect() {
        this.numRays = 0;
    }

}