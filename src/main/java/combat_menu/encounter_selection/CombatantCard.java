package combat_menu.encounter_selection;

import _global_list.Combatants;
import combat_object.combatant.Combatant;
import combat_object.combatant.NPC;
import combat_object.combatant.CombatantOption;
import lombok.AccessLevel;
import lombok.experimental.ExtensionMethod;
import lombok.experimental.FieldDefaults;
import swing.custom.ValidatedField;
import swing.builder.ButtonBuilder;
import swing.builder.ComponentBuilder;
import util.Filterable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Set;

import static swing.ColorStyles.*;
import static swing.builder.PanelBuilder.*;

@FieldDefaults(makeFinal = true, level = AccessLevel.PACKAGE)
@ExtensionMethod(util.StringUtil.class)
public class CombatantCard extends JPanel {

    Combatant combatant;
    JLabel inputLabel;
    ValidatedField input;
    boolean showInput;
    JCheckBox checkBox;
    JPanel accentBar;
    JLabel label;
    boolean isEmpty;
    CombatantScroller host;

    private CombatantCard( // default constructor
                           CombatantScroller host, Combatant combatant,
                           String inputType, boolean showInput,
                           boolean absentCheck, // else include check
                           Runnable validator
    ) {
        this.host = host;
        this.combatant = combatant;
        this.showInput = showInput;
        label = null;
        isEmpty = false;

        modifyBasicPanel(this, BORDER, 10, 0, BorderFactory.createEmptyBorder(0, 0, 0, 12));
        modifying(this).background(BG_SURFACE)
                .fixedHeight(52)
                .alignmentX(LEFT_ALIGNMENT).build().validated();

        //
        // accent bar
        Color accent = absentCheck ? combatant.getCombatantColor() : TRACK;

        accentBar = createPanel(FLOW)
                .preferredSize(new Dimension(3, 0))
                .background(accent)
                .build().putIn(this, BorderLayout.WEST);

        //
        // label
        ComponentBuilder.createLabel(combatant.toString(), Font.BOLD, 14f, FOREGROUND)
                .border(BorderFactory.createEmptyBorder(0, 10, 0, 0))
                .build().putIn(this, BorderLayout.CENTER);

        //
        // input
        JPanel right = createPanel(GRID_BAG).build().putIn(this, BorderLayout.EAST);
        GridBagConstraints gbc = createConstraints(GridBagConstraints.CENTER, 0, 8, 0, 0);

        inputLabel = ComponentBuilder.createLabel(inputType, Font.PLAIN, 11f, FG_MUTED)
                .visible(showInput).build().putIn(right, gbc);

        input = new ValidatedField("1", validator, 30);
        input.allowEnter(() -> host.focusNext(this));
        input.setVisible(showInput);
        right.add(input, gbc);

        Runnable action = absentCheck
                ? this::updateAbsentState
                : this::updateIncludeState;

        String checkBoxText = absentCheck ? "Absent?" : "Include?";
        checkBox = ButtonBuilder.createCheckBox(checkBoxText, b -> action.run())
                .fontBuilder(FontBuilder.of(Font.PLAIN, 11f, FG_MUTED))
                .background(getBackground())
                .build().putIn(right, gbc);

        setForegroundAlpha(this, 0.5f);
    }

    private CombatantCard( // constructor for add new model
                           CombatantScroller host, CombatantOption option
    ) {
        this.host = host;
        this.combatant = null;
        showInput = false;
        isEmpty = true;

        modifyingPanel(this).layoutType(BORDER).hgap(10)
                .border(BorderFactory.createEmptyBorder(0, 0, 0, 12))
                .background(BG_SURFACE)
                .fixedHeight(52)
                .alignmentX(LEFT_ALIGNMENT).build().validated();

        accentBar = createPanel(FLOW)
                .preferredSize(new Dimension(3, 0))
                .background(TRACK)
                .build().putIn(this, BorderLayout.WEST);

        String labelText = "Add New " + option.getExpandedName();
        label = ComponentBuilder.createLabel(labelText, Font.PLAIN, 14f, FG_MUTED)
                .border(BorderFactory.createEmptyBorder(0, 10, 0, 0))
                .build().putIn(this, BorderLayout.CENTER);

        inputLabel = ComponentBuilder.createLabel("", Font.PLAIN, 11f, FG_MUTED)
                .visible(false).build().get();

        input = new ValidatedField(null, null);
        checkBox = new JCheckBox();

        for (Component c : getComponents()) {
            c.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseExited(MouseEvent e) {
                    accentBar.setBackground(TRACK);
                    FontBuilder.setFontOn(label, FontBuilder.of(Font.PLAIN, 14f, FG_MUTED));
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    accentBar.setBackground(option.getColor());
                    FontBuilder.setFontOn(label, FontBuilder.of(Font.BOLD, 14f, option.getColor()));
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    if (host.isForQuickAdd()) {
                        ((QuickAddDialog) host.getHost()).showCard(QuickAddDialog.CREATE);
                    } else {
                        boolean forEnemies = option.isEnemies();
                        Set<Combatant> available = forEnemies ? Combatants.getEnemies() : Combatants.getFriendlies();
                        List<NPC> availableNpcs = Filterable.of(available, NPC.class).toList();
                        new QuickAddDialog(forEnemies, availableNpcs, host::addCardBulk).setVisible(true);
                    }
                }
            });
        }
    }

    private void updateIncludeState() {
        boolean included = checkBox.isSelected();

        input.setVisible(included);
        inputLabel.setVisible(included);
        input.setEnabled(included);

        Color backgroundPrimary = included ? BG_SURFACE : BG_ABSENT;
        Color accent = included ? combatant.getCombatantColor() : TRACK;
        setBackground(backgroundPrimary);
        checkBox.setBackground(backgroundPrimary);
        accentBar.setBackground(accent);

        float alpha = included ? 1.0f : 0.5f;
        setForegroundAlpha(this, alpha);

        revalidate();
        repaint();
    }

    private void updateAbsentState() {
        boolean absent = checkBox.isSelected();

        input.setEnabled(!absent);

        Color background = absent ? BG_ABSENT : BG_SURFACE;
        Color accent = absent ? TRACK : combatant.getCombatantColor();
        setBackground(background);
        checkBox.setBackground(background);
        accentBar.setBackground(accent);

        float alpha = absent ? 0.5f : 1.0f;
        setForegroundAlpha(this, alpha);
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

    public int getInputValue() {
        if (input == null) return 0;
        return input.getValue().toInt();
    }

    public boolean hasValidInput() {
        return input.isInputValid() || !showInput;
    }

    public static CombatantCard getCombatantCard(
            CombatantScroller host, Combatant combatant, String inputType,
            boolean showInput, Runnable validator
    ) {
        return new CombatantCard(host, combatant, inputType, showInput, true, validator);
    }

    public static CombatantCard getCombatantQuickAddCard(
            CombatantScroller host, Combatant combatant, String inputType, Runnable validator
    ) {
        return new CombatantCard(host, combatant, inputType, true, false, validator);
    }

    public static CombatantCard getAddNewCombatantCard(CombatantScroller host, CombatantOption option) {
        return new CombatantCard(host, option);
    }

}
