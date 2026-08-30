package _manager;

import combat_object.combatant.Combatant;
import combat_object.implement.Effect;
import combat_object.implement.Spell;
import lombok.*;
import lombok.experimental.*;
import util.Filterable;

import java.util.ArrayList;
import java.util.List;

@UtilityClass public class ConcentrationManager {

    @Getter private static final List<Concentration> concentrations = new ArrayList<>();

    public void startNewConcentration(Combatant by, Combatant on, Spell spell) {
        if (!spell.isRequiresConcentration()) return;

        Concentration concentration = new Concentration(by, on, spell);
        breakConcentration(by);
        concentrations.add(concentration);
    }

    public void breakConcentration(Combatant by) {
        Concentration concentration = Filterable.of(concentrations)
                .filteredBy(c -> c.by.equals(by)).firstOrNull();

        if (concentration == null) return;

        Effect endedEffect = concentration.spell.getEffect();
        EffectManager.removeEffect(concentration.on, endedEffect);
    }

    public boolean isCombatantConcentrating(Combatant query) {
        return concentrations.stream().anyMatch(c -> c.by.equals(query));
    }

    public record Concentration(Combatant by, Combatant on, Spell spell) {
    }

}
