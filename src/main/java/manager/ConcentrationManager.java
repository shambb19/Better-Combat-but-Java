package manager;

import combat_object.combatant.Combatant;
import combat_object.damage_implements.Effect;
import combat_object.damage_implements.Spell;
import lombok.experimental.*;
import util.Filterable;

import java.util.ArrayList;
import java.util.List;

@UtilityClass public class ConcentrationManager {

    private static final List<Concentration> CONCENTRATIONS = new ArrayList<>();

    public void startNewConcentration(Combatant by, Combatant on, Spell spell) {
        if (!spell.isRequiresConcentration()) return;

        Concentration concentration = new Concentration(by, on, spell);
        breakConcentration(by);
        CONCENTRATIONS.add(concentration);
    }

    public void breakConcentration(Combatant by) {
        Concentration concentration = Filterable.of(CONCENTRATIONS)
                .filteredBy(c -> c.by.equals(by)).firstOrNull();

        if (concentration == null) return;

        Effect endedEffect = concentration.spell.getEffect();
        EffectManager.removeEffectOn(concentration.on, endedEffect);
    }

    public boolean isCombatantConcentrating(Combatant query) {
        return CONCENTRATIONS.stream().anyMatch(c -> c.by.equals(query));
    }

    public List<Concentration> getConcentrationsAsList() {
        return CONCENTRATIONS;
    }

    public record Concentration(Combatant by, Combatant on, Spell spell) {
    }

}
