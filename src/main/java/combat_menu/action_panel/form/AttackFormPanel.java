package combat_menu.action_panel.form;

import __main.Main;
import __main.manager.CombatManager;
import __main.manager.EncounterManager;
import character_info.combatant.Combatant;
import combat_menu.CombatantPanel;
import combat_menu.action_panel.LabeledField;
import damage_implements.Effect;
import damage_implements.Implement;
import damage_implements.Spell;
import damage_implements.Weapon;
import format.ColorStyles;
import util.Locators;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class AttackFormPanel extends ActionFormPanel {

    private static final Implement HEADER_WEAPON = new Weapon("── Weapons ──", 0, 0, null);
    private static final Implement HEADER_SPELL = new Spell("── Spells ──", 0, 0, null, Effect.NONE);

    private JComboBox<Implement> attackCombo;
    private ValidatedField rollField;
    private JLabel rollFieldLabel;

    private AttackFormPanel() {
        super("Use Weapon");
        populateComboBox();
        Main.getMenu().setActionMode(CombatantPanel.ATTACK);
        setTargetValidator(c -> Locators.getTargetList(true).contains(c));
    }

    public static AttackFormPanel newInstance() {
        return new AttackFormPanel();
    }

    @Override
    protected void buildFields(JPanel container) {
        attackCombo = addMixedCombo(container);

        LabeledField rollRow = addLabeledField(container, "Roll for hit", "Enter roll");
        rollFieldLabel = rollRow.label();
        rollField = rollRow.field();

        rollField.setVisible(false);

        attackCombo.addActionListener(e -> onSelectionChanged());

        rollField.setValidator(s -> {
            try {
                int v = Integer.parseInt(s);
                return v > 0 && v <= 20;
            } catch (NumberFormatException ex) {
                return false;
            }
        });
    }

    @Override
    protected void onConfirm() {
        Combatant attacker = EncounterManager.getCurrentCombatant();
        Implement implement = (Implement) attackCombo.getSelectedItem();
        int roll = Integer.parseInt(rollField.getValue());

        boolean hit = CombatManager.logAttack(target, roll, implement);

        if (!hit) {
            String reason;
            assert implement != null;
            reason = switch (implement) {
                case Weapon w ->
                        "Roll " + (roll + attacker.attackBonus(w)) + " did not meet " + target.name() + "'s AC";
                case Spell s when s.hasSave() ->
                        "Roll " + (roll + target.mod(s.stat())) + " beat " + target.name() + "'s " + s.stat() + " save";
                case Spell s when !s.hasSave() ->
                        "Roll " + (roll + attacker.mod(s.stat())) + " did not meet " + target.name() + "'s AC";
                default -> throw new ClassCastException("onConfirm in AttackFormPanel: implement not Weapon or Spell");
            };

            showMissResult(reason);
            return;
        }

        clearTarget();
        rollField.clear();
    }

    private void showMissResult(String reason) {
        btnRow.setVisible(false);

        JPanel banner = buildMissBanner(reason);
        banner.setAlignmentX(LEFT_ALIGNMENT);
        fieldsPanel.add(banner);
        fieldsPanel.revalidate();
        fieldsPanel.repaint();

        Timer drain = new Timer(20, null);
        long[] start = {System.currentTimeMillis()};
        int DURATION = 4000;

        drain.addActionListener(e -> {
            float progress = (float) (System.currentTimeMillis() - start[0]) / DURATION;

            if (progress >= 1f) {
                drain.stop();
                fieldsPanel.remove(banner);
                btnRow.setVisible(true);
                clearTarget();
                rollField.clear();
                fieldsPanel.revalidate();
                fieldsPanel.repaint();
            }
        });
        drain.start();
    }

    private JPanel buildMissBanner(String reason) {
        JPanel banner = new JPanel(new BorderLayout(10, 0));
        banner.setBackground(new Color(0x2A, 0x1E, 0x1E));
        banner.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 3, 0, 0, new Color(0xE2, 0x4B, 0x4A)),
                new EmptyBorder(10, 12, 10, 10)));
        banner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));

        // Icon
        JLabel icon = new JLabel("✕");
        icon.setFont(icon.getFont().deriveFont(Font.PLAIN, 15f));
        icon.setForeground(new Color(0xE2, 0x4B, 0x4A));
        banner.add(icon, BorderLayout.WEST);

        // Text
        JPanel text = new JPanel();
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.setOpaque(false);

        JLabel title = new JLabel("Attack missed");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 13f));
        title.setForeground(new Color(0xE2, 0x4B, 0x4A));

        JLabel sub = new JLabel(reason);
        sub.setFont(sub.getFont().deriveFont(Font.PLAIN, 11f));
        sub.setForeground(new Color(0x6B, 0x70, 0x80));

        text.add(title);
        text.add(Box.createRigidArea(new Dimension(0, 2)));
        text.add(sub);
        banner.add(text, BorderLayout.CENTER);

        return banner;
    }

    @Override
    protected boolean isInputValid() {
        Object selected = attackCombo.getSelectedItem();
        if (selected == null || selected instanceof String) return false;
        return rollField.isVisible() && rollField.isValid();
    }

    private JComboBox<Implement> addMixedCombo(JPanel container) {
        JComboBox<Implement> combo = new JComboBox<>();
        combo.setBackground(ColorStyles.TRACK);
        combo.setForeground(ColorStyles.SECTION_FG);
        combo.setFont(combo.getFont().deriveFont(Font.PLAIN, 13f));
        combo.setRenderer(new MixedComboRenderer());
        combo.setModel(new MixedComboModel());

        JPanel row = attackComboRow(combo);
        container.add(row);
        container.add(vgap(10));
        return combo;
    }

    private void populateComboBox() {
        Combatant currentCombatant = EncounterManager.getCurrentCombatant();

        List<Weapon> weapons = currentCombatant.weapons();
        List<Spell> spells = currentCombatant.spells();

        if (!weapons.isEmpty()) {
            attackCombo.addItem(HEADER_WEAPON);
            weapons.forEach(attackCombo::addItem);
        }
        if (!spells.isEmpty()) {
            attackCombo.addItem(HEADER_SPELL);
            spells.forEach(attackCombo::addItem);
        }
    }

    private void onSelectionChanged() {
        Object selected = attackCombo.getSelectedItem();

        if (selected == null || selected instanceof String) {
            rollField.setVisible(false);
            rollField.clear();
            refreshButtons();
            return;
        }

        if (selected instanceof Spell spell) {
            if (spell.hasSave()) {
                rollFieldLabel.setText("Target save roll");
                rollField.setValidator(s -> {
                    try {
                        int v = Integer.parseInt(s);
                        return v >= 1 && v <= 20;
                    } catch (NumberFormatException ex) {
                        return false;
                    }
                });
            } else {
                rollFieldLabel.setText("Roll for hit");
                rollField.setValidator(s -> {
                    try {
                        int v = Integer.parseInt(s);
                        return v > 0 && v <= 20;
                    } catch (NumberFormatException ex) {
                        return false;
                    }
                });
            }
        } else {
            rollFieldLabel.setText("Roll for hit");
            rollField.setValidator(s -> {
                try {
                    int v = Integer.parseInt(s);
                    return v > 0 && v <= 20;
                } catch (NumberFormatException ex) {
                    return false;
                }
            });
        }

        rollField.setVisible(true);
        rollField.clear();
        refreshButtons();

        String btnLabel = (selected instanceof Spell) ? "Cast Spell" : "Use Weapon";
        confirmButton.setText(btnLabel);
    }

    private static class MixedComboRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                                                      int index, boolean selected, boolean focused) {
            super.getListCellRendererComponent(list, value, index, selected, focused);

            boolean isHeader = value instanceof Implement
                    && (value.equals(HEADER_WEAPON) || value.equals(HEADER_SPELL));

            if (isHeader) {
                setText(value.toString());
                setForeground(new Color(0x50, 0x55, 0x68));
                setFont(getFont().deriveFont(Font.PLAIN, 11f));
                setBackground(new Color(0x23, 0x26, 0x2E));
                setEnabled(false);
            } else if (value instanceof Weapon w) {
                setText(w.name());
                setForeground(new Color(0xD8, 0xDC, 0xE8));
            } else if (value instanceof Spell s) {
                setText(s.name() + (s.hasSave() ? "  [save]" : ""));
                setForeground(new Color(0xAF, 0xA9, 0xEC)); // purple tint for spells
            }
            return this;
        }
    }

    private static class MixedComboModel extends DefaultComboBoxModel<Implement> {
        @Override
        public void setSelectedItem(Object item) {
            if (item instanceof Implement s &&
                    (s.equals(HEADER_WEAPON) || s.equals(HEADER_SPELL))) return;
            super.setSelectedItem(item);
        }
    }
}