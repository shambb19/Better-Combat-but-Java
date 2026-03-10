package combat_menu.popup;

import character_info.combatant.Combatant;
import _main.CombatMain;
import character_info.combatant.NPC;
import character_info.combatant.PC;
import scenario_info.Scenario;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Objects;

public class FinalizeCombatantsPopup extends JFrame {

    private final HashMap<Combatant, JPanel> friendlies = new HashMap<>();
    private final HashMap<Combatant, JPanel> enemies = new HashMap<>();

    private final JComboBox<Scenario> scenarioBox = new JComboBox<>();

    private final JPanel partyContainer = new JPanel(new GridLayout(0, 1));
    private final JPanel dynamicContainer = new JPanel(new GridLayout(0, 1));

    public FinalizeCombatantsPopup() {
        setTitle("Finalize Combat Information");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setAlwaysOnTop(true);

        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        add(getHeaderPanel());

        add(setupScenarioBox());
        add(new JSeparator());

        add(new JLabel("--- PARTY ---"));
        add(partyContainer);
        initializeParty();

        add(dynamicContainer);

        JButton button = new JButton("Confirm");
        button.addActionListener(e -> logAndBegin());
        add(button);

        updateScenario((Scenario) Objects.requireNonNull(scenarioBox.getSelectedItem()));

        pack();
        setLocationRelativeTo(null);
    }

    private JPanel getHeaderPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3));
        panel.add(new JLabel("Combatant"));
        panel.add(new JLabel("Initiative Roll"));
        panel.add(new JLabel("Absent?"));

        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, panel.getPreferredSize().height));
        return panel;
    }

    private JComboBox<Scenario> setupScenarioBox() {
        CombatMain.BATTLE.scenarios().forEach(scenarioBox::addItem);

        Scenario all = new Scenario("Everyone", CombatMain.BATTLE.friendlies(), CombatMain.BATTLE.enemies());
        scenarioBox.addItem(all);
        scenarioBox.setSelectedItem(all);

        scenarioBox.addActionListener(e -> {
            Scenario selected = (Scenario) scenarioBox.getSelectedItem();
            if (selected != null) {
                updateScenario(selected);
            }
        });

        return scenarioBox;
    }

    private void initializeParty() {
        CombatMain.BATTLE.friendlies().stream()
                .filter(c -> c instanceof PC)
                .forEach(pc -> {
                    JPanel panel = pc.getCombatantPanel();
                    friendlies.put(pc, panel);
                    partyContainer.add(panel);
                });
    }

    private void updateScenario(Scenario scenario) {
        enemies.clear();
        friendlies.entrySet().removeIf(entry -> entry.getKey() instanceof NPC);

        dynamicContainer.removeAll();

        dynamicContainer.add(new JLabel("--- ALLIES ---"));
        scenario.with().forEach(npc -> {
            if (npc instanceof NPC) {
                JPanel panel = npc.getCombatantPanel();
                friendlies.put(npc, panel);
                dynamicContainer.add(panel);
            }
        });

        dynamicContainer.add(new JSeparator());

        dynamicContainer.add(new JLabel("--- ENEMIES ---"));
        scenario.against().forEach(npc -> {
            JPanel panel = npc.getCombatantPanel();
            enemies.put(npc, panel);
            dynamicContainer.add(panel);
        });

        revalidate();
        repaint();
        pack();
    }

    private int getInitiative(JPanel combatantPanel) {
        String text = ((JTextField) combatantPanel.getComponent(1)).getText();
        try {
            return text.isEmpty() ? 0 : Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private boolean isAbsent(JPanel combatantPanel) {
        Component component = combatantPanel.getComponent(2);
        if (component instanceof JCheckBox box) {
            return box.isSelected();
        }
        return false;
    }

    private void logAndBegin() {
        CombatMain.BATTLE.friendlies().removeIf(combatant -> {
            if (!friendlies.containsKey(combatant)) {
                return true;
            }

            JPanel panel = friendlies.get(combatant);
            if (isAbsent(panel)) {
                return true;
            }

            combatant.setInitiative(getInitiative(panel));
            return false;
        });

        CombatMain.BATTLE.enemies().removeIf(combatant -> {
            if (!enemies.containsKey(combatant)) {
                return true;
            }

            JPanel panel = enemies.get(combatant);
            combatant.setInitiative(getInitiative(panel));
            return false;
        });

        dispose();
        CombatMain.start();
    }
}