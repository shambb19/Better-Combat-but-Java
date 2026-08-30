package combat_menu.encounter_selection;

import combat_object.combatant.NPC;
import lombok.*;
import lombok.experimental.*;
import swing.custom.ValidatedField;
import swing.fluent.SwingComp;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.stream.Stream;

import static swing.ColorStyles.*;
import static swing.fluent.SwingComp.button;
import static swing.fluent.SwingPane.*;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@ExtensionMethod(util.StringUtil.class)
public class QuickCombatant extends JDialog {

    EncounterSelectionPanel root;

    ValidatedField nameField, hpField, acField;
    JToggleButton enemyToggle;
    JButton confirmButton;

    public QuickCombatant(EncounterSelectionPanel root) {
        this.root = root;

        setTitle("Quick Combatant");
        setLocationRelativeTo(root.getScenarioCombo());

        fluent(this).arrangedAs(BORDER, 0, 15).spaced().withPreferredSize(350, 180);

        nameField = new ValidatedField("Name", this::onValidation);

        enemyToggle = SwingComp.fluent(new JToggleButton("Friendly"))
                .withAction(t -> {
                    t.setText(t.isSelected() ? "Enemy" : "Friendly");
                    t.setBackground(t.isSelected() ? ENEMY : FRIENDLY);
                }).withPaddedBorder(new LineBorder(BACKGROUND, 4), 10, 10, 10, 10)
                .withBackground(FRIENDLY)
                .withText(Font.BOLD, 16f, FG_HINT)
                .component();

        hpField = new ValidatedField("Health", this::onValidation, 100);
        acField = new ValidatedField("Armor Class", this::onValidation, 40);

        panelIn(this, BorderLayout.CENTER).arrangedAs(TWO_COLUMN, 15, 15)
                .collect(nameField, enemyToggle, hpField, acField);

        confirmButton = button("Quick Add", SUCCESS, this::addQuickCombatant).in(this, BorderLayout.SOUTH);

        pack();
        setVisible(true);
    }

    void addQuickCombatant() {
        NPC quickAdd = NPC.builder()
                .name(nameField.getValue())
                .hp(hpField.getValue().toInt())
                .armorClass(acField.getValue().toInt())
                .isEnemy(enemyToggle.isSelected())
                .build();

        root.getQuickAdds().add(quickAdd);
        root.updateScenario();
        dispose();
    }

    void onValidation() {
        boolean isValid = Stream.of(nameField, hpField, acField).allMatch(ValidatedField::isValid);
        confirmButton.setEnabled(isValid);
    }

}
