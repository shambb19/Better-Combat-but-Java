package combat_menu.action_panel.form;

import __main.Main;
import __main.manager.CombatManager;
import __main.manager.EffectManager;
import combat_menu.CombatantPanel;
import combat_object.damage_implements.Effect;
import combat_object.damage_implements.Implement;
import combat_object.damage_implements.Spell;
import combat_object.damage_implements.Weapon;
import format.ColorStyles;
import lombok.*;
import lombok.experimental.*;
import swing.ValidatedField;
import swing.swing_comp.SwingComp;
import swing.swing_comp.SwingPane;
import util.Locators;
import util.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@ExtensionMethod(StringUtils.class)
public class AttackFormPanel extends ActionFormPanel {

    static final Implement HEADER_WEAPON = new Weapon("── Weapons ──", 0, 0, null);
    static final Implement HEADER_SPELL = new Spell("── Spells ──", 0, 0, null, Effect.NONE);

    JComboBox<Implement> attackCombo;
    ValidatedField rollField;
    JLabel rollFieldLabel;


    private AttackFormPanel() {
        super("Use Weapon");
        populateComboBox();
        Main.getCombatMenu().setActionMode(CombatantPanel.ATTACK);
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
            int v = s.toInt();
            if (v == Integer.MIN_VALUE) return false;
            return v > 0 && v <= 20;
        });

        noticeConditions = new LinkedHashMap<>();

        noticeConditions.put(Effect.POISON, EffectManager.hasEffect(attacker, Effect.POISON));
        noticeConditions.put(Effect.FRIGHTEN, EffectManager.hasEffect(attacker, Effect.FRIGHTEN));
        noticeConditions.put(Effect.BLIND, EffectManager.hasEffect(target, Effect.BLIND));
        noticeConditions.put(Effect.PRONE, EffectManager.hasEffect(target, Effect.PRONE));
        noticeConditions.put(Effect.RESTRAIN, EffectManager.hasEffect(target, Effect.RESTRAIN));

        addNotices(noticeConditions, container);
    }

    @Override
    protected void onConfirm() {
        Implement implement = (Implement) attackCombo.getSelectedItem();
        int roll = rollField.getValue().toInt();

        boolean continues = CombatManager.logAttack(target, roll, implement);

        if (!continues) {
            String reason;
            assert implement != null;
            reason = switch (implement) {
                case Weapon w ->
                        "Roll " + (roll + attacker.attackBonus(w)) + " did not meet " + target.getName() + "'s AC";
                case Spell s when s.hasSave() ->
                        "Roll " + (roll + target.mod(s.getStat())) + " beat " + attacker.getName() + "'s " + s.getStat() + " save";
                case Spell s when !s.hasSave() ->
                        "Roll " + (roll + attacker.mod(s.getStat())) + " did not meet " + target.getName() + "'s AC";
                default -> throw new ClassCastException("onConfirm error: implement type unknown");
            };

            showMissResult(reason);
        } else {
            clearTarget();
            rollField.clear();
        }
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

                CombatManager.finishAction();

                fieldsPanel.revalidate();
                fieldsPanel.repaint();
            }
        });
        drain.start();
    }

    private JPanel buildMissBanner(String reason) {
        JPanel banner = SwingPane.panel().withLayout(SwingPane.BORDER)
                .withGaps(10, 0)
                .withBackground(new Color(0x2A, 0x1E, 0x1E))
                .withMaximumSize(Integer.MAX_VALUE, 56)
                .withPaddedMatteBorderOnSide(new Color(0xe2, 0x4b, 0x4a), SwingComp.LEFT, 10, 12, 10, 10)
                .component();

        SwingComp.label("✕")
                .withDerivedFont(Font.PLAIN, 15f)
                .withForeground(new Color(0xE2, 0x4B, 0x4A))
                .in(banner, BorderLayout.WEST);

        JPanel text = SwingPane.panelIn(banner, BorderLayout.CENTER).withLayout(SwingPane.VERTICAL_BOX).transparent().component();

        SwingComp.label("Attack missed")
                .withDerivedFont(Font.BOLD, 13f)
                .withForeground(new Color(0xE2, 0x4B, 0x4A))
                .in(text);

        SwingComp.gapIn(2, text);

        SwingComp.label(reason)
                .withDerivedFont(Font.PLAIN, 11f)
                .withForeground(new Color(0x6B, 0x70, 0x80))
                .in(text);

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
        List<Weapon> weapons = attacker.getWeapons();
        List<Spell> spells = attacker.getSpells();

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
        clearNotices(fieldsPanel);

        if (selected == null || selected instanceof String) {
            rollField.setVisible(false);
            rollField.clear();
            refreshButtons();
            return;
        }

        if (selected instanceof Spell spell && spell.effectEquals(Effect.AUTO_HIT)) {
            rollField.setVisible(false);
            confirmButton.setEnabled(true);
            confirmButton.setText("Cast Spell");
            addNotice(spell.getEffect(), fieldsPanel);
            return;
        }

        String rollFieldText;
        if (selected instanceof Spell spell && spell.hasSave())
            rollFieldText = "Target save roll";
        else
            rollFieldText = "Roll for hit";
        rollFieldLabel.setText(rollFieldText);

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
                setText(w.getName());
                setForeground(new Color(0xD8, 0xDC, 0xE8));
            } else if (value instanceof Spell s) {
                setText(s.getName() + (s.hasSave() ? "  [save]" : ""));
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