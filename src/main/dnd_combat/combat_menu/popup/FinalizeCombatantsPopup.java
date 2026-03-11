package combat_menu.popup;

import character_info.combatant.Combatant;
import _main.CombatMain;
import character_info.combatant.NPC;
import character_info.combatant.PC;
import scenario_info.Scenario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FinalizeCombatantsPopup extends JFrame {

    private final Map<Combatant, JPanel> combatantPanelMap = new HashMap<>();
    private final JComboBox<Scenario> scenarioBox = new JComboBox<>();

    private final JPanel partyContainer = new JPanel(new GridLayout(0, 1, 0, 5));
    private final JPanel dynamicContainer = new JPanel(new GridLayout(0, 1, 0, 5));

    public FinalizeCombatantsPopup() {
        setTitle("Finalize Combat Information");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setAlwaysOnTop(true);

        setLayout(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.add(setupScenarioBox());
        topPanel.add(Box.createVerticalStrut(10));
        topPanel.add(getHeaderPanel());
        add(topPanel, BorderLayout.NORTH);

        JPanel scrollContent = new JPanel();
        scrollContent.setLayout(new BoxLayout(scrollContent, BoxLayout.Y_AXIS));
        scrollContent.setBorder(new EmptyBorder(10, 10, 10, 10));

        addSectionHeader(scrollContent, "--- PARTY ---");
        scrollContent.add(partyContainer);
        scrollContent.add(Box.createVerticalStrut(15));
        scrollContent.add(dynamicContainer);

        JScrollPane scrollPane = new JScrollPane(scrollContent);
        scrollPane.setPreferredSize(new Dimension(500, 600)); // Constraint size
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Smoother scrolling
        add(scrollPane, BorderLayout.CENTER);

        JButton confirmBtn = new JButton("Begin Encounter");
        confirmBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        confirmBtn.addActionListener(e -> logAndBegin());

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.add(confirmBtn);
        add(footer, BorderLayout.SOUTH);

        initializeParty();
        updateScenario((Scenario) Objects.requireNonNull(scenarioBox.getSelectedItem()));

        pack();
        setLocationRelativeTo(null);
    }

    private void addSectionHeader(JPanel container, String title) {
        JLabel label = new JLabel(title);
        label.setFont(new Font("SansSerif", Font.BOLD, 12));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(label);
    }

    private JPanel getHeaderPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3));
        panel.add(new JLabel("Combatant Name"));
        panel.add(new JLabel("Initiative"));
        panel.add(new JLabel("Check if Absent"));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        panel.setBorder(new EmptyBorder(0, 10, 0, 10));
        return panel;
    }

    private JComboBox<Scenario> setupScenarioBox() {
        CombatMain.BATTLE.scenarios().forEach(scenarioBox::addItem);

        Scenario all = new Scenario("Full Roster (All)", CombatMain.BATTLE.mapFriendlies(), CombatMain.BATTLE.mapEnemies());
        scenarioBox.addItem(all);
        scenarioBox.setSelectedItem(all);

        scenarioBox.addActionListener(e -> updateScenario((Scenario) scenarioBox.getSelectedItem()));
        return scenarioBox;
    }

    private void initializeParty() {
        CombatMain.BATTLE.friendlies().stream()
                .filter(c -> c instanceof PC)
                .forEach(pc -> {
                    JPanel panel = pc.getCombatantPanel();
                    combatantPanelMap.put(pc, panel);
                    partyContainer.add(panel);
                });
    }

    private void updateScenario(Scenario scenario) {
        if (scenario == null) return;

        combatantPanelMap.entrySet().removeIf(entry -> entry.getKey() instanceof NPC);
        dynamicContainer.removeAll();

        if (scenario.containsFriendlies()) {
            addSectionHeader(dynamicContainer, "--- ALLIES ---");
            scenario.withListAllOccurrences().forEach(npc -> addCombatantToUI((NPC) npc, dynamicContainer));
        } else {
            addSectionHeader(dynamicContainer, "--- NO ALLIES ---");
        }

        dynamicContainer.add(Box.createVerticalStrut(10));
        dynamicContainer.add(new JSeparator());
        addSectionHeader(dynamicContainer, "--- ENEMIES ---");

        scenario.againstListAllOccurrences().forEach(npc -> addCombatantToUI((NPC) npc, dynamicContainer));

        revalidate();
        repaint();
    }

    private void addCombatantToUI(NPC npc, JPanel container) {
        Combatant instance = npc.copy();
        JPanel panel = instance.getCombatantPanel();
        combatantPanelMap.put(instance, panel);
        container.add(panel);
    }

    private int getInitiative(JPanel panel) {
        try {
            Component comp = panel.getComponent(1);
            if (comp instanceof JTextField tf) {
                return tf.getText().isEmpty() ? 0 : Integer.parseInt(tf.getText());
            }
        } catch (Exception e) {
            return 0;
        }
        return 0;
    }

    private boolean isAbsent(JPanel panel) {
        return panel.getComponent(2) instanceof JCheckBox box && box.isSelected();
    }

    private void logAndBegin() {
        CombatMain.BATTLE.friendlies().clear();
        CombatMain.BATTLE.enemies().clear();

        combatantPanelMap.forEach((combatant, panel) -> {
            if (!isAbsent(panel)) {
                combatant.setInitiative(getInitiative(panel));

                if (combatant.isEnemy()) {
                    CombatMain.BATTLE.enemies().add(combatant);
                } else {
                    CombatMain.BATTLE.friendlies().add(combatant);
                }
            }
        });

        dispose();
        CombatMain.start();
    }

}