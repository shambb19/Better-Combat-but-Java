package popup;

import combat_object.implement.Gun;
import config.ruleset.AttackResult;
import lombok.*;
import lombok.experimental.*;
import swing.custom.Popup;
import swing.custom.ValidatedField;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;

import static config.ruleset.AttackResult.*;
import static swing.ColorStyles.*;
import static swing.fluent.SwingComp.fluent;
import static swing.fluent.SwingComp.*;
import static swing.fluent.SwingPane.fluent;
import static swing.fluent.SwingPane.*;

@ExtensionMethod(util.StringUtil.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GunAttackPopup extends Popup {

    final int roll;
    final Gun gun;
    AttackResult hits;
    boolean isLongRange;
    JCheckBox coverCheck;
    ValidatedField coverInputField;
    JButton confirmButton;

    private GunAttackPopup(int roll, Gun gun) {
        this.roll = roll;
        this.gun = gun;

        setModalityType(DEFAULT_MODALITY_TYPE);
        setUndecorated(true);
        setLocationRelativeTo(null);

        fluent(this).arrangedAs(BORDER).borderCollect(
                north(getTopBar()), center(getBody()), south(getFooter())
        ).withBorder(new LineBorder(TRACK, 1));

        pack();
    }

    private JPanel getTopBar() {
        JLabel finalize = label("Finalize Shot", Font.BOLD, 11f).muted().component();

        JLabel rollIndicator = label("Roll: " + roll, Font.PLAIN, 11f).muted()
                .withSidePaddedEmptyBorder(4).component();

        return newArrangedAs(BORDER).borderCollect(
                        west(finalize), east(rollIndicator)
                ).withBackground(BG_DARK)
                .withBorder(new MatteBorder(0, 0, 1, 0, TRACK))
                .component();
    }

    private JPanel getBody() {
        JPanel rangeRow = newArrangedAs(FLOW_LEFT, 12, 0).collect(
                label("Attack range", Font.PLAIN, 13f).muted().component(),
                buildRangeToggle()
        ).component();

        JSeparator separator = fluent(new JSeparator(SwingConstants.HORIZONTAL))
                .withForeground(TRACK)
                .withMaximumSize(Integer.MAX_VALUE, 1).component();

        return newArrangedAs(VERTICAL_BOX).collect(
                rangeRow, spacer(0, 10),
                separator, spacer(0, 10),
                getCoverSection()
        ).spaced().component();
    }

    private JPanel getFooter() {
        confirmButton = button("Attack", SUCCESS, () -> {
            hits = calculateShotHits();
            dispose();
        }).component();

        return newArrangedAs(FLOW_RIGHT, 5, 5).collect(confirmButton)
                .withBackground(BG_DARK)
                .withBorder(new MatteBorder(1, 0, 0, 0, TRACK))
                .component();
    }

    private JPanel buildRangeToggle() {
        JButton shortButton = button("Short", SCENARIO, (Runnable) null)
                .withForeground(BG_DEEP)
                .withPreferredSize(64, 26)
                .component();
        JButton longButton = button("Long", BG_DARK, (Runnable) null)
                .withPreferredSize(64, 26)
                .component();

        shortButton.addActionListener(e -> {
            isLongRange = false;
            shortButton.setBackground(SCENARIO);
            shortButton.setForeground(BG_DEEP);
            longButton.setBackground(BACKGROUND);
            longButton.setForeground(FOREGROUND);
        });
        longButton.addActionListener(e -> {
            isLongRange = true;
            longButton.setBackground(SCENARIO);
            longButton.setForeground(BG_DEEP);
            shortButton.setBackground(BACKGROUND);
            shortButton.setForeground(FOREGROUND);
        });

        return newArrangedAs(HORIZONTAL_BOX).collect(
                shortButton, longButton
        ).component();
    }

    private JPanel getCoverSection() {
        coverInputField = new ValidatedField("Cover save roll",
                () -> confirmButton.setEnabled(coverInputField.isValid()), 12);
        coverInputField.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel inputRow = newArrangedAs(FLOW_LEFT, 12, 0).collect(
                label("Cover save roll", Font.PLAIN, 13f).muted().component(),
                coverInputField
        ).applied(p -> p.setVisible(false)).component();

        coverCheck = fluent(new JCheckBox("Target behind cover?"))
                .withDerivedFont(Font.PLAIN, 13f)
                .withAction(c -> {
                    inputRow.setVisible(c.isSelected());
                    if (!c.isSelected()) confirmButton.setEnabled(true);
                    pack();
                }).transparent().component();

        return newArrangedAs(VERTICAL_BOX, 0, 8).collect(
                coverCheck, inputRow
        ).component();
    }

    private AttackResult calculateShotHits() {
        int hitDc = isLongRange ? gun.getLongHitDc() : gun.getShortHitDc();

        if (roll < hitDc) return SHOT_MISSED;
        if (!coverCheck.isSelected()) return SUCCEEDED;

        if (coverInputField.getValue().toInt() < gun.getCoverDc()) return SUCCEEDED;
        return COVER_SAVE_SUCCESSFUL;
    }

    public static AttackResult runAndReturnHit(int roll, Gun gun) {
        GunAttackPopup popup = new GunAttackPopup(roll, gun);
        popup.setVisible(true);
        return popup.hits;
    }
}