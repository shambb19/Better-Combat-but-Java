package gui.listener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class DieRollListener extends IntegerFieldListener {

    private final int numDice;
    private final int dieSize;
    private final JTextField root;

    /**
     * Attaches an expected number and size of dice to the root JTextField param. Limits the
     * entries to the field to integers on the range [num_dice, num_dice*size_die]. For example,
     * a field associated with a 2d6 roll would be limited to integers on [2, 12].
     * @param root the root JTextField
     */
    public DieRollListener(int numDice, int dieSize, JTextField root) {
        this.numDice = numDice;
        this.dieSize = dieSize;
        this.root = root;
    }

    /**
     * Attaches an expected size of die (assuming 1 die) to the root JTextField param. Limits the
     * entries to the field to integers on the range [1, size_die]. For example,
     * a field associated with a d20 would be limited to integers on [1, 20].
     * @param root the root JTextField
     */
    public DieRollListener(int dieSize, JTextField root) {
        numDice = 1;
        this.dieSize = dieSize;
        this.root = root;
    }

    /**
     * Changes the field border color to red if the input is: a. not an integer, or b.
     * not in the range described in the class description. Otherwise, changes the border
     * color to green.
     * @param e the event to be processed
     */
    @Override
    public void keyTyped(KeyEvent e) {
        super.keyTyped(e);
        if (root.getText().isEmpty()) {
            return;
        }
        int value = Integer.parseInt(root.getText());
        if (value > numDice * dieSize || value < numDice) {
            root.putClientProperty("JComponent.outline", "error");
        } else {
            root.putClientProperty("JComponent.outline", Color.GREEN);
        }
    }

    /**
     * Changes the border color to red if the field is empty.
     * @param e the event to be processed
     */
    @Override
    public void keyReleased(KeyEvent e) {
        if (root.getText().isEmpty()) {
            root.putClientProperty("JComponent.outline", "error");
        }
        keyTyped(e);
    }
}
