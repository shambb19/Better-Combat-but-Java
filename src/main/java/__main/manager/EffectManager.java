package __main.manager;

import character_info.combatant.Combatant;
import damage_implements.Effect;

import java.util.ArrayList;
import java.util.List;

public class EffectManager {

    private static final ArrayList<DealtEffect> EFFECTS = new ArrayList<>();
    private static final List<Effect> ENDS_ON_TURN_START = List.of(Effect.POISON, Effect.HEAL_BLOCK);

    public static void logEffect(Combatant affected, Combatant by, Effect effect) {
        EFFECTS.add(new DealtEffect(affected, by, effect));
    }

    public static boolean hasEffect(Combatant query, Effect effect) {
        if (effect.equals(Effect.BONUS_DAMAGE))
            throw new ClassCastException("hasEffect in EffectManager: use isHexedBy for Effect.BONUS_DAMAGE");

        return EFFECTS.stream().anyMatch(e -> e.affected().equals(query) && e.effect().equals(effect));
    }

    public static boolean isHexedBy(Combatant targetQuery, Combatant byQuery) {
        return EFFECTS.stream().anyMatch(
                e -> e.affected().equals(targetQuery)
                        && e.by().equals(byQuery)
                        && e.effect().equals(Effect.BONUS_DAMAGE)
        );
    }

    public static void processCombatantTurnStart() {
        Combatant active = EncounterManager.getCurrentCombatant();
        EFFECTS.removeIf(e -> e.by().equals(active) && ENDS_ON_TURN_START.contains(e.effect()));
    }

}

record DealtEffect(Combatant affected, Combatant by, Effect effect) {
}
