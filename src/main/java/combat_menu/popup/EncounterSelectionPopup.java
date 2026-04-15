package combat_menu.popup;

import __main.Main;
import __main.manager.EncounterManager;
import combat_object.combatant.Combatant;
import combat_object.combatant.NPC;
import combat_object.scenario.Scenario;
import format.ColorStyles;
import lombok.*;
import lombok.experimental.*;
import swing.swing_comp.SwingComp;
import swing.swing_comp.SwingPane;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class EncounterSelectionPopup extends JDialog {

    static Color
            BG_DIALOG = new Color(0x1E, 0x21, 0x28),
            BG_BAR = new Color(0x19, 0x1C, 0x22),
            BG_CARD = new Color(0x23, 0x26, 0x2E),
            BG_CARD_ABS = new Color(0x1C, 0x1E, 0x24),
            BG_FIELD = new Color(0x2A, 0x2E, 0x3A),
            BG_CONFIRM = new Color(0x1D, 0x9E, 0x75),

    BORDER = new Color(0x2A, 0x2E, 0x3A),
            BORDER_FLD = new Color(0x3A, 0x3E, 0x4A);

    @NonFinal boolean updatingScenario = false;

    List<CombatantCard> activeCards = new ArrayList<>();
    JPanel partyContainer, dynamicContainer;
    JButton beginButton;

    public static EncounterSelectionPopup newInstance() {
        return new EncounterSelectionPopup();
    }

    private EncounterSelectionPopup() {
        setTitle("Finalize Combat Information");
        setModal(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setAlwaysOnTop(true);
        setIconImage(Main.getAppIcon().getImage());
        setBackground(BG_DIALOG);
        setLayout(new BorderLayout());
        getRootPane().setBorder(BorderFactory.createLineBorder(BORDER, 1));

        JPanel topBar = SwingPane.panelIn(this, BorderLayout.NORTH).withLayout(SwingPane.FLOW_LEFT)
                .withGaps(10, 0)
                .withBackground(BG_BAR)
                .withBorder(new MatteBorder(0, 0, 1, 0, BORDER))
                .component();

        SwingComp.label("Combat scenario:").withDerivedFont(Font.PLAIN, 12f)
                .withForeground(ColorStyles.TEXT_MUTED)
                .in(topBar);

        JComboBox<Scenario> scenarioCombo = new JComboBox<>(
                EncounterManager.getEncounter().getScenarios().toArray(new Scenario[0]));
        scenarioCombo.setBackground(BG_FIELD);
        scenarioCombo.setForeground(ColorStyles.TEXT_PRIMARY);
        scenarioCombo.setFont(scenarioCombo.getFont().deriveFont(Font.PLAIN, 12f));
        styleComboBox(scenarioCombo);
        scenarioCombo.setSelectedIndex(-1);
        scenarioCombo.addActionListener(e -> updateScenario((Scenario) scenarioCombo.getSelectedItem()));
        topBar.add(scenarioCombo);

        partyContainer = boxPanel();
        dynamicContainer = boxPanel();

        JPanel scrollContent = boxPanel();
        scrollContent.setBackground(BG_DIALOG);
        scrollContent.setBorder(new EmptyBorder(8, 12, 12, 12));
        scrollContent.add(sectionLabel("The Party", ColorStyles.PARTY));
        scrollContent.add(partyContainer);
        scrollContent.add(vgap());
        scrollContent.add(dynamicContainer);

        JScrollPane scroller = new JScrollPane(scrollContent);
        scroller.setBorder(null);
        scroller.getViewport().setBackground(BG_DIALOG);
        scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroller.setPreferredSize(new Dimension(500, 520));
        add(scroller, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 8));
        footer.setBackground(BG_BAR);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER));
        add(footer, BorderLayout.SOUTH);

        beginButton = SwingComp.button("Begin Encounter", this::logAndBegin)
                .withDerivedFont(Font.PLAIN, 13f)
                .withBackgroundAndForeground(BG_CONFIRM, new Color(0xd8, 0xf4, 0xec))
                .withEmptyBorder(8, 20, 8, 20)
                .disabled()
                .in(footer).component();

        initializeParty();

        pack();
        setLocationRelativeTo(null);
        SwingUtilities.invokeLater(beginButton::requestFocusInWindow);
    }

    private static void styleComboBox(JComboBox<?> combo) {
        SwingComp.modifiable(combo)
                .withPaddedBorder(new LineBorder(BORDER_FLD, 1), 2, 8, 2, 8);
    }

    private void updateScenario(Scenario scenario) {
        if (updatingScenario) return;
        updatingScenario = true;
        activeCards.removeIf(card -> card.getCombatant() instanceof NPC);
        dynamicContainer.removeAll();
        if (scenario == null) return;

        if (scenario.containsFriendlies()) {
            dynamicContainer.add(sectionLabel("Allies", ColorStyles.ALLY));
            scenario.list(true, false).forEach(npc ->
                    addCombatantCard(npc, ColorStyles.ALLY));
        }
        dynamicContainer.add(sectionLabel("Enemies", ColorStyles.ENEMY));
        scenario.list(false, false).forEach(npc -> addCombatantCard(npc, ColorStyles.ENEMY));
        dynamicContainer.revalidate();
        dynamicContainer.repaint();
        beginButton.setEnabled(true);

        updatingScenario = false;
    }

    private static JPanel boxPanel() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        return p;
    }

    private static JLabel sectionLabel(String text, Color color) {
        JLabel l = new JLabel(text.toUpperCase());
        l.setFont(l.getFont().deriveFont(Font.BOLD, 10f));
        l.setForeground(color);
        l.setBorder(new EmptyBorder(10, 2, 6, 2));
        l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }

    private static Component vgap() {
        return Box.createRigidArea(new Dimension(0, 6));
    }


    private void logAndBegin() {
        var activeCombatants = activeCards.stream()
                .filter(CombatantCard::isPresent)
                .map(CombatantCard::getCombatant)
                .collect(Collectors.partitioningBy(Combatant::isEnemy));

        EncounterManager.getEncounter().setEnemies(activeCombatants.get(true));
        EncounterManager.getEncounter().setFriendlies(activeCombatants.get(false));

        dispose();
        Main.finalizeAndStartCombat();
    }

    private void initializeParty() {
        EncounterManager.getParty().forEach(pc -> {
            CombatantCard card = new CombatantCard(pc, ColorStyles.PARTY, true);
            activeCards.add(card);
            partyContainer.add(card);
            partyContainer.add(vgap());
        });
    }

    private void addCombatantCard(NPC npc, Color accent) {
        CombatantCard card = new CombatantCard(npc, accent, false);
        activeCards.add(card);
        dynamicContainer.add(card);
        dynamicContainer.add(vgap());
    }

    @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
    private static class CombatantCard extends JPanel {

        static int ACCENT_W = 3, CARD_H = 52;

        Combatant combatant;
        JSpinner spinner;
        JCheckBox absentCheck;
        JPanel accentBar;

        CombatantCard(Combatant combatant, Color accent, boolean showAbsent) {
            this.combatant = combatant;

            SwingPane.modifiable(this).withLayout(SwingPane.BORDER).withGaps(10, 0)
                    .withBackground(BG_CARD)
                    .opaque()
                    .withMaximumSize(Integer.MAX_VALUE, CARD_H)
                    .withPreferredSize(0, CARD_H)
                    .onLeft()
                    .withEmptyBorder(0, 0, 0, 12);

            accentBar = SwingPane.panelIn(this, BorderLayout.WEST)
                    .withPreferredSize(ACCENT_W, 0)
                    .withBackground(accent)
                    .opaque()
                    .component();

            SwingComp.label(combatant.getName())
                    .withDerivedFont(Font.BOLD, 14f)
                    .withForeground(ColorStyles.TEXT_PRIMARY)
                    .withEmptyBorder(0, 10, 0, 0)
                    .in(this, BorderLayout.CENTER);

            JPanel right = new JPanel(new GridBagLayout());
            right.setOpaque(false);
            add(right, BorderLayout.EAST);

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(0, 8, 0, 0);
            gbc.anchor = GridBagConstraints.CENTER;

            JLabel initLabel = SwingComp.label("Initiative").withDerivedFont(Font.PLAIN, 11f)
                    .withForeground(ColorStyles.TEXT_MUTED).component();
            right.add(initLabel, gbc);

            SpinnerNumberModel model = new SpinnerNumberModel(10, 1, 20, 1);
            spinner = new JSpinner(model);
            spinner.setPreferredSize(new Dimension(60, 26));
            styleSpinner(spinner);
            right.add(spinner, gbc);

            if (showAbsent) {
                absentCheck = SwingComp.checkBox("Absent").withAction(this::updateAbsentState)
                        .withDerivedFont(Font.PLAIN, 11f)
                        .withBackgroundAndForeground(BG_CARD, ColorStyles.TEXT_MUTED)
                        .transparent()
                        .withoutPaintedFocus()
                        .component();
                right.add(absentCheck, gbc);
            } else {
                absentCheck = null;
            }
        }

        private static void setForegroundAlpha(Container c, float alpha) {
            for (Component child : c.getComponents()) {
                if (child instanceof JLabel lbl) {
                    Color fg = lbl.getForeground();
                    lbl.setForeground(new Color(
                            fg.getRed(), fg.getGreen(), fg.getBlue(),
                            Math.round(255 * alpha)));
                }
                if (child instanceof Container cont) setForegroundAlpha(cont, alpha);
            }
        }

        private static void styleSpinner(JSpinner spinner) {
            spinner.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_FLD, 1),
                    new EmptyBorder(0, 4, 0, 4)));
            spinner.setBackground(BG_FIELD);
            spinner.setOpaque(true);

            JComponent editor = spinner.getEditor();
            if (editor instanceof JSpinner.DefaultEditor de) {
                SwingComp.modifiable(de.getTextField())
                        .withBackground(BG_FIELD)
                        .withForegroundAndCaretColor(ColorStyles.TEXT_PRIMARY)
                        .withEmptyBorder(0, 2, 0, 2)
                        .centered();
            }
        }

        private void updateAbsentState() {
            boolean absent = absentCheck.isSelected();
            spinner.setEnabled(!absent);
            setBackground(absent ? BG_CARD_ABS : BG_CARD);
            accentBar.setBackground(absent
                    ? new Color(0x2A, 0x2E, 0x3A)
                    : accentBar.getBackground());

            if (!absent) {
                Color orig = (Color) getClientProperty("accent");
                if (orig != null) accentBar.setBackground(orig);
            } else {
                putClientProperty("accent", accentBar.getBackground());
                accentBar.setBackground(new Color(0x2A, 0x2E, 0x3A));
            }

            float alpha = absent ? 0.5f : 1.0f;
            setForegroundAlpha(this, alpha);
            repaint();
        }

        public Combatant getCombatant() {
            combatant.setInitiative((int) spinner.getValue());
            return combatant;
        }

        public boolean isPresent() {
            return absentCheck == null || !absentCheck.isSelected();
        }
    }
}