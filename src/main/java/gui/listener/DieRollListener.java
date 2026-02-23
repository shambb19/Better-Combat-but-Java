package gui.listener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class DieRollListener extends IntegerFieldListener {

    private final int numDice;
    private final int dieSize;
    private final JTextField root;

    public DieRollListener(int numDice, int dieSize, JTextField root) {
        this.numDice = numDice;
        this.dieSize = dieSize;
        this.root = root;
    }

    public DieRollListener(int dieSize, JTextField root) {
        numDice = 1;
        this.dieSize = dieSize;
        this.root = root;
    }

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

    @Override
    public void keyReleased(KeyEvent e) {
        if (root.getText().isEmpty()) {
            root.putClientProperty("JComponent.outline", "error");
        }
        keyTyped(e);
    }
}
