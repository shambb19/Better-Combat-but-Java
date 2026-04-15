package campaign_creator_menu;

import combat_object.combatant.NPC;
import combat_object.scenario.Scenario;
import lombok.*;
import lombok.experimental.*;
import swing.ValidatedField;
import swing.swing_comp.SwingComp;
import swing.swing_comp.SwingPane;
import util.Message;

import javax.swing.*;
import java.awt.*;
import java.util.logging.Logger;

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

        JPanel namePanel = SwingPane.panel().collect("Scenario Name", nameField).withLayout(SwingPane.FLOW).component();

        JPanel listsContainer = SwingPane.panel().withLayout(SwingPane.ONE_COLUMN).withGaps(0, 20)
                .collect(friendlyPanel, enemyPanel, Box.createVerticalGlue())
                .component();

        JPanel infoPanel = SwingPane.panel().withLayout(SwingPane.BORDER)
                .with(namePanel, BorderLayout.NORTH)
                .with(listsContainer, BorderLayout.CENTER)
                .component();

        JPanel okCancelPanel = SwingComp.button("Add Scenario", this::logAndGetScenario)
                .withCancelOption(() -> root.setScenarioPanelEnabled(false))
                .component();

        SwingPane.modifiable(this).withLayout(SwingPane.BORDER).withGaps(0, 10)
                .with(label, BorderLayout.NORTH)
                .with(infoPanel, BorderLayout.CENTER)
                .with(okCancelPanel, BorderLayout.SOUTH);
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
