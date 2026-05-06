package popup;

import combat_object.implement.Gun;
import lombok.experimental.*;
import swing.custom.Popup;
import swing.custom.ValidatedField;

import javax.swing.*;

import static swing.ColorStyles.*;
import static swing.fluent.SwingComp.fluent;
import static swing.fluent.SwingComp.*;
import static swing.fluent.SwingPane.fluent;
import static swing.fluent.SwingPane.*;

@ExtensionMethod(util.StringUtil.class)
public class GunAttackPopup extends Popup {

    private boolean hits;

    private final int roll;
    private final Gun gun;
    private boolean isLongRange;

    private JCheckBox coverCheck;
    private ValidatedField coverInputField;
    private JButton confirmButton;

    private GunAttackPopup(int roll, Gun gun) {
        this.roll = roll;
        this.gun = gun;

        setTitle("Finalize Shot");
        setModalityType(DEFAULT_MODALITY_TYPE);

        fluent(this).arrangedAs(ONE_COLUMN, 0, 15).collect(
                getRangeSelectionPanel(), getCoverPanel(), getConfirmButton()
        );

        pack();
    }

    private JPanel getRangeSelectionPanel() {
        JToggleButton rangeToggle = toggleButton("Short Range", SCENARIO, "Long Range", SPELL)
                .withAction(b -> isLongRange = b.isSelected())
                .component();

        return newArrangedAs(FLOW_RIGHT, 20, 0).collect(
                "Select Attack Range:", rangeToggle
        ).component();
    }

    private JPanel getCoverPanel() {
        coverInputField = new ValidatedField("Cover Save Roll",
                () -> confirmButton.setEnabled(coverInputField.isValid()), 12);

        JPanel coverInputPanel = newArrangedAs(FLOW_RIGHT, 20, 0).collect(
                        "Enter Cover Save Roll:", coverInputField
                ).applied(p -> p.setVisible(false))
                .component();

        coverCheck = fluent(new JCheckBox("Target Behind Cover?"))
                .withAction(c -> coverInputPanel.setVisible(c.isSelected()))
                .component();

        return newArrangedAs(ONE_COLUMN, 0, 10).collect(
                coverCheck, coverInputPanel
        ).component();
    }

    private JButton getConfirmButton() {
        confirmButton = button("Attack", SUCCESS, () -> {
            hits = calculateShotHits();
            dispose();
        }).component();

        return confirmButton;
    }

    private boolean calculateShotHits() {
        boolean hitExcludingCover = roll >= gun.getShortHitDc();
        if (isLongRange) hitExcludingCover = roll >= gun.getLongHitDc();

        if (!hitExcludingCover || !coverCheck.isSelected()) return hitExcludingCover;

        return coverInputField.getValue().toInt() < gun.getCoverDc();
    }

    public static boolean runAndReturnHit(int roll, Gun gun) {
        GunAttackPopup popup = new GunAttackPopup(roll, gun);
        popup.setVisible(true);
        return popup.hits;
    }

}