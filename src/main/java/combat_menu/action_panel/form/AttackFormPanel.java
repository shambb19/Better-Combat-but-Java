package combat_menu.action_panel.form;

import __main.Main;
import __main.manager.CombatManager;
import __main.manager.EffectManager;
import _global_list.DamageImplements;
import combat_menu.encounter_info.HealthBarPanel;
import combat_object.damage_implements.Effect;
import combat_object.damage_implements.Implement;
import combat_object.damage_implements.Spell;
import combat_object.damage_implements.Weapon;
import format.ColorStyles;
import lombok.*;
import lombok.experimental.*;
import swing.custom_component.ValidatedField;
import util.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

import static combat_object.damage_implements.Effect.*;
import static swing.swing_comp.SwingComp.fluent;
import static swing.swing_comp.SwingComp.*;
import static swing.swing_comp.SwingPane.fluent;
import static swing.swing_comp.SwingPane.*;

@FieldDefaults(level = AccessLevel.PRIVATE)
@ExtensionMethod(StringUtils.class)
public class AttackFormPanel extends ActionFormPanel {

    static final Implement HEADER_WEAPON = new Weapon.ManualWeapon("── Weapons ──");
    static final Implement HEADER_SPELL = new Spell.ManualSpell("── Spells ──");

    static final Effect[] ATTACK_EFFECTS = new Effect[]{POISON, FRIGHTEN, BLIND, PRONE, RESTRAIN};

    JComboBox<Implement> attackCombo;
    ValidatedField rollField;
    JLabel rollFieldLabel;


    private AttackFormPanel() {
        super("Use Weapon");

        populateComboBox();
        Main.getCombatMenu().setActionMode(HealthBarPanel.ATTACK, this);
    }

    private void populateComboBox() {
        List<Weapon> weapons = attacker.getWeapons();
        List<Spell> spells = attacker.getSpells();

        attackCombo.addItem(HEADER_WEAPON);
        weapons.forEach(attackCombo::addItem);
        attackCombo.addItem(DamageImplements.MANUAL_WEAPON);

        attackCombo.addItem(HEADER_SPELL);
        spells.forEach(attackCombo::addItem);
        attackCombo.addItem(DamageImplements.MANUAL_HIT);
        attackCombo.addItem(DamageImplements.MANUAL_SAVE);
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
        for (Effect e : ATTACK_EFFECTS) {
            noticeConditions.put(e, EffectManager.hasEffect(attacker, e));
        }

        addNotices(container);
    }

    @Override
    protected void onConfirm() {
        Implement implement = Objects.requireNonNull((Implement) attackCombo.getSelectedItem());
        int roll = rollField.getValue().toInt();

        boolean continues = CombatManager.logAttack(target, roll, implement);

        if (!continues) {
            String reason;
            if (implement instanceof Spell s && s.hasSave())
                reason = "Roll " + target.getSaveThrow(roll, implement) + " beat " + attacker + "'s " + s.getStat() + " save";
            else
                reason = "Roll " + attacker.getAttackRoll(roll, implement) + " did not meet " + target + "'s AC";
            showMissResult(reason);
        }
    }

    private void showMissResult(String reason) {
        buttonRow.setVisible(false);

        JPanel banner = buildMissBanner(reason);
        banner.setAlignmentX(LEFT_ALIGNMENT);
        fieldsPanel.add(banner);

        long startTime = System.currentTimeMillis();
        int DURATION = 4000;

        Timer drain = new Timer(20, null);
        drain.addActionListener(e -> {
            if (System.currentTimeMillis() - startTime == DURATION) {
                drain.stop();
                CombatManager.finishAction();
            }
        });
        drain.start();
    }

    private JPanel buildMissBanner(String reason) {
        JPanel banner = newArrangedAs(BORDER, 10, 0)
                .withBackground(new Color(0x2A, 0x1E, 0x1E))
                .withMaximumSize(Integer.MAX_VALUE, 56)
                .withPaddedMatteBorderOnSide(new Color(0xe2, 0x4b, 0x4a), LEFT, 10, 12, 10, 10)
                .component();

        label("✕", Font.PLAIN, 15f, new Color(0xE2, 0x4B, 0x4A)).in(banner, BorderLayout.WEST);

        panelIn(banner, BorderLayout.CENTER).arrangedAs(VERTICAL_BOX)
                .collect(
                        label("Attack missed", Font.BOLD, 13f, new Color(0xE2, 0x4B, 0x4A)),
                        spacer(0, 2),
                        label(reason, Font.PLAIN, 11f, new Color(0x6B, 0x70, 0x80))
                ).transparent();

        return banner;
    }

    @Override
    protected boolean isInputValid() {
        Object selected = attackCombo.getSelectedItem();
        if (selected == null || selected instanceof String) return false;
        return rollField.isVisible() && rollField.isValid();
    }

    private JComboBox<Implement> addMixedCombo(JPanel container) {
        JComboBox<Implement> combo = fluent(new JComboBox<Implement>())
                .withText(Font.PLAIN, 13f, ColorStyles.TEXT_SECTION)
                .withBackground(ColorStyles.TRACK)
                .applied(b -> {
                    b.setRenderer(new MixedComboRenderer());
                    b.setModel(new MixedComboModel());
                }).component();

        fluent(container).collect(getAttackComboRow(combo), spacer(0, 10));
        return combo;
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
                fluent(this)
                        .withText(Font.PLAIN, 11f, new Color(0x50, 0x55, 0x68))
                        .withBackground(new Color(0x23, 0x26, 0x2e)).enabled(false);
                setText(value.toString());
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