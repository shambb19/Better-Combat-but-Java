package campaign_creator_menu;

import combat_object.combatant.Combatant;
import combat_object.combatant.NPC;
import combat_object.combatant.PC;
import combat_object.scenario.Scenario;
import encounter.Encounter;
import lombok.*;
import lombok.experimental.*;
import swing.swing_comp.SwingPane;

import javax.swing.*;
import java.util.List;

import static swing.swing_comp.SwingPane.FLOW;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CompletedElementsList extends JPanel {

    CompletedElementList<Combatant> friendlyPane, enemyPane;
    CompletedElementList<Scenario> scenarioPane;

    public CompletedElementsList(Encounter input, CampaignCreatorMenu root) {
        friendlyPane = new CompletedElementList<>(input.getFriendlies(), CompletedElementList.FRIENDLY_NEW, this, root);
        enemyPane = new CompletedElementList<>(input.getEnemies(), CompletedElementList.ENEMY_NEW, this, root);
        scenarioPane = new CompletedElementList<>(input.getScenarios(), CompletedElementList.SCENARIO_NEW, this, root);

        SwingPane.fluent(this).arrangedAs(FLOW, 10, 0)
                .collect(friendlyPane, enemyPane, scenarioPane)
                .withLabeledBorder("Completed Elements");
    }

    /**
     * @param combatant the completed combatant that will be added to its pane
     */
    public void addCombatant(Combatant combatant) {
        if (combatant.isEnemy())
            enemyPane.add(combatant);
        else
            friendlyPane.add(combatant);
    }

    public List<NPC> getFriendlyNPCs() {
        return friendlyPane.toList().stream().filter(NPC.class::isInstance).map(NPC.class::cast).toList();
    }

    public List<NPC> getEnemies() {
        return enemyPane.toList().stream().filter(NPC.class::isInstance).map(NPC.class::cast).toList();
    }

    /**
     * @param scenario the completed scenario that will be added to its pane
     */
    public void addScenario(Scenario scenario) {
        scenarioPane.add(scenario);
    }

    public void findAndLocateCopy(Combatant copy) {
        friendlyPane.remove(copy);
        enemyPane.remove(copy);
    }

    public boolean isNotEnoughForScenario() {
        boolean noPartyFound = friendlyPane.toList().stream().noneMatch(PC.class::isInstance);

        return enemyPane.toList().isEmpty() || noPartyFound;
    }

}