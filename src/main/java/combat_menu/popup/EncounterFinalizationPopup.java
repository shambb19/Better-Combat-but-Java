package combat_menu.popup;

import __main.EncounterInfo;
import __main.Main;
import _global_list.Combatants;
import character_info.combatant.Combatant;
import character_info.combatant.NPC;
import encounter_info.Scenario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class EncounterFinalizationPopup extends JDialog {

    // ── Palette ───────────────────────────────────────────────────────────────
    private static final Color BG_DIALOG = new Color(0x1E, 0x21, 0x28);
    private static final Color BG_BAR = new Color(0x19, 0x1C, 0x22);
    private static final Color BG_CARD = new Color(0x23, 0x26, 0x2E);
    private static final Color BG_CARD_ABS = new Color(0x1C, 0x1E, 0x24);
    private static final Color BG_FIELD = new Color(0x2A, 0x2E, 0x3A);
    private static final Color BG_CONFIRM = new Color(0x1D, 0x9E, 0x75);

    private static final Color BORDER = new Color(0x2A, 0x2E, 0x3A);
    private static final Color BORDER_FLD = new Color(0x3A, 0x3E, 0x4A);

    private static final Color FG_PRIMARY = new Color(0xD8, 0xDC, 0xE8);
    private static final Color FG_MUTED = new Color(0x6B, 0x70, 0x80);
    private static final Color FG_HINT = new Color(0x50, 0x55, 0x68);

    private static final Color ACCENT_PARTY = new Color(0x5D, 0xCA, 0xA5);
    private static final Color ACCENT_ALLY = new Color(0x7F, 0x77, 0xDD);
    private static final Color ACCENT_ENEMY = new Color(0xE2, 0x4B, 0x4A);

    private static final Color SECTION_PARTY = new Color(0x5D, 0xCA, 0xA5);
    private static final Color SECTION_ALLY = new Color(0x7F, 0x77, 0xDD);
    private static final Color SECTION_ENEMY = new Color(0xE2, 0x4B, 0x4A);

    // ── State ─────────────────────────────────────────────────────────────────
    private final List<CombatantCard> activeCards = new ArrayList<>();
    private final JPanel partyContainer;
    private final JPanel dynamicContainer;

    // ── Constructor ───────────────────────────────────────────────────────────

    private EncounterFinalizationPopup() {
        setTitle("Finalize Combat Information");
        setModal(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setAlwaysOnTop(true);
        setIconImage(Main.getImage());
        setBackground(BG_DIALOG);
        setLayout(new BorderLayout());
        getRootPane().setBorder(BorderFactory.createLineBorder(BORDER, 1));

        // ── Top bar: scenario selector ────────────────────────────────────────
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        topBar.setBackground(BG_BAR);
        topBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));
        add(topBar, BorderLayout.NORTH);

        JLabel scenarioLabel = new JLabel("Combat scenario:");
        scenarioLabel.setFont(scenarioLabel.getFont().deriveFont(Font.PLAIN, 12f));
        scenarioLabel.setForeground(FG_MUTED);
        topBar.add(scenarioLabel);

        JComboBox<Scenario> scenarioCombo = new JComboBox<>(
                EncounterInfo.getBattle().scenarios().toArray(new Scenario[0]));
        scenarioCombo.setBackground(BG_FIELD);
        scenarioCombo.setForeground(FG_PRIMARY);
        scenarioCombo.setFont(scenarioCombo.getFont().deriveFont(Font.PLAIN, 12f));
        styleComboBox(scenarioCombo);
        scenarioCombo.setSelectedIndex(-1);
        scenarioCombo.addActionListener(
                e -> updateScenario((Scenario) scenarioCombo.getSelectedItem()));
        topBar.add(scenarioCombo);

        // ── Scroll area ───────────────────────────────────────────────────────
        partyContainer = boxPanel();
        dynamicContainer = boxPanel();

        JPanel scrollContent = boxPanel();
        scrollContent.setBackground(BG_DIALOG);
        scrollContent.setBorder(new EmptyBorder(8, 12, 12, 12));
        scrollContent.add(sectionLabel("The Party", SECTION_PARTY));
        scrollContent.add(partyContainer);
        scrollContent.add(vgap());
        scrollContent.add(dynamicContainer);

        JScrollPane scroller = new JScrollPane(scrollContent);
        scroller.setBorder(null);
        scroller.getViewport().setBackground(BG_DIALOG);
        scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroller.setPreferredSize(new Dimension(500, 520));
        add(scroller, BorderLayout.CENTER);

        // ── Footer: begin button ──────────────────────────────────────────────
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 8));
        footer.setBackground(BG_BAR);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER));
        add(footer, BorderLayout.SOUTH);

        JButton beginButton = new JButton("Begin Encounter");
        beginButton.setFont(beginButton.getFont().deriveFont(Font.PLAIN, 13f));
        beginButton.setBackground(BG_CONFIRM);
        beginButton.setForeground(new Color(0xD8, 0xF4, 0xEC));
        beginButton.setBorder(new EmptyBorder(8, 20, 8, 20));
        beginButton.setFocusPainted(false);
        beginButton.setOpaque(true);
        beginButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        beginButton.addActionListener(e -> logAndBegin());
        footer.add(beginButton);

        // ── Init ──────────────────────────────────────────────────────────────
        initializeParty();
        updateScenario(Combatants.toScenario());

        pack();
        setLocationRelativeTo(null);
        SwingUtilities.invokeLater(beginButton::requestFocusInWindow);
    }

    private static void styleComboBox(JComboBox<?> combo) {
        combo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_FLD, 1),
                new EmptyBorder(2, 8, 2, 8)));
    }

    // ── Section labels ────────────────────────────────────────────────────────

    private void updateScenario(Scenario scenario) {
        if (scenario == null) return;

        activeCards.removeIf(card -> card.getCombatant() instanceof NPC);
        dynamicContainer.removeAll();

        if (scenario.containsFriendlies()) {
            dynamicContainer.add(sectionLabel("Allies", SECTION_ALLY));
            scenario.withListAllOccurrences().forEach(npc ->
                    addCombatantCard((NPC) npc, ACCENT_ALLY));
        }

        dynamicContainer.add(sectionLabel("Enemies", SECTION_ENEMY));
        scenario.againstListAllOccurrences().forEach(npc ->
                addCombatantCard((NPC) npc, ACCENT_ENEMY));

        dynamicContainer.revalidate();
        dynamicContainer.repaint();
    }

    // ── Party init ────────────────────────────────────────────────────────────

    private static JPanel boxPanel() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        return p;
    }

    // ── Scenario update ───────────────────────────────────────────────────────

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

    // ── Confirm ───────────────────────────────────────────────────────────────

    private void logAndBegin() {
        EncounterInfo.getFriendlies().clear();
        EncounterInfo.getEnemies().clear();

        activeCards.stream()
                .filter(CombatantCard::isPresent)
                .map(CombatantCard::getCombatant)
                .forEach(EncounterInfo::addCombatant);

        dispose();
        Main.finalizeCombat();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void initializeParty() {
        EncounterInfo.getParty().forEach(pc -> {
            CombatantCard card = new CombatantCard(pc, ACCENT_PARTY, true);
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

    public static void run() {
        new EncounterFinalizationPopup().setVisible(true);
    }

    // =========================================================================
    // CombatantCard
    // =========================================================================

    private static class CombatantCard extends JPanel {

        private static final int ACCENT_W = 3;
        private static final int CARD_H = 52;

        private final Combatant combatant;
        private final JSpinner spinner;
        private final JCheckBox absentCheck;
        private final JPanel accentBar;

        CombatantCard(Combatant combatant, Color accent, boolean showAbsent) {
            this.combatant = combatant;

            setLayout(new BorderLayout(10, 0));
            setBackground(BG_CARD);
            setOpaque(true);
            setMaximumSize(new Dimension(Integer.MAX_VALUE, CARD_H));
            setPreferredSize(new Dimension(0, CARD_H));
            setAlignmentX(LEFT_ALIGNMENT);
            setBorder(new EmptyBorder(0, 0, 0, 12));

            // Left accent strip
            accentBar = new JPanel();
            accentBar.setPreferredSize(new Dimension(ACCENT_W, 0));
            accentBar.setBackground(accent);
            accentBar.setOpaque(true);
            add(accentBar, BorderLayout.WEST);

            // Name
            JLabel nameLabel = new JLabel(combatant.name());
            nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD, 14f));
            nameLabel.setForeground(FG_PRIMARY);
            nameLabel.setBorder(new EmptyBorder(0, 10, 0, 0));
            add(nameLabel, BorderLayout.CENTER);

// Right: initiative + optional absent checkbox
            JPanel right = new JPanel(new GridBagLayout()); // GridBagLayout centers vertically by default
            right.setOpaque(false);
            add(right, BorderLayout.EAST);

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(0, 8, 0, 0); // Replaces the 8px hgap from FlowLayout
            gbc.anchor = GridBagConstraints.CENTER;

            JLabel initLabel = new JLabel("Initiative");
            initLabel.setFont(initLabel.getFont().deriveFont(Font.PLAIN, 11f));
            initLabel.setForeground(FG_MUTED);
            right.add(initLabel, gbc);

            SpinnerNumberModel model = new SpinnerNumberModel(10, 1, 20, 1);
            spinner = new JSpinner(model);
            spinner.setPreferredSize(new Dimension(60, 26));
            styleSpinner(spinner);
            right.add(spinner, gbc);

            if (showAbsent) {
                absentCheck = new JCheckBox("Absent");
                absentCheck.setFont(absentCheck.getFont().deriveFont(Font.PLAIN, 11f));
                absentCheck.setForeground(FG_MUTED);
                absentCheck.setBackground(BG_CARD);
                absentCheck.setOpaque(false);
                absentCheck.setFocusPainted(false);
                absentCheck.addActionListener(e -> updateAbsentState());
                right.add(absentCheck, gbc);
            } else {
                absentCheck = null;
            }
        }

        // ── Absent state ──────────────────────────────────────────────────────

        /**
         * Recursively dims all labels in the card.
         */
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
                de.getTextField().setBackground(BG_FIELD);
                de.getTextField().setForeground(FG_PRIMARY);
                de.getTextField().setCaretColor(FG_PRIMARY);
                de.getTextField().setBorder(new EmptyBorder(0, 2, 0, 2));
                de.getTextField().setHorizontalAlignment(SwingConstants.CENTER);
            }
        }

        // ── Spinner styling ───────────────────────────────────────────────────

        private void updateAbsentState() {
            boolean absent = absentCheck.isSelected();
            spinner.setEnabled(!absent);
            setBackground(absent ? BG_CARD_ABS : BG_CARD);
            accentBar.setBackground(absent
                    ? new Color(0x2A, 0x2E, 0x3A)
                    : accentBar.getBackground()); // keeps original accent if not absent

            // Restore correct accent when un-ticking — stored as client property
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