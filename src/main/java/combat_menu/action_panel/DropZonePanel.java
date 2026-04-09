package combat_menu.action_panel;

import character_info.combatant.Combatant;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.dnd.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class DropZonePanel extends JPanel {

    private static final Color BG_EMPTY = new Color(0x23, 0x26, 0x2E);
    private static final Color BG_HOVER = new Color(0x1A, 0x2A, 0x24);
    private static final Color BG_FILLED = new Color(0x1A, 0x2A, 0x24);
    private static final Color BG_ERROR = new Color(0x2A, 0x1A, 0x1A);
    private static final Color FG_TARGET = new Color(0x5D, 0xCA, 0xA5);
    private static final Color FG_PROMPT = new Color(0x50, 0x55, 0x68);
    private static final Color FG_ERROR = new Color(0xE2, 0x4B, 0x4A);
    private static final Color FG_HP = new Color(0x6B, 0x70, 0x80);
    private static final Color BG_CLEAR = new Color(0x2A, 0x2E, 0x3A);
    private static final Color BORDER_CLR = new Color(0x3A, 0x3E, 0x4A);

    private static final int ERROR_DURATION_MS = 1000;

    private final Consumer<Combatant> onDrop;
    private final JLabel nameLabel;
    private final JLabel hpLabel;
    private final JButton clearBtn;
    private final JPanel contentStack;

    private Predicate<Combatant> isValidTarget = c -> true;
    private Combatant current;
    private Timer errorTimer;

    public DropZonePanel(Consumer<Combatant> onDrop) {
        this.onDrop = onDrop;

        JLabel promptLabel = new JLabel("Drag a highlighted combatant here");
        promptLabel.setFont(promptLabel.getFont().deriveFont(Font.PLAIN, 13f));
        promptLabel.setForeground(FG_PROMPT);

        JLabel errorLabel = new JLabel("Invalid target");
        errorLabel.setFont(errorLabel.getFont().deriveFont(Font.BOLD, 13f));
        errorLabel.setForeground(FG_ERROR);
        errorLabel.setVisible(false);

        nameLabel = new JLabel("");
        nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD, 14f));
        nameLabel.setForeground(FG_TARGET);

        hpLabel = new JLabel("");
        hpLabel.setFont(hpLabel.getFont().deriveFont(Font.PLAIN, 12f));
        hpLabel.setForeground(FG_HP);

        clearBtn = new JButton("✕ clear");
        clearBtn.setFont(clearBtn.getFont().deriveFont(Font.PLAIN, 11f));
        clearBtn.setForeground(FG_HP);
        clearBtn.setBackground(BG_CLEAR);
        clearBtn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_CLR, 1),
                new EmptyBorder(3, 8, 3, 8)));
        clearBtn.setFocusPainted(false);
        clearBtn.setOpaque(true);
        clearBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        clearBtn.addActionListener(e -> clearTarget());

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

        // 4. Main Panel Layout
        setLayout(new BorderLayout());
        setBackground(BG_EMPTY);
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
        nameLabel.setText(c.name());
        hpLabel.setText(c.getHealthBarString());
        showCard("FILLED");
        setBackground(BG_FILLED);
    }

    public void clearTarget() {
        this.current = null;
        showCard("PROMPT");
        setBackground(BG_EMPTY);
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
        setBackground(BG_ERROR);

        errorTimer = new Timer(ERROR_DURATION_MS, e -> {
            if (current == null) {
                showCard("PROMPT");
                setBackground(BG_EMPTY);
            } else {
                showCard("FILLED");
                setBackground(BG_FILLED);
            }
        });
        errorTimer.setRepeats(false);
        errorTimer.start();
    }

    private void installDropTarget() {
        new DropTarget(this, DnDConstants.ACTION_COPY, new DropTargetAdapter() {
            @Override
            public void dragEnter(DropTargetDragEvent e) {
                if (e.isDataFlavorSupported(Combatant.COMBATANT_FLAVOR)) setBackground(BG_HOVER);
            }

            @Override
            public void dragExit(DropTargetEvent e) {
                setBackground(current == null ? BG_EMPTY : BG_FILLED);
            }

            @Override
            public void drop(DropTargetDropEvent e) {
                try {
                    if (e.isDataFlavorSupported(Combatant.COMBATANT_FLAVOR)) {
                        e.acceptDrop(DnDConstants.ACTION_COPY);
                        Combatant dropped = (Combatant) e.getTransferable().getTransferData(Combatant.COMBATANT_FLAVOR);

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