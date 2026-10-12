package combat_menu.action_panel.form;

import __main.Main;
import _manager.CombatManager;
import _manager.EffectManager;
import combat_menu.encounter_info.HealthBarPanel;
import combat_object.combatant.Combatant;
import combat_object.implement.*;
import config.Config;
import config.ruleset.AttackResult;
import lombok.*;
import lombok.experimental.*;
import swing.custom.ValidatedField;
import swing.builder.ComponentBuilder;
import util.StringUtil;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static combat_object.implement.Effect.*;
import static config.ruleset.AttackResult.SUCCEEDED;
import static swing.ColorStyles.*;
import static swing.builder.Components.*;
import static swing.builder.PanelBuilder.*;

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

        Main.getCombatMenu().setActionMode(HealthBarPanel.ATTACK, this);
    }

    @Override protected void buildFields() {
        JLabel attackLabel = ComponentBuilder.createLabel("Select an attack", Font.PLAIN, 12f, FG_MUTED)
                .preferredSize(new Dimension(110, 0)).build().get();

        attackCombo = ComponentBuilder.createComboBox(getImplements(), b -> onSelectionChanged(), null)
                .fontBuilder(FontBuilder.of(Font.PLAIN, 13f, FG_SECTION))
                .background(TRACK).build().get();
        attackCombo.setRenderer(new MixedComboRenderer());
        attackCombo.setModel(new MixedComboModel());

        JPanel attackPanel = createPanel(BORDER).hgap(12)
                .hostedComponent(attackLabel, BorderLayout.WEST)
                .hostedComponent(attackCombo, BorderLayout.CENTER)
                .alignmentX(LEFT_ALIGNMENT)
                .preferredSize(new Dimension(Integer.MAX_VALUE, 52)).build().get();

        addConstraintlessComponentsTo(fieldsPanel, attackPanel, spacer(0, 10));

        LabeledField rollRow = addLabeledField(fieldsPanel, "Roll for hit", "Enter Roll");
        rollFieldLabel = rollRow.label();
        rollField = rollRow.field();
        rollField.setVisible(false);
        rollField.setValidator(s -> {
            int v = s.toInt();
            return v > 0 && v <= 20;
        });

        addNotices();
    }

    private Implement[] getImplements() {
        List<Function<Class<? extends Implement>, List<? extends Implement>>> getters =
                List.of(Implement::createHeader, attacker::getImplements, Implement::getManualEntries);

        return Config.getRuleset().getAllowedImplementClasses().stream()
                .flatMap(c -> getters.stream().flatMap(g -> g.apply(c).stream()))
                .toArray(Implement[]::new);
    }

    @Override protected void addNotices() {
        BiConsumer<Effect[], Combatant> putter = (effectOn, on) -> {
            for (Effect e : effectOn) {
                boolean effectValidHere = EffectManager.hasEffect(on, e);
                if (effectValidHere) noticeConditions.put(e, on);
            }
        };
        putter.accept(ATTACKER_EFFECTS, attacker);
        putter.accept(TARGET_EFFECTS, target);

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
        return rollField.isVisible() && rollField.isInputValid();
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
        JPanel banner = createPanel(BORDER).hgap(10)
                .background(new Color(0x2a, 0x1e, 0x1e))
                .fixedHeight(56)
                .border(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 6, 0, 0, ENEMY),
                        BorderFactory.createEmptyBorder(10, 12, 10, 0)
                )).build().get();

        ComponentBuilder.createLabel("✕", Font.PLAIN, 15f, ENEMY)
                .build().putIn(banner, BorderLayout.WEST);

        JLabel missLabel = ComponentBuilder.getBasicLabel("Attack missed", Font.BOLD, 13f, ENEMY);

        JLabel reasonLabel = ComponentBuilder.getBasicLabel(reason, Font.PLAIN, 11f, FG_MUTED);

        createPanel(VERTICAL_BOX, missLabel, spacer(0, 2), reasonLabel)
                .build().putIn(banner, BorderLayout.CENTER);

        return banner;
    }

    private void onSelectionChanged() {
        if (attackCombo == null) return;

        Object selected = attackCombo.getSelectedItem();

        addNotices();

        if (selected instanceof Spell spell && spell.effectEquals(AUTO_HIT)) {
            rollField.setVisible(false);
            confirmButton.setEnabled(true);
            confirmButton.setText("Cast Spell");
            addNotice(spell.getEffect(), fieldsPanel);
        } else if (selected instanceof Gun gun) {
            boolean hasDisadvantage = (gun.isHeavy() && attacker.getHp() <= 4) || attacker.getHp() <= 2;
            if (hasDisadvantage) {
                confirmButton.setText("Fire");
                addNotice(DISADVANTAGE, fieldsPanel);
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
        @Override public Component getListCellRendererComponent(
                JList<?> list, Object value, int index, boolean selected, boolean focused
        ) {
            super.getListCellRendererComponent(list, value, index, selected, focused);

            if (!(value instanceof Implement implement)) return this;

            boolean isHeader = implement.getName().startsWith("──");

            if (isHeader) {
                modifying(this)
                        .fontBuilder(FontBuilder.of(Font.PLAIN, 11f))
                        .background(BG_SURFACE).foreground(FG_HINT)
                        .enabled(false).build().validated();
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
            // this is also disgusting, but I'm too lazy to think of something better, and it works so ¯\_(ツ)_/¯
            if (item instanceof Implement s && s.getName().startsWith("──")) return;
            super.setSelectedItem(item);
        }
    }
}