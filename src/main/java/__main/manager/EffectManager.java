package __main.manager;

import combat_object.combatant.Combatant;
import combat_object.damage_implements.Effect;
import combat_object.damage_implements.Implement;
import combat_object.damage_implements.Spell;
import lombok.experimental.*;
import util.Message;

import java.util.ArrayList;
import java.util.List;

import static combat_object.damage_implements.Effect.*;

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
        return EFFECTS.stream().filter(e -> e.effect.equals(BONUS_DAMAGE))
                .anyMatch(e -> e.affected.equals(targetQuery) && e.by.equals(byQuery));
    }

    public void removeEffectOn(Combatant query, Effect effect) {
        EFFECTS.removeIf(e -> e.affected.equals(query) && e.effect.equals(effect));
    }

    public void logTurnEnd(Combatant query) {
        final List<Effect> effectsDealtByCombatant = List.of(ADVANTAGE_SOON, PENALTY_SAVE);
        final List<Effect> effectsOnCombatant = List.of(POISON, DISADVANTAGE_ATTACK);
        final List<Effect> effectsOnCombatantWithRoll = List.of(
                BLIND, DAMAGE_OVER_TIME, FRIGHTEN, RESTRAIN, PENALTY_ATTACK, RANDOM_ACTION, STUNNED
        );

        for (Effect effect : effectsDealtByCombatant) {
            EFFECTS.removeIf(e -> e.by.equals(query) && e.effect.equals(effect));
        }
        for (Effect effect : effectsOnCombatant) {
            EFFECTS.removeIf(e -> e.affected.equals(query) && e.effect.equals(effect));
        }
        for (Effect effect : effectsOnCombatantWithRoll) {
            if (!hasEffect(query, effect)) return;
            EFFECTS.removeIf(e -> {
                int result = Message.getWithLoopUntilInt(
                        "Roll and enter a saving throw to remove the effect " + effect.name() + " from " + query,
                        effect.name() + " Save Throw");
                return result >= 10;
            });
        }
    }

    public void logTurnStart(Combatant query) {
        final List<Effect> effectsEnding = List.of(HEAL_BLOCK);

        for (Effect effect : effectsEnding) {
            EFFECTS.removeIf(e -> e.by.equals(query) && e.effect.equals(effect));
        }
    }

    public List<DealtEffect> getEffectsAsList() {
        return EFFECTS.stream().toList();
    }

    public record DealtEffect(Combatant affected, Combatant by, Effect effect) {
    }
}