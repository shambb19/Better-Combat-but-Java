package config.queue;

import _manager.EffectManager;
import _manager.MisfireManager;
import combat_object.combatant.Combatant;
import lombok.experimental.*;
import popup.MisfireManagerPopup;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@ExtensionMethod(EffectManager.class)
public class SteampunkQueue extends PlayerQueue {

    public SteampunkQueue(List<Combatant> friendlies, List<Combatant> enemies) {
        super(friendlies, enemies);
    }

    @Override public void endCurrentTurn() {
        List<Combatant> allCombatants = Stream.concat(friendlies.stream(), enemies.stream())
                .filter(c -> !c.equals(currentCombatant))
                .sorted(Comparator.comparing(Combatant::getInitiative).reversed()).toList();

        List<Combatant> remainingQueue = allCombatants.stream()
                .filter(c -> c.getInitiative() <= currentCombatant.getInitiative()).toList();

        if (remainingQueue.isEmpty()) currentCombatant = allCombatants.getFirst();
        else currentCombatant = remainingQueue.getFirst();

        super.endCurrentTurn();
    }

    @Override protected void processTurnStart() {
        if (currentCombatant == null) return;
        currentCombatant.logTurnStart();

        promptMisfires();
        super.processTurnStart();
    }

    private void promptMisfires() {
        List<MisfireManager.Misfire> misfires = MisfireManager.getCurrentCombatantMisfires();

        if (misfires.isEmpty()) return;

        new MisfireManagerPopup(misfires);
        endCurrentTurn();
    }
}
