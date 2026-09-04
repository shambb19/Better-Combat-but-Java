package combat_menu.encounter_selection;

import _global_list.Combatants;
import combat_object.combatant.Combatant;
import combat_object.combatant.NPC;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.ExtensionMethod;
import lombok.experimental.FieldDefaults;
import swing.custom.ValidatedField;
import swing.fluent.SwingComp;
import swing.fluent.SwingPane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import static swing.ColorStyles.*;
import static swing.ColorStyles.BG_SURFACE;
import static swing.ColorStyles.TRACK;
import static swing.fluent.SwingComp.label;
import static swing.fluent.SwingPane.*;

@FieldDefaults(makeFinal = true, level = AccessLevel.PACKAGE)
@ExtensionMethod(util.StringUtil.class)
public class CombatantCard extends JPanel {

    @Getter Combatant combatant;
    JLabel inputLabel;
    ValidatedField input;
    @Getter JCheckBox checkBox;
    JPanel accentBar;
    JLabel label;
    @Getter boolean isEmpty;
    CombatantScroller host;

    CombatantCard( // default constructor
            CombatantScroller host, Combatant combatant, String inputType, boolean showAbsent, Runnable validator
    ) {
        this.host = host;
        this.combatant = combatant;
        label = null;
        isEmpty = false;

        SwingPane.fluent(this).arrangedAs(SwingPane.BORDER, 10, 0)
                .withBackground(BG_SURFACE)
                .withMaximumSize(Integer.MAX_VALUE, 52)
                .withPreferredSize(0, 52)
                .onLeft()
                .withEmptyBorder(0, 0, 0, 12);

        accentBar = panelIn(this, BorderLayout.WEST)
                .withPreferredSize(3, 0)
                .withBackground(combatant.getCombatantColor())
                .component();

        label(combatant, Font.BOLD, 14f).withEmptyBorder(0, 10, 0, 0)
                .in(this, BorderLayout.CENTER);

        JPanel right = new JPanel(new GridBagLayout());
        right.setOpaque(false);
        add(right, BorderLayout.EAST);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 8, 0, 0);
        gbc.anchor = GridBagConstraints.CENTER;

        inputLabel = label(inputType, 11f).muted().component();
        right.add(inputLabel, gbc);

        input = new ValidatedField("0", validator, 30);
        fluent(input).withBackground(TRACK).opaque();
        right.add(input, gbc);

        checkBox = SwingComp.fluent(new JCheckBox("Absent"))
                .withAction(b -> updateAbsentState())
                .withDerivedFont(Font.PLAIN, 11f)
                .withBackgroundAndForeground(BG_SURFACE, FG_MUTED)
                .transparent().component();
        if (showAbsent)
            right.add(checkBox, gbc);
    }

    private CombatantCard( // constructor for add new model
            CombatantScroller host, CombatantScroller.CombatantOption option
    ) {
        this.host = host;
        this.combatant = null;
        isEmpty = true;

        SwingPane.fluent(this).arrangedAs(SwingPane.BORDER, 10, 0)
                .withBackground(BG_ABSENT)
                .withMaximumSize(Integer.MAX_VALUE, 52)
                .withPreferredSize(0, 52)
                .onLeft()
                .withEmptyBorder(0, 0, 0, 12);

        accentBar = panelIn(this, BorderLayout.WEST)
                .withPreferredSize(3, 0)
                .withBackground(TRACK)
                .component();

        label = label("Add New " + option.text, Font.PLAIN, 14f).muted()
                .withEmptyBorder(0, 10, 0, 0)
                .in(this, BorderLayout.CENTER);

        inputLabel = label("", 11f).muted().component();
        inputLabel.setVisible(false);

        input = new ValidatedField("0", null, 30);
        input.setVisible(false);

        checkBox = SwingComp.fluent(new JCheckBox("Absent"))
                .transparent().applied(b -> b.setVisible(false)).component();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                accentBar.setBackground(TRACK);
                fluent(label).withForeground(FOREGROUND).withDerivedFont(Font.PLAIN, 14f);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                accentBar.setBackground(SUCCESS);
                label.setForeground(SUCCESS);
                fluent(label).withForeground(SUCCESS).withDerivedFont(Font.BOLD, 14f);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (host.isForQuickAdd()) {
                    if (!(host.getHost() instanceof QuickAddDialog dialog))
                        throw new IllegalArgumentException(
                                "CombatantCard.MouseAdapter.mousePressed: QuickAddDialog expected"
                        );
                    dialog.showCard(QuickAddDialog.CREATE);
                } else {
                    boolean forEnemies = option == CombatantScroller.CombatantOption.ENEMIES;
                    List<NPC> available = forEnemies ? Combatants.getEnemyNpcs() : Combatants.getFriendlyNpcs();
                    new QuickAddDialog(forEnemies, available, host::addCardBulk).setVisible(true);
                }
            }
        });
    }

    private CombatantCard( // constructor for scenario editing
            CombatantScroller host, Combatant combatant, String inputType, Runnable validator
    ) {
        this.host = host;
        this.combatant = combatant;
        label = null;
        isEmpty = false;

        SwingPane.fluent(this).arrangedAs(SwingPane.BORDER, 10, 0)
                .withBackground(BG_ABSENT)
                .withMaximumSize(Integer.MAX_VALUE, 52)
                .withPreferredSize(0, 52)
                .onLeft()
                .withEmptyBorder(0, 0, 0, 12);

        accentBar = panelIn(this, BorderLayout.WEST)
                .withPreferredSize(3, 0)
                .withBackground(TRACK)
                .component();

        label(combatant, Font.BOLD, 14f).withEmptyBorder(0, 10, 0, 0)
                .in(this, BorderLayout.CENTER);

        JPanel right = new JPanel(new GridBagLayout());
        right.setOpaque(false);
        add(right, BorderLayout.EAST);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 8, 0, 0);
        gbc.anchor = GridBagConstraints.CENTER;

        inputLabel = label(inputType, 11f).muted().component();
        inputLabel.setVisible(false);
        right.add(inputLabel, gbc);

        input = new ValidatedField("1", validator, 30);
        fluent(input).withBackground(TRACK).opaque();
        input.setVisible(false);
        right.add(input, gbc);

        checkBox = SwingComp.fluent(new JCheckBox("Include"))
                .withAction(b -> updateIncludeState())
                .applied(b -> b.addChangeListener(e -> updateIncludeState()))
                .withDerivedFont(Font.PLAIN, 11f)
                .withBackgroundAndForeground(BG_ABSENT, FG_MUTED)
                .transparent().component();
        right.add(checkBox, gbc);

        setForegroundAlpha(this, 0.5f);
    }

    private void updateIncludeState() {
        boolean included = checkBox.isSelected();
        input.setVisible(included);
        inputLabel.setVisible(included);
        input.setEnabled(included);
        setBackground(included ? BG_SURFACE : BG_ABSENT);
        checkBox.setBackground(included ? BG_SURFACE : BG_ABSENT);
        accentBar.setBackground(included ? combatant.getCombatantColor() : TRACK);

        float alpha = included ? 1.0f : 0.5f;
        setForegroundAlpha(this, alpha);

        revalidate();
        repaint();
    }

    private static void setForegroundAlpha(Container container, float alpha) {
        for (Component child : container.getComponents()) {
            if (child instanceof JLabel l) {
                Color fg = l.getForeground();
                l.setForeground(new Color(
                        fg.getRed(), fg.getGreen(), fg.getBlue(),
                        Math.round(255 * alpha)));
            }
            if (child instanceof Container c) setForegroundAlpha(c, alpha);
        }
    }

    private void updateAbsentState() {
        boolean absent = checkBox.isSelected();
        input.setEnabled(!absent);
        setBackground(absent ? BG_ABSENT : BG_SURFACE);
        checkBox.setBackground(absent ? BG_ABSENT : BG_SURFACE);
        accentBar.setBackground(absent ? TRACK : combatant.getCombatantColor());

        float alpha = absent ? 0.5f : 1.0f;
        setForegroundAlpha(this, alpha);
        repaint();
    }

    public int getInputValue() {
        return input.getValue().toInt();
    }

    public boolean hasValidInput() {
        if (!input.isVisible()) return true;
        return input.isValid();
    }

    public CombatantCard withUpdatedVisibility(boolean noInput, boolean noAbsentCheck) {
        input.setVisible(!noInput);
        inputLabel.setVisible(!noInput);
        checkBox.setVisible(!noAbsentCheck);
        return this;
    }

    public static CombatantCard promptCard(CombatantScroller host, CombatantScroller.CombatantOption option) {
        return new CombatantCard(host, option);
    }

    public static CombatantCard quickAddCard(
            CombatantScroller host, Combatant combatant, String inputType, Runnable validator
    ) {
        return new CombatantCard(host, combatant, inputType, validator);
    }

}
