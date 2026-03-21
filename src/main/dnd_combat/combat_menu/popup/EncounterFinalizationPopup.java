package combat_menu.popup;

import __main.EncounterInfo;
import __main.Main;
import _global_list.Combatants;
import character_info.combatant.Combatant;
import character_info.combatant.NPC;
import character_info.combatant.PC;
import com.formdev.flatlaf.FlatClientProperties;
import encounter_info.Scenario;
import format.ColorStyle;
import format.SwingStyles;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EncounterFinalizationPopup extends JDialog {

    private final List<CombatantCard> activeCards = new ArrayList<>();
    private final JComboBox<Scenario> scenarioBox = new JComboBox<>();

    private final JPanel partyContainer = new JPanel();
    private final JPanel dynamicContainer = new JPanel();

    private EncounterFinalizationPopup() {
        setTitle("Finalize Combat Information");
        setModal(false);
        setAlwaysOnTop(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setAlwaysOnTop(true);
        setIconImage(Main.getImage());

        setupVerticalContainer(partyContainer);
        setupVerticalContainer(dynamicContainer);

        JPanel root = new JPanel(new BorderLayout(15, 15));
        root.setBorder(new EmptyBorder(15, 15, 15, 15));
        setContentPane(root);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel scenarioLabel = new JLabel("Select Combat Scenario");
        scenarioLabel.putClientProperty(FlatClientProperties.STYLE, "font: small");
        scenarioLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        scenarioBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        scenarioBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        topPanel.add(scenarioLabel);
        topPanel.add(Box.createVerticalStrut(5));
        topPanel.add(setupScenarioBox());
        topPanel.add(Box.createVerticalStrut(15));

        root.add(topPanel, BorderLayout.NORTH);

        JPanel scrollContent = new JPanel();
        scrollContent.setLayout(new BoxLayout(scrollContent, BoxLayout.Y_AXIS));

        addSectionHeader(scrollContent, "THE PARTY", ColorStyle.PARTY.getColor());
        scrollContent.add(partyContainer);
        scrollContent.add(Box.createRigidArea(new Dimension(0, 20)));
        scrollContent.add(dynamicContainer);

        JScrollPane scrollPane = new JScrollPane(scrollContent);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setPreferredSize(new Dimension(550, 600));
        root.add(scrollPane, BorderLayout.CENTER);

        JButton confirmButton = new JButton("Begin Encounter");
        confirmButton.setBackground(ColorStyle.DARKER_GREEN.getColor());
        confirmButton.addActionListener(e -> logAndBegin());

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.add(confirmButton);
        root.add(footer, BorderLayout.SOUTH);

        initializeParty();
        updateScenario((Scenario) Objects.requireNonNull(scenarioBox.getSelectedItem()));

        pack();
        setLocationRelativeTo(null);

        SwingUtilities.invokeLater(confirmButton::requestFocusInWindow);
    }

    private void setupVerticalContainer(JPanel panel) {
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setOpaque(false);
    }

    public static void run() {
        new EncounterFinalizationPopup().setVisible(true);
    }

    private void addSectionHeader(JPanel container, String title, Color accent) {
        JLabel label = new JLabel(title.toUpperCase());
        label.setFont(new Font("SansSerif", Font.BOLD, 12));
        label.setForeground(accent);
        label.setBorder(new EmptyBorder(10, 5, 5, 0));

        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(label);
    }

    private JComboBox<Scenario> setupScenarioBox() {
        EncounterInfo.getBattle().scenarios().forEach(scenarioBox::addItem);
        Scenario all = Combatants.toScenario();
        scenarioBox.addItem(all);
        scenarioBox.setSelectedItem(all);
        scenarioBox.addActionListener(e -> updateScenario((Scenario) scenarioBox.getSelectedItem()));
        return scenarioBox;
    }

    private void initializeParty() {
        EncounterInfo.getFriendlies().stream()
                .filter(c -> c instanceof PC)
                .forEach(pc -> {
                    CombatantCard card = new CombatantCard(pc, ColorStyle.PARTY.getColor());
                    activeCards.add(card);
                    partyContainer.add(card);
                    partyContainer.add(Box.createVerticalStrut(8));
                });
    }

    private void updateScenario(Scenario scenario) {
        if (scenario == null) return;

        activeCards.removeIf(card -> card.combatant instanceof NPC);
        dynamicContainer.removeAll();

        if (scenario.containsFriendlies()) {
            addSectionHeader(dynamicContainer, "ALLIES", ColorStyle.NPC.getColor());
            scenario.withListAllOccurrences().forEach(npc -> addCombatantToUI((NPC) npc, dynamicContainer, ColorStyle.NPC.getColor()));
        }

        addSectionHeader(dynamicContainer, "ENEMIES", ColorStyle.ENEMY.getColor());
        scenario.againstListAllOccurrences().forEach(npc -> addCombatantToUI((NPC) npc, dynamicContainer, ColorStyle.ENEMY.getColor()));

        revalidate();
        repaint();
    }

    private void addCombatantToUI(NPC npc, JPanel container, Color accent) {
        Combatant instance = npc.copy();
        CombatantCard card = new CombatantCard(instance, accent);
        activeCards.add(card);
        container.add(card);
        container.add(Box.createVerticalStrut(8));
    }

    private void logAndBegin() {
        EncounterInfo.getFriendlies().clear();
        EncounterInfo.getEnemies().clear();

        for (CombatantCard card : activeCards) {
            if (card.isAbsent()) {
                continue;
            }
            Combatant c = card.combatant;
            c.setInitiative(card.getInitiative());
            if (c.isEnemy()) {
                EncounterInfo.getEnemies().add(c);
            } else EncounterInfo.getFriendlies().add(c);
        }

        dispose();
        Main.finalizeCombat();
    }

    private static class CombatantCard extends JPanel {

        private final Combatant combatant;
        private final JSpinner spinner;
        private final JCheckBox absentCheck;

        public CombatantCard(Combatant combatant, Color accentColor) {
            this.combatant = combatant;
            setLayout(new BorderLayout(15, 0));
            setAlignmentX(Component.LEFT_ALIGNMENT);

            setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

            putClientProperty(FlatClientProperties.STYLE, "arc: 12; background: lighten($Panel.background, 3%)");
            setBorder(new CompoundBorder(
                    new MatteBorder(0, 5, 0, 0, accentColor),
                    new EmptyBorder(10, 15, 10, 15)
            ));

            JLabel nameLabel = new JLabel(combatant.name());
            nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD, 13f));
            add(nameLabel, BorderLayout.CENTER);

            JPanel controls = new JPanel(new GridLayout(1, 0));
            controls.setOpaque(false);

            spinner = new JSpinner(new SpinnerNumberModel(10, 1, 20, 1));
            spinner.setPreferredSize(new Dimension(60, 28));
            SwingStyles.setHighlightsOnFocus(spinner);

            JPanel spinnerGroup = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
            spinnerGroup.setOpaque(false);
            JLabel initLabel = new JLabel("Initiative:");
            initLabel.putClientProperty(FlatClientProperties.STYLE, "font: small; foreground: #888888");
            spinnerGroup.add(initLabel);
            spinnerGroup.add(spinner);

            absentCheck = new JCheckBox("Absent");
            absentCheck.addActionListener(e -> updateVisualState());

            controls.add(spinnerGroup);
            if (combatant instanceof PC) {
                controls.add(absentCheck);
            } else {
                controls.add(new JLabel());
            }
            add(controls, BorderLayout.EAST);
        }

        private void updateVisualState() {
            boolean isAbsent = absentCheck.isSelected();
            spinner.setEnabled(!isAbsent);
            putClientProperty(FlatClientProperties.STYLE, isAbsent ?
                    "arc: 12; background: darken($Panel.background, 5%)" :
                    "arc: 12; background: lighten($Panel.background, 3%)");
            repaint();
        }

        public int getInitiative() {
            return (int) spinner.getValue();
        }

        public boolean isAbsent() {
            return absentCheck.isSelected();
        }
    }
}