package txt_menu;

import character_info.Combatant;
import scenario_info.Scenario;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class ScenarioInputPanel extends JPanel {

    private final TxtMenu root;
    private final CompletedElementsList sibling;

    private final JTextField nameField;

    private final ListSelectionPanel<Combatant> friendlyPanel;
    private final ListSelectionPanel<Combatant> enemyPanel;

    private static final String labelText = "Select friendly NPCs and enemies to include in the scenario. " +
            "All party members (any non-npc friendly combatant) will automatically be included in any scenario. " +
            "This can be changed in a prompt before the actual combat, not here.";

    public ScenarioInputPanel(CompletedElementsList sibling, TxtMenu root) {
        this.root = root;
        this.sibling = sibling;

        setLayout(new GridLayout(0, 1));

        JButton label = new JButton(labelText);
        label.putClientProperty("FlatLaf.style", "font: $h1.regular.font");
        label.setEnabled(false);

        nameField = new JTextField();

        ArrayList<Object> friendlies = sibling.getFriendlyNPCs();
        friendlies.removeIf(friendly -> !((Combatant) friendly).isNPC());

        friendlyPanel = new ListSelectionPanel<>(friendlies, "Friendly NPCs");
        enemyPanel = new ListSelectionPanel<>(sibling.getEnemies(), "Enemies");

        JButton okButton = new JButton("Confirm");
        okButton.addActionListener(e -> logAndGetScenario());

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> root.setScenarioPanelEnabled(false));

        add(label);
        add(namePanel());
        add(friendlyPanel);
        add(enemyPanel);
        add(buttonPanel(okButton, cancelButton));
    }

    private void logAndGetScenario() {
        Scenario scenario = new Scenario(nameField.getText(), friendlyPanel.getSelected(), enemyPanel.getSelected());
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

        friendlyPanel.setTo(selection.with());
        enemyPanel.setTo(selection.against());
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
