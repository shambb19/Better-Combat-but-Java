package combat_menu.action_panel;

import combat_object.combatant.Combatant;
import combat_object.combatant.CombatantTransferable;
import format.ColorStyles;
import lombok.*;
import lombok.experimental.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.dnd.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static combat_object.combatant.CombatantTransferable.COMBATANT_FLAVOR;
import static format.ColorStyles.*;
import static swing.swing_comp.SwingComp.button;
import static swing.swing_comp.SwingComp.label;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class DropZonePanel extends JPanel {

    static int ERROR_DURATION_MS = 1000;

    Consumer<Combatant> onDrop;
    JLabel nameLabel;
    JLabel hpLabel;
    JButton clearBtn;
    JPanel contentStack;

    @NonFinal Predicate<Combatant> isValidTarget = c -> true;
    @NonFinal Combatant current;
    @NonFinal Timer errorTimer;

    public DropZonePanel(Consumer<Combatant> onDrop) {
        this.onDrop = onDrop;

        JLabel promptLabel = label("Drag a highlighted combatant here").withDerivedFont(Font.PLAIN, 13f)
                .withForeground(FG_HINT).component();

        JLabel errorLabel = label("Invalid target").withDerivedFont(Font.BOLD, 13f)
                .withForeground(CRITICAL).component();
        errorLabel.setVisible(false);

        nameLabel = label("").withDerivedFont(Font.BOLD, 14f)
                .withForeground(HEALTHY).component();

        hpLabel = label("").withDerivedFont(Font.PLAIN, 12f)
                .withForeground(TEXT_MUTED).component();

        clearBtn = button("Clear", this::clearTarget).withDerivedFont(Font.PLAIN, 11f)
                .withBackgroundAndForeground(TRACK, TEXT_MUTED)
                .withPaddedBorder(new LineBorder(BORDER_LIGHT, 1), 3, 8, 3, 8)
                .component();

        JPanel filledRow = new JPanel(new GridBagLayout());
        filledRow.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();

        // Name & HP (Left side)
        JPanel nameHp = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        nameHp.setOpaque(false);
        nameHp.add(nameLabel);
        nameHp.add(hpLabel);

        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        filledRow.add(nameHp, gbc);

        // Clear button (Right side)
        gbc.gridx = 1;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        filledRow.add(clearBtn, gbc);

        // Main Panel Layout
        setLayout(new BorderLayout());
        setBackground(ColorStyles.BG_SURFACE);
        setOpaque(true);
        setPreferredSize(new Dimension(0, 44));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        setBorder(new EmptyBorder(0, 16, 0, 16));

        contentStack = new JPanel(new CardLayout());
        contentStack.setOpaque(false);

        contentStack.add(createCenteredWrapper(promptLabel), "PROMPT");
        contentStack.add(createCenteredWrapper(errorLabel), "ERROR");
        contentStack.add(filledRow, "FILLED");

        add(contentStack, BorderLayout.CENTER);

        installDropTarget();
    }

    private JPanel createCenteredWrapper(Component c) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;
        p.add(c, gbc);
        return p;
    }

    public void setTarget(Combatant c) {
        this.current = c;
        nameLabel.setText(c.getName());
        hpLabel.setText(c.getHealthBarString());
        showCard("FILLED");
        setBackground(ColorStyles.HOVER_TINT);
    }

    public void clearTarget() {
        this.current = null;
        showCard("PROMPT");
        setBackground(ColorStyles.BG_SURFACE);
    }

    private void showCard(String cardName) {
        CardLayout cl = (CardLayout) contentStack.getLayout();
        cl.show(contentStack, cardName);
        revalidate();
        repaint();
    }

    private void flashError() {
        if (errorTimer != null && errorTimer.isRunning()) errorTimer.stop();

        showCard("ERROR");
        setBackground(ColorStyles.ERROR_BG);

        errorTimer = new Timer(ERROR_DURATION_MS, e -> {
            if (current == null) {
                showCard("PROMPT");
                setBackground(ColorStyles.BG_SURFACE);
            } else {
                showCard("FILLED");
                setBackground(ColorStyles.HOVER_TINT);
            }
        });
        errorTimer.setRepeats(false);
        errorTimer.start();
    }

    private void installDropTarget() {
        new DropTarget(this, DnDConstants.ACTION_COPY, new DropTargetAdapter() {
            @Override
            public void dragEnter(DropTargetDragEvent e) {
                if (e.isDataFlavorSupported(COMBATANT_FLAVOR)) {
                    setBackground(ColorStyles.HOVER_TINT);
                }
            }

            @Override
            public void dragExit(DropTargetEvent e) {
                setBackground(current == null ? ColorStyles.BG_SURFACE : ColorStyles.HOVER_TINT);
            }

            @Override
            public void drop(DropTargetDropEvent e) {
                try {
                    if (e.isDataFlavorSupported(COMBATANT_FLAVOR)) {
                        e.acceptDrop(DnDConstants.ACTION_COPY);
                        Combatant dropped = (Combatant) e.getTransferable().getTransferData(CombatantTransferable.COMBATANT_FLAVOR);

                        if (isValidTarget.test(dropped)) {
                            onDrop.accept(dropped);
                            e.dropComplete(true);
                        } else {
                            flashError();
                            e.dropComplete(true);
                        }
                    } else {
                        e.rejectDrop();
                    }
                } catch (Exception ex) {
                    e.dropComplete(false);
                }
            }
        });
    }

    public void setTargetValidator(Predicate<Combatant> validator) {
        this.isValidTarget = validator;
    }

    public void removeClearOption() {
        clearBtn.setVisible(false);
    }
}