package _manager;

import combat_object.combatant.Combatant;
import combat_object.implement.Spell;
import lombok.*;
import lombok.experimental.*;
import util.Locators;

import java.util.HashSet;
import java.util.Set;

@UtilityClass public class ConcentrationManager {

    @Getter private static final Set<Concentration> concentrations = new HashSet<>();

    /**
     * Logs that the given Combatant is concentrating on the given target
     * to use the given Spell. Because a Combatant can only concentrate on one
     * Spell at a time, this also ends any other Concentrations that are logged
     * with the same Combatant that is beginning the concentration
     * @param by the Combatant object doing the concentrating
     * @param on the Combatant object being concentrated on
     * @param spell the Spell used by the by Combatant
     */
    public void startNewConcentration(Combatant by, Combatant on, Spell spell) {
        if (!spell.isConcentrationRequired()) return;

        Concentration concentration = new Concentration(by, on, spell);
        breakConcentration(by);
        concentrations.add(concentration);
    }

    /**
     * Ends all logged concentrations for which the concentrating combatant
     * is the given Combatant object. Because a Combatant can only be
     * concentrating on one spell at a time, this method does not filter
     * the entire HashSet but instead removes the first instance of a
     * Concentration where the by Combatant equals the given Combatant
     * object.
     * @param by the Combatant object for which concentration is to be
     *           ended
     */
    public void breakConcentration(Combatant by) {
        Locators.firstMatching(concentrations, Concentration::by, by)
                .ifPresent(c -> EffectManager.removeEffect(c.on, c.spell.getEffect()));
    }

    /**
     * Returns whether there exists in the Concentrations HashSet a
     * Concentration object for which the by Combatant equals the
     * given Combatant object
     * @param query the Combatant object for which Concentrations are
     *              to be checked
     * @return true if a Concentration is found for which by equals
     * the given Combatant, else false
     */
    public boolean isCombatantConcentrating(Combatant query) {
        return concentrations.stream().anyMatch(c -> c.by.equals(query));
    }

    /**
     * A data holder representing a Concentration action, including the
     * Combatant doing the concentrating, the target of the concentration,
     * and the Spell used by the concentrating Combatant
     * @param by the Combatant doing the concentrating
     * @param on the Combatant being concentrated on
     * @param spell the Spell used by the concentrating Combatant
     */
    public record Concentration(Combatant by, Combatant on, Spell spell) {
    }

}
