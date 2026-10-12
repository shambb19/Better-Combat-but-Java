package combat_menu.encounter_selection;

import _global_list.Combatants;
import combat_object.combatant.NPC;
import lombok.*;
import lombok.experimental.*;
import swing.custom.ValidatedField;
import swing.builder.ButtonBuilder;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static swing.ColorStyles.*;
import static swing.builder.PanelBuilder.*;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@ExtensionMethod(util.StringUtil.class)
public class QuickCombatantPanel extends JPanel {

    boolean isEnemy;
    ValidatedField nameField, hpField, acField;
    JButton confirmButton;

    public QuickCombatantPanel(Consumer<NPC> onConfirm, boolean isEnemy) {
        this.isEnemy = isEnemy;

        modifyBasicPanel(this, BORDER, 0, 15, SPACER_BORDER);
        setPreferredSize(new Dimension(350, 180));

        nameField = new ValidatedField("Name", this::validateInputs);

        hpField = new ValidatedField("Health", this::validateInputs, 1000);
        acField = new ValidatedField("Armor Class", this::validateInputs, 40);

        createPanel(VERTICAL_BOX, nameField, hpField, acField
        ).hgap(15).vgap(15).build().putIn(this, BorderLayout.CENTER);

        confirmButton = ButtonBuilder.createButton("Quick Add", SUCCESS, () -> onConfirm.accept(buildNpc()))
                .build().putIn(this, BorderLayout.SOUTH);

        setVisible(true);
    }

    private NPC buildNpc() {
        return NPC.builder()
                .name(nameField.getValue())
                .hp(hpField.getValue().toInt())
                .armorClass(acField.getValue().toInt())
                .isEnemy(isEnemy)
                .build();
    }

    void validateInputs() {
        boolean inputValid = Stream.of(nameField, hpField, acField).allMatch(ValidatedField::isInputValid);
        boolean nameTaken = Combatants.toList().stream().anyMatch(c -> c.getName().equals(nameField.getValue()));

        String buttonText = "Quick Add";
        if (!inputValid) buttonText = "Invalid Input(s)";
        if (nameTaken) buttonText = "Name Taken";

        confirmButton.setEnabled(inputValid && !nameTaken);
        confirmButton.setText(buttonText);
    }

}