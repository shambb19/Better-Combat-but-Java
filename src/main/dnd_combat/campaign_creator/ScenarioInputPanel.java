package campaign_creator;

import character_info.combatant.Combatant;
import character_info.combatant.PC;
import scenario_info.Scenario;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class ScenarioInputPanel extends JPanel {

    private final TxtMenu root;
    private final CompletedElementsList sibling;

    private final JTextField nameField = new JTextField();

    private final ListSelectionPanel<Combatant> friendlyPanel;
    private final ListSelectionPanel<Combatant> enemyPanel;

    private static final String labelText = "Select friendly NPCs and enemies to include in the scenario from " +
            "the panels on the left. All party members (any non-npc friendly combatant) will automatically be " +
            "included in any scenario. You will be prompted to remove any absent party members when you open " +
            "the actual combat encounter. If you have a generic NPC that you would like to add several of " +
            "(say, a bunch of Orcs), click on their name and then select \"Edit\" when prompted. To remove a " +
            "combatant from an encounter, select \"Remove\" in the same prompt.";

    public ScenarioInputPanel(CompletedElementsList sibling, TxtMenu root) {
        this.root = root;
        this.sibling = sibling;

        ArrayList<Combatant> friendlies = sibling.getFriendlyNPCs();
        friendlies.removeIf(friendly -> friendly instanceof PC);

        friendlyPanel = new ListSelectionPanel<>(friendlies, "Friendly NPCs");
        enemyPanel = new ListSelectionPanel<>(sibling.getEnemies(), "Enemies");

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextArea label = new JTextArea(labelText);
        label.setLineWrap(true);
        label.setWrapStyleWord(true);
        label.setEditable(false);
        label.setBackground(null);
        label.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        JPanel namePanel = namePanel();
        namePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JPanel listsContainer = new JPanel(new GridLayout(1, 2, 10, 0));
        listsContainer.add(friendlyPanel);
        listsContainer.add(enemyPanel);

        JButton okButton = new JButton("Confirm");
        okButton.addActionListener(e -> logAndGetScenario());

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> root.setScenarioPanelEnabled(false));

        add(label);
        add(Box.createVerticalStrut(10));
        add(namePanel);
        add(Box.createVerticalStrut(10));
        add(listsContainer);
        add(Box.createVerticalGlue());
        add(buttonPanel(okButton, cancelButton));
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

    private JPanel namePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        JLabel label = new JLabel("Scenario Name:");

        panel.add(label);
        panel.add(nameField);

        return panel;
    }

    private JPanel buttonPanel(JButton okButton, JButton cancelButton) {
        JPanel panel = new JPanel(new FlowLayout());

        panel.add(okButton);
        panel.add(cancelButton);

        return panel;
    }

}
