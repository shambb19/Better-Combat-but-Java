package campaign_creator_menu.input;

import campaign_creator_menu.CampaignCreatorMenu;
import campaign_creator_menu.CompletedElementsList;
import combat_object.combatant.NPC;
import combat_object.scenario.Scenario;
import format.ColorStyles;
import lombok.*;
import lombok.experimental.*;
import swing.custom_component.ValidatedField;
import swing.swing_comp.SwingComp;
import swing.swing_comp.SwingPane;
import util.Message;

import javax.swing.*;
import java.awt.*;
import java.util.logging.Logger;

import static swing.swing_comp.SwingPane.*;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ScenarioInputPanel extends JPanel {

    static String LABEL_TEXT = "Select friendly NPCs and enemies to include in the scenario from " +
            "the panels on the left. All party members (any non-npc friendly combatant) will automatically be " +
            "included in any scenario. You will be prompted to remove any absent party members when you open " +
            "the actual combat encounter. If you have a generic NPC that you would like to add several of " +
            "(say, a bunch of Orcs), click on their name and then select \"Edit\" when prompted. To remove a " +
            "combatant from an encounter, select \"Remove\" in the same prompt.";
    CampaignCreatorMenu root;
    CompletedElementsList sibling;
    ValidatedField nameField;
    ListSelectionPanel<NPC> friendlyPanel;
    ListSelectionPanel<NPC> enemyPanel;

    public ScenarioInputPanel(CompletedElementsList sibling, CampaignCreatorMenu root) {
        this.root = root;
        this.sibling = sibling;

        friendlyPanel = new ListSelectionPanel<>(sibling.getFriendlyNPCs(), "Friendly NPCs");
        enemyPanel = new ListSelectionPanel<>(sibling.getEnemies(), "Enemies");

        JTextArea label = SwingComp.textArea(LABEL_TEXT)
                .withMaximumSize(Integer.MAX_VALUE, 60)
                .component();

        nameField = new ValidatedField("Scenario Name");
        nameField.setValidator(s -> !s.isBlank());
        nameField.setPreferredSize(new Dimension(150, nameField.getPreferredSize().height));

        JPanel namePanel = SwingPane.newArrangedAs(SwingPane.FLOW).collect("Scenario Name", nameField).component();

        JPanel listsContainer = SwingPane.newArrangedAs(SwingPane.ONE_COLUMN, 0, 20)
                .collect(friendlyPanel, enemyPanel, Box.createVerticalGlue())
                .component();

        JPanel infoPanel = SwingPane.newArrangedAs(SwingPane.BORDER).borderCollect(
                north(namePanel), center(listsContainer)).component();

        JPanel okCancelPanel = SwingComp.button("Add Scenario", ColorStyles.SUCCESS, this::logAndGetScenario)
                .withCancelOption(() -> root.setScenarioPanelEnabled(false))
                .component();

        SwingPane.fluent(this).arrangedAs(SwingPane.BORDER, 0, 10).borderCollect(
                north(label), center(infoPanel), south(okCancelPanel));
    }

    private void logAndGetScenario() {
        try {
            Scenario scenario = new Scenario(nameField.getValue(), friendlyPanel.getSelectedScenario(), enemyPanel.getSelectedScenario());
            root.logScenarioCompleted(scenario);
            root.setScenarioPanelEnabled(false);
        } catch (Exception error) {
            Message.error(error.getMessage() + "; check all fields are filled");
            Logger.getAnonymousLogger().severe("CombatantInputPanel.logAndGetCombatant: " + error.getMessage());
        }
    }

    public void openNew() {
        root.setScenarioPanelEnabled(true);

        nameField.setValue("");
        nameField.setEnabled(true);

        friendlyPanel.updateSourceList(sibling.getFriendlyNPCs());
        enemyPanel.updateSourceList(sibling.getEnemies());
    }

    public void openExisting(Scenario selection) {
        root.setScenarioPanelEnabled(false);

        nameField.setValue(selection.getName());
        nameField.setEnabled(false);

        friendlyPanel.updateSourceList(sibling.getFriendlyNPCs());
        enemyPanel.updateSourceList(sibling.getEnemies());

        friendlyPanel.setTo(selection.list(true, true));
        enemyPanel.setTo(selection.list(false, true));
    }

}
