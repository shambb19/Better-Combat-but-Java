package manager;

import combat_object.combatant.Combatant;
import combat_object.damage_implements.Effect;
import combat_object.damage_implements.Implement;
import combat_object.damage_implements.Spell;
import lombok.experimental.*;
import util.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static combat_object.damage_implements.Effect.*;

@UtilityClass
public class EffectManager {

    private static final List<DealtEffect> EFFECTS = new ArrayList<>();

    public void logEffect(Combatant affected, Combatant by, Implement implement) {
        if (!(implement instanceof Spell spell)) return;

        if (spell.isRequiresConcentration())
            ConcentrationManager.startNewConcentration(by, affected, spell);

        if (!spell.effectEquals(NONE))
            EFFECTS.add(new DealtEffect(affected, by, spell.getEffect()));
    }

    public boolean hasEffect(Combatant query, Effect effect) {
        if (effect.equals(BONUS_DAMAGE)) {
            Logger.getAnonymousLogger().warning("EffectManager.hasEffect: use isHexedBy for Effect.BONUS_DAMAGE");
            return false;
        }

        return EFFECTS.stream().anyMatch(e -> e.on().equals(query) && e.effect().equals(effect));
    }

    public boolean isHexedBy(Combatant targetQuery, Combatant byQuery) {
        return EFFECTS.stream().filter(e -> e.effect.equals(BONUS_DAMAGE))
                .anyMatch(e -> e.on.equals(targetQuery) && e.by.equals(byQuery));
    }

    public void removeEffectOn(Combatant query, Effect effect) {
        EFFECTS.removeIf(e -> e.on.equals(query) && e.effect.equals(effect));
    }

    public void logTurnEnd(Combatant query) {
        final Effect[] effectsDealtByCombatant = {ADVANTAGE_SOON, PENALTY_SAVE};
        final Effect[] effectsOnCombatant = {POISON, DISADVANTAGE_ATTACK};
        final Effect[] effectsOnCombatantWithRoll =
                {BLIND, DAMAGE_OVER_TIME, FRIGHTEN, RESTRAIN, PENALTY_ATTACK, RANDOM_ACTION, STUNNED};

        for (Effect effect : effectsDealtByCombatant) {
            EFFECTS.removeIf(e -> e.by.equals(query) && e.effect.equals(effect));
        }
        for (Effect effect : effectsOnCombatant) {
            EFFECTS.removeIf(e -> e.on.equals(query) && e.effect.equals(effect));
        }
        for (Effect effect : effectsOnCombatantWithRoll) {
            if (!hasEffect(query, effect)) return;
            EFFECTS.removeIf(e -> {
                int result = Message.promptIntWithLoop(
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
        return EFFECTS;
    }

    public record DealtEffect(Combatant on, Combatant by, Effect effect) {
    }
}