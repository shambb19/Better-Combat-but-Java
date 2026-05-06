package manager;

import combat_object.combatant.Combatant;
import combat_object.implement.Effect;
import combat_object.implement.Implement;
import combat_object.implement.Spell;
import lombok.*;
import lombok.experimental.*;
import util.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static combat_object.implement.Effect.*;

@UtilityClass
public class EffectManager {

    @Getter private static final List<DealtEffect> effects = new ArrayList<>();

    public void logEffect(Combatant affected, Combatant by, Implement implement) {
        if (!(implement instanceof Spell spell)) return;

        if (spell.isRequiresConcentration())
            ConcentrationManager.startNewConcentration(by, affected, spell);

        if (!spell.effectEquals(NONE))
            effects.add(new DealtEffect(affected, by, spell.getEffect()));
    }

    public boolean hasEffect(Combatant query, Effect effect) {
        if (effect.equals(BONUS_DAMAGE)) {
            Logger.getAnonymousLogger().warning("EffectManager.hasEffect: use isHexedBy for Effect.BONUS_DAMAGE");
            return false;
        }

        return effects.stream().anyMatch(e -> e.on().equals(query) && e.effect().equals(effect));
    }

    public boolean isHexedBy(Combatant targetQuery, Combatant byQuery) {
        return effects.stream().filter(e -> e.effect.equals(BONUS_DAMAGE))
                .anyMatch(e -> e.on.equals(targetQuery) && e.by.equals(byQuery));
    }

    public void removeEffectOn(Combatant query, Effect effect) {
        effects.removeIf(e -> e.on.equals(query) && e.effect.equals(effect));
    }

    public void logTurnEnd(Combatant query) {
        final Effect[] effectsDealtByCombatant = {ADVANTAGE_SOON, PENALTY_SAVE};
        final Effect[] effectsOnCombatant = {POISON, DISADVANTAGE_ATTACK};
        final Effect[] effectsOnCombatantWithRoll =
                {BLIND, DAMAGE_OVER_TIME, FRIGHTEN, RESTRAIN, PENALTY_ATTACK, RANDOM_ACTION, STUNNED};

        for (Effect effect : effectsDealtByCombatant) {
            effects.removeIf(e -> e.by.equals(query) && e.effect.equals(effect));
        }
        for (Effect effect : effectsOnCombatant) {
            effects.removeIf(e -> e.on.equals(query) && e.effect.equals(effect));
        }
        for (Effect effect : effectsOnCombatantWithRoll) {
            if (!hasEffect(query, effect)) return;
            effects.removeIf(e -> {
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
            effects.removeIf(e -> e.by.equals(query) && e.effect.equals(effect));
        }
    }

    public record DealtEffect(Combatant on, Combatant by, Effect effect) {
    }
}