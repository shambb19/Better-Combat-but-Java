package __main.manager;

import combat_object.combatant.Combatant;
import combat_object.damage_implements.Effect;
import combat_object.damage_implements.Implement;
import combat_object.damage_implements.Spell;
import lombok.experimental.*;

import java.util.ArrayList;

@UtilityClass
public class EffectManager {

    private static final ArrayList<DealtEffect> EFFECTS = new ArrayList<>();

    public void logEffect(Combatant affected, Combatant by, Implement implement) {
        if (!(implement instanceof Spell spell) || spell.effectEquals(Effect.NONE)) return;
        EFFECTS.add(new DealtEffect(affected, by, spell.getEffect()));
    }

    public boolean hasEffect(Combatant query, Effect effect) {
        if (effect.equals(Effect.BONUS_DAMAGE))
            throw new ClassCastException("hasEffect in EffectManager: use isHexedBy for Effect.BONUS_DAMAGE");

        return EFFECTS.stream().anyMatch(e -> e.affected().equals(query) && e.effect().equals(effect));
    }

    public boolean isHexedBy(Combatant targetQuery, Combatant byQuery) {
        return EFFECTS.stream().anyMatch(
                e -> e.affected().equals(targetQuery)
                        && e.by().equals(byQuery)
                        && e.effect().equals(Effect.BONUS_DAMAGE)
        );
    }

    public void processCombatantTurnStart() {
        Combatant active = EncounterManager.getCurrentCombatant();
        EFFECTS.removeIf(e -> {
            boolean isByActiveCombatant = e.by.equals(active);
            boolean endsOnTurnStart = e.effect.equals(Effect.POISON) || e.effect.equals(Effect.HEAL_BLOCK);
            return isByActiveCombatant && endsOnTurnStart;
        });
    }

    public void endPenaltiesOn(Combatant target) {
        EFFECTS.removeIf(e -> {
            boolean isOnTarget = e.affected.equals(target);
            boolean isPenalty = e.effect.equals(Effect.PENALTY_SAVE);
            return isOnTarget && isPenalty;
        });
    }

    record DealtEffect(Combatant affected, Combatant by, Effect effect) {
    }
}