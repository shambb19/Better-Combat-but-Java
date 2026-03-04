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
    //TODO add code for FULL_HP_OPTION (probably in gui.popup.damage.DamageAmountPopup)
    // should deal increased damage if target is on full health.
    FULL_HP_OPTION,
    NONE;

    private int numRays;

    private int numDice;
    private int dieValue;

    Effect() {
        this.numRays = 0;
        this.numDice = 0;
        this.dieValue = 0;
    }

    public Effect withRays(int totalAttacks) {
        this.numRays = totalAttacks;
        return this;
    }

    public Effect withMaxDmg(int numDice, int dieValue) {
        this.numDice = numDice;
        this.dieValue = dieValue;
        return this;
    }

}