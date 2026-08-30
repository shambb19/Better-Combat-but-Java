package combat_menu.action_panel.form;

import __main.Main;
import _global_list.DamageImplements;
import _manager.CombatManager;
import _manager.EffectManager;
import combat_menu.encounter_info.HealthBarPanel;
import combat_object.implement.*;
import config.ruleset.AttackResult;
import lombok.*;
import lombok.experimental.*;
import swing.custom.ValidatedField;
import util.Filterable;
import util.StringUtil;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Objects;

import static combat_object.implement.Effect.*;
import static config.ruleset.AttackResult.SUCCEEDED;
import static swing.ColorStyles.*;
import static swing.fluent.SwingComp.fluent;
import static swing.fluent.SwingComp.*;
import static swing.fluent.SwingPane.fluent;
import static swing.fluent.SwingPane.*;

@FieldDefaults(level = AccessLevel.PRIVATE)
@ExtensionMethod(StringUtil.class)
public class AttackFormPanel extends ActionFormPanel {

    static final Effect[] ATTACKER_EFFECTS = new Effect[]{POISON, ADVANTAGE_SOON};
    static final Effect[] TARGET_EFFECTS = new Effect[]{FRIGHTEN, BLIND, RESTRAIN};

    JComboBox<Implement> attackCombo;
    ValidatedField rollField;
    JLabel rollFieldLabel;

    public AttackFormPanel() {
        super("Use Weapon");

        populateComboBox();
        Main.getCombatMenu().setActionMode(HealthBarPanel.ATTACK, this);
    }

    private void populateComboBox() {
        for (var c : Main.getRuleset().getAllowedImplementClasses()) {
            Implement header = DamageImplements.createHeader(c);

            List<?> implementList = attacker.getImplements(c);

            if (c.isInstance(Gun.class))
                implementList = Filterable.of(implementList).castTo(Gun.class)
                        .filteredByAsList(g -> {
                            if (g.isHeavy()) return attacker.getHp() > 2;
                            return true;
                        });

            attackCombo.addItem(header);
            implementList.forEach(i -> attackCombo.addItem((Implement) i));
            DamageImplements.getManualEntries(c).forEach(attackCombo::addItem);
        }
    }

    @Override protected void buildFields() {
        attackCombo = addMixedCombo(fieldsPanel);

        LabeledField rollRow = addLabeledField(fieldsPanel, "Roll for hit", "Enter Roll");
        rollFieldLabel = rollRow.label();
        rollField = rollRow.field();
        rollField.setVisible(false);
        rollField.setValidator(s -> {
            int v = s.toInt();
            return v > 0 && v <= 20;
        });

        attackCombo.addActionListener(e -> onSelectionChanged());

        addNotices();
    }

    @Override protected void addNotices() {
        for (Effect e : ATTACKER_EFFECTS)
            noticeConditions.put(e, EffectManager.hasEffect(attacker, e));
        for (Effect e : TARGET_EFFECTS)
            noticeConditions.put(e, EffectManager.hasEffect(target, e));


        super.addNotices();
    }

    @Override protected void onConfirm() {
        Implement implement = Objects.requireNonNull((Implement) attackCombo.getSelectedItem());
        int roll = rollField.getValue().toInt();

        AttackResult continues = CombatManager.logAttack(target, roll, implement);

        if (continues != SUCCEEDED) showMissResult(continues.getReason(attacker, target));
    }

    @Override protected void onTargetChanged() {
        super.onTargetChanged();
        addNotices();
        onSelectionChanged();
    }

    @Override protected boolean isInputValid() {
        Object selected = attackCombo.getSelectedItem();
        if (selected == null || selected instanceof String) return false;
        return rollField.isVisible() && rollField.isValid();
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
            if (System.currentTimeMillis() - startTime >= DURATION) {
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
                .withPaddedMatteBorderOnSide(ENEMY, LEFT, 10, 12, 10, 10)
                .component();

        label("✕", Font.PLAIN, 15f, ENEMY).in(banner, BorderLayout.WEST);

        panelIn(banner, BorderLayout.CENTER).arrangedAs(VERTICAL_BOX)
                .collect(
                        label("Attack missed", Font.BOLD, 13f, ENEMY),
                        spacer(0, 2),
                        label(reason, 11f).muted()
                ).transparent();

        return banner;
    }

    private JComboBox<Implement> addMixedCombo(JPanel container) {
        JComboBox<Implement> combo = fluent(new JComboBox<Implement>())
                .withText(Font.PLAIN, 13f, FG_SECTION)
                .withBackground(TRACK)
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

        switch (selected) {
            case null -> {
                rollField.setVisible(false);
                rollField.clear();
                refreshButtons();
                return;
            }
            case Spell spell when spell.effectEquals(Effect.AUTO_HIT) -> {
                rollField.setVisible(false);
                confirmButton.setEnabled(true);
                confirmButton.setText("Cast Spell");
                addNotice(spell.getEffect(), fieldsPanel);
                return;
            }
            case Gun gun -> {
                boolean hasDisadvantage = (gun.isHeavy() && attacker.getHp() <= 4) || attacker.getHp() <= 2;
                if (hasDisadvantage) {
                    confirmButton.setText("Fire");
                    addNotice(DISADVANTAGE, fieldsPanel);
                }
            }
            default -> {
            }
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
        @Override public Component getListCellRendererComponent(JList<?> list, Object value,
                                                      int index, boolean selected, boolean focused) {
            super.getListCellRendererComponent(list, value, index, selected, focused);

            if (!(value instanceof Implement implement)) return this;

            boolean isHeader = implement.getName().startsWith("──");

            if (isHeader) {
                fluent(this)
                        .withText(Font.PLAIN, 11f, FG_HINT)
                        .withBackground(BG_SURFACE).enabled(false);
                setText(implement.toString());
            } else if (implement.isManual()) {
                setText(implement.getName());
                setForeground(FG_MUTED);
            } else if (implement instanceof Weapon w) {
                setText(w.getName());
                setForeground(FOREGROUND);
            } else if (implement instanceof Spell s) {
                String text = s.getName() + " [Save]".stringIfElseBlank(s.hasSave());
                setText(text);
                setForeground(SPELL);
            }
            return this;
        }
    }

    private static class MixedComboModel extends DefaultComboBoxModel<Implement> {
        @Override public void setSelectedItem(Object item) {
            // this is also disgusting, but I'm too lazy to implement something more robust, and works so ¯\_(ツ)_/¯
            if (item instanceof Implement s && s.getName().startsWith("──")) return;
            super.setSelectedItem(item);
        }
    }
}