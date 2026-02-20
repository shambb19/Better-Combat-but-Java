package damage;

public enum Effect {

    HEAL_BLOCK,
    HALF_DAMAGE,
    POISON,
    ILLUSION,
    BONUS_DAMAGE,
    SPLIT_ATTACK(3),
    NONE; // multiple rays that can be targeted towards different or same target

    private final int numRays;

    Effect(int numRays) {
        this.numRays = numRays;
    }

    Effect() {
        this.numRays = 0;
    }

}