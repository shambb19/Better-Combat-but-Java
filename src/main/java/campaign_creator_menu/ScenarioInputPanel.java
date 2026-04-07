package campaign_creator_menu;

import character_info.combatant.Combatant;
import character_info.combatant.PC;
import encounter_info.Scenario;
import format.swing_comp.SwingComp;
import format.swing_comp.SwingPane;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ScenarioInputPanel extends JPanel {

    private static final String labelText = "Select friendly NPCs and enemies to include in the scenario from " +
            "the panels on the left. All party members (any non-npc friendly combatant) will automatically be " +
            "included in any scenario. You will be prompted to remove any absent party members when you open " +
            "the actual combat encounter. If you have a generic NPC that you would like to add several of " +
            "(say, a bunch of Orcs), click on their name and then select \"Edit\" when prompted. To remove a " +
            "combatant from an encounter, select \"Remove\" in the same prompt.";
    private final CampaignCreatorMenu root;
    private final CompletedElementsList sibling;
    private final JTextField nameField;
    private final ListSelectionPanel<Combatant> friendlyPanel;
    private final ListSelectionPanel<Combatant> enemyPanel;

    public ScenarioInputPanel(CompletedElementsList sibling, CampaignCreatorMenu root) {
        this.root = root;
        this.sibling = sibling;

        List<Combatant> friendlies = sibling.getFriendlyNPCs().stream()
                .filter(PC.class::isInstance).toList();

        friendlyPanel = new ListSelectionPanel<>(friendlies, "Friendly NPCs");
        enemyPanel = new ListSelectionPanel<>(sibling.getEnemies(), "Enemies");

        JTextArea label = new JTextArea(labelText);
        label.setLineWrap(true);
        label.setWrapStyleWord(true);
        label.setEditable(false);
        label.setBackground(null);
        label.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        JLabel nameLabel = SwingComp.label("Scenario Name:")
                .withSize(Integer.MAX_VALUE, 40)
                .build();

        nameField = new JTextField();

        JPanel namePanel = SwingPane.panel().collect(nameLabel, nameField).withLayout(SwingPane.FLOW).build();

        JPanel listsContainer = SwingPane.panel().collect(friendlyPanel, enemyPanel)
                .withLayout(SwingPane.SINGLE_ROW)
                .build();

        JPanel okCancelPanel = SwingComp.button("Add Scenario", this::logAndGetScenario)
                .withCancelOption(() -> root.setScenarioPanelEnabled(false))
                .build();

        SwingPane.modifiable(this).collect(
                        nameLabel, SwingComp.gap(10),
                        namePanel, SwingComp.gap(10),
                        listsContainer, Box.createVerticalGlue(),
                        okCancelPanel
                ).withLayout(SwingPane.VERTICAL_BOX)
                .withLabeledBorder("Scenario Input")
                .build();
    }

    private void logAndGetScenario() {
        Scenario scenario = new Scenario(nameField.getText(), friendlyPanel.getSelectedScenario(), enemyPanel.getSelectedScenario());
        root.logScenarioCompleted(scenario);
        root.setScenarioPanelEnabled(false);
    }

    public void openNew() {
        root.setScenarioPanelEnabled(true);

        nameField.setText("");
        nameField.setEnabled(true);

        friendlyPanel.updateSourceList(sibling.getFriendlyNPCs());
        enemyPanel.updateSourceList(sibling.getEnemies());
    }

    public void openExisting(Scenario selection) {
        root.setScenarioPanelEnabled(false);

        nameField.setText(selection.name());
        nameField.setEnabled(false);

        friendlyPanel.updateSourceList(sibling.getFriendlyNPCs());
        enemyPanel.updateSourceList(sibling.getEnemies());

        friendlyPanel.setTo(selection.withListSingleOccurrence());
        enemyPanel.setTo(selection.againstListSingleOccurrence());
    }

}
