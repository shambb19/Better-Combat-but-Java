package character_info;

import character_info.combatant.Combatant;
import damage_implements.Effect;

import java.util.ArrayList;

public class DealtEffectsList {

    private final Combatant parentCombatant;

    private final ArrayList<Combatant> poisonedCombatants = new ArrayList<>();
    private final ArrayList<Combatant> healBlockedCombatants = new ArrayList<>();

    /**
     * Creates a list of combatants poisoned and heal blocked by the root
     * combatant. Other effects added later will also be logged here.
     *
     * @param parentCombatant the root combatant
     */
    public DealtEffectsList(Combatant parentCombatant) {
        this.parentCombatant = parentCombatant;
    }

    /**
     * Logs the effect param as dealt to the target param.
     */
    public void put(Combatant target, Effect effect) {
        if (effect == null || effect.equals(Effect.NONE)) return;

        switch (effect) {
            case Effect.POISON -> {
                poisonedCombatants.add(target);
                target.setPoisoned(true);
            }
            case Effect.HEAL_BLOCK -> {
                healBlockedCombatants.add(target);
                target.setCanHeal(false);
            }
            case Effect.BONUS_DAMAGE -> target.setHexedBy(parentCombatant);
        }
    }

    /**
     * Ends all effects dealt by the root combatant (inner method)
     */
    public void clear() {
        poisonedCombatants.forEach(combatant -> combatant.setPoisoned(false));
        healBlockedCombatants.forEach(combatant -> combatant.setCanHeal(true));

        poisonedCombatants.clear();
        healBlockedCombatants.clear();
    }

}
