package gui.listener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class DieRollListener extends IntegerFieldListener {

    private final int numDice;
    private final int dieSize;
    private final JTextField root;
    private final boolean isManual;

    public DieRollListener(int numDice, int dieSize, JTextField root) {
        this.numDice = numDice;
        this.dieSize = dieSize;
        this.root = root;
        isManual = numDice < 0;
    }

    public DieRollListener(int dieSize, JTextField root) {
        numDice = 1;
        this.dieSize = dieSize;
        this.root = root;
        isManual = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        super.keyTyped(e);
        if (root.getText().isEmpty()) {
            return;
        }
        if (isManual) {
            keyTypedManual();
            return;
        }
        int value = Integer.parseInt(root.getText());
        if (value > numDice * dieSize || value < numDice) {
            root.putClientProperty("JComponent.outline", "error");
        } else {
            root.putClientProperty("JComponent.outline", Color.GREEN);
        }
    }

    public void keyTypedManual() {
        int value = Integer.parseInt(root.getText());
        if (value < 1) {
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
