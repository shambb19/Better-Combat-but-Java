package _manager;

import combat_object.combatant.Combatant;
import combat_object.implement.Effect;
import combat_object.implement.Implement;
import combat_object.implement.Spell;
import lombok.*;
import lombok.experimental.*;
import util.Filterable;
import util.Message;
import util.Roll;

import java.util.*;
import java.util.logging.Logger;

import static combat_object.implement.Effect.*;

@UtilityClass
public class EffectManager {

    @Getter private static final Set<DealtEffect> effects = new HashSet<>();

    /**
     * Creates a DealtEffect object based on the given parameters. Any implement
     * is accepted as a parameter but definitionally, only Spells will be logged.
     * If the given Spell requires concentration, a new concentration is logged
     * in the ConcentrationManager
     * @param affected the Combatant object on which the Effect is dealt
     * @param by the Combatant who cast the spell
     * @param implement the Spell cast by the by Combatant (NOTE: any implement is
     *                  allowed but the method will only run if instanceof Spell)
     */
    public void logEffect(Combatant affected, Combatant by, Implement implement) {
        if (!(implement instanceof Spell spell)) return;

        if (spell.isConcentrationRequired())
            ConcentrationManager.startNewConcentration(by, affected, spell);

        if (!spell.effectEquals(NONE))
            effects.add(new DealtEffect(affected, by, spell.getEffect()));
    }

    /**
     * Returns an Optional of the Combatant who, for the given Combatant and
     * Effect (assuming the Effect has been dealt to the Combatant),
     * originally cast the Spell in question
     * @param on the Combatant on which the Effect is dealt
     * @param effect the Effect that was dealt
     * @return the Optional of the Combatant attacker in question
     */
    public Optional<Combatant> getSource(Combatant on, Effect effect) {
        return Filterable.of(effects)
                .filteredBy(DealtEffect::on, on).filteredBy(DealtEffect::effect, effect)
                .mapped(DealtEffect::by).stream.findFirst();
    }

    /**
     * Returns whether the given Combatant has had the given Effect dealt
     * to them. Note that the BONUS_DAMAGE effect specifically cannot be
     * checked in this method because it requires an additional Combatant
     * parameter
     * @param on the Combatant that might have had the Effect parameter
     *              dealt to it
     * @param effect the Effect that might have been dealt to the Combatant
     *               parameter
     * @return true if the Effect exists on the Combatant, else false
     */
    public boolean hasEffect(Combatant on, Effect effect) {
        if (effect.equals(BONUS_DAMAGE)) {
            Logger.getAnonymousLogger().warning("EffectManager.hasEffect: use isHexedBy for Effect.BONUS_DAMAGE");
            return false;
        }

        return effects.stream().anyMatch(e -> e.on().equals(on) && e.effect().equals(effect));
    }

    /**
     * Returns whether the given by Combatant can deal bonus damage to
     * the given target Combatant. Note that this method MUST be used for
     * BONUS_DAMAGE checks because both Combatants must be checked;
     * EffectManager.hasEffect cannot be used for this check
     * @param on the Combatant who might be receiving bonus damage
     * @param by the Combatant who might be dealing bonus damage
     * @return true if the by Combatant can deal bonus damage to the target,
     * else false
     */
    public boolean isHexedBy(Combatant on, Combatant by) {
        return effects.stream().filter(e -> e.effect.equals(BONUS_DAMAGE))
                .anyMatch(e -> e.on.equals(on) && e.by.equals(by));
    }

    /**
     * Removes the given Effect from the given target Combatant. This is
     * done by removing all DealtEffect objects for which on and effect
     * equal their respective parameters
     * @param on the Combatant for whom the Effect no longer applies
     * @param effect the Effect that is no longer affecting the on Combatant
     */
    public void removeEffect(Combatant on, Effect effect) {
        effects.removeIf(e -> e.on.equals(on) && e.effect.equals(effect));
    }

    /**
     * On the end of the given Combatant's turn, ends any effects that have
     * the potential to expire at the end of the Combatant's turn. This includes:
     * effects dealt by this Combatant to other Combatants; effects dealt to this
     * Combatant; effects dealt to this Combatant that require a save roll to be
     * ended
     * @param query the Combatant whose turn is ending
     */
    public void logTurnEnd(Combatant query) {
        final Effect[] effectsDealtByCombatant = {ADVANTAGE, PENALTY_SAVE};
        final Effect[] effectsOnCombatant = {POISON, ATTACK_DISADVANTAGE};
        final Effect[] effectsOnCombatantWithRoll =
                {BLIND, DAMAGE_OVER_TIME, FRIGHTENED, RESTRAINED, PENALTY_ATTACK, RANDOM_ACTION, STUNNED};

        for (Effect effect : effectsDealtByCombatant) {
            effects.removeIf(e -> e.by.equals(query) && e.effect.equals(effect));
        }
        for (Effect effect : effectsOnCombatant) {
            effects.removeIf(e -> e.on.equals(query) && e.effect.equals(effect));
        }
        for (Effect effect : effectsOnCombatantWithRoll) {
            if (!hasEffect(query, effect)) return;
            effects.removeIf(e -> Message.promptRoll(
                    "to remove the effect " + effect.name() + " from " + query,
                    Roll.d20(), 10,
                    () -> {}, "The effect was not removed"
            ) >= 10);
        }
    }

    /**
     * At the start of the given Combatant's turn, ends any effects dealt
     * to the Combatant that expire at this time
     * @param query the Combatant whose turn is beginning
     */
    public void logTurnStart(Combatant query) {
        Effect[] effectsEnding = {HEAL_BLOCK};

        for (Effect effect : effectsEnding) {
            effects.removeIf(e -> e.by.equals(query) && e.effect.equals(effect));
        }
    }

    /**
     * A data holder representing a dealt Effect, including the Combatant
     * on which the Effect is dealt, the Combatant who cast the spell dealing
     * the Effect, and the Effect in question
     * @param on the Combatant on whom the Effect is dealt
     * @param by the Combatant who dealt the Effect
     * @param effect the Effect being dealt
     */
    public record DealtEffect(Combatant on, Combatant by, Effect effect) {
    }
}