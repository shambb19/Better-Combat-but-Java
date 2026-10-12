package swing.popup;

import combat_object.implement.Gun;
import config.ruleset.AttackResult;
import lombok.*;
import lombok.experimental.*;
import swing.custom.Popup;
import swing.custom.ValidatedField;
import swing.builder.ButtonBuilder;
import swing.builder.ComponentBuilder;

import javax.swing.*;
import java.awt.*;

import static config.ruleset.AttackResult.*;
import static swing.ColorStyles.*;
import static swing.builder.Components.*;
import static swing.builder.PanelBuilder.*;

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

        modifyingPanel(this).layoutType(BORDER)
                .hostedComponent(getTopBar(), BorderLayout.NORTH)
                .hostedComponent(getBody(), BorderLayout.CENTER)
                .hostedComponent(getFooter(), BorderLayout.SOUTH)
                .border(BorderFactory.createLineBorder(TRACK, 1)).build().validated();

        pack();
    }

    private JPanel getTopBar() {
        JLabel finalize = ComponentBuilder.getBasicLabel("Finalize Shot", Font.BOLD, 11f, FG_MUTED);

        JLabel rollIndicator = ComponentBuilder.createLabel("Roll: " + roll, Font.PLAIN, 11f, FG_MUTED)
                .border(BorderFactory.createEmptyBorder(4, 8, 4, 8)).build().get();

        return createPanel(BORDER)
                .hostedComponent(finalize, BorderLayout.WEST)
                .hostedComponent(rollIndicator, BorderLayout.EAST)
                .border(BorderFactory.createMatteBorder(0, 0, 1, 0, TRACK)).build().get();
    }

    private JPanel getBody() {
        JPanel rangeRow = createPanel(FLOW_LEFT,
                ComponentBuilder.createLabel("Attack Range", Font.PLAIN, 13f, FG_MUTED),
                buildRangeToggle()
                ).hgap(12).build().get();

        JSeparator separator = modifying(new JSeparator(SwingConstants.HORIZONTAL))
                .foreground(TRACK)
                .fixedHeight(1).build().get();

        return createPanel(VERTICAL_BOX,
                rangeRow, spacer(0, 10),
                separator, spacer(0, 10),
                getCoverSection()
                ).border(SPACER_BORDER).build().get();
    }

    private JPanel getFooter() {
        confirmButton = ButtonBuilder.createButton("Attack", SUCCESS, () -> {
            hits = calculateShotHits();
            dispose();
        }).build().get();

        return createPanel(FLOW_RIGHT, confirmButton).hgap(5).vgap(5)
                .background(BG_DARK)
                .border(BorderFactory.createMatteBorder(1, 0, 0, 0, TRACK)).build().get();
    }

    private JPanel buildRangeToggle() {
        JButton shortButton = ButtonBuilder.createButton("Short", SCENARIO, (Runnable) null)
                .foreground(BG_DEEP)
                .preferredSize(new Dimension(64, 26)).build().get();

        JButton longButton = ButtonBuilder.createButton("Long", BG_DARK, (Runnable) null)
                .preferredSize(new Dimension(64, 26)).build().get();

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

        return createPanel(HORIZONTAL_BOX, shortButton, longButton).build().get();
    }

    private JPanel getCoverSection() {
        coverInputField = new ValidatedField("Cover save roll",
                () -> confirmButton.setEnabled(coverInputField.isInputValid()), 12);
        coverInputField.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel inputRow = createPanel(FLOW_LEFT,
                ComponentBuilder.createLabel("Cover Save Roll", Font.PLAIN, 13f, FG_MUTED)
                ).hgap(12)
                .visible(false).build().get();

        coverCheck = ButtonBuilder.createCheckBox("Target Behind Cover?", c -> {
            inputRow.setVisible(c.isSelected());
            if (!c.isSelected()) confirmButton.setEnabled(true);
            pack();
        }).fontBuilder(FontBuilder.of(Font.PLAIN, 13f)).build().get();

        return createPanel(VERTICAL_BOX, coverCheck, inputRow).vgap(8).build().get();
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