package combat_menu.listener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class IntegerRangeListener extends IntegerFieldListener {

    private final int min;
    private final int max;
    private final JComponent root;

    /**
     * Attaches an expected number and size of dice to the root JTextField param. Limits the
     * entries to the field to integers on the range [num_dice, num_dice*size_die]. For example,
     * a field associated with a 2d6 roll would be limited to integers on [2, 12].
     * @param root the root JTextField
     */
    public IntegerRangeListener(int min, int max, JComponent root) {
        this.min = min;
        this.max = max;
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

        int value;
        switch (root) {
            case JTextField f when f.getText().isEmpty() -> {
                return;
            }
            case JTextField f -> value = Integer.parseInt(f.getText());
            case JSpinner s -> value = (int) s.getValue();
            default -> value = 0;
        }

        if (value < min || value > max)
            root.putClientProperty("JComponent.outline", "error");
        else
            root.putClientProperty("JComponent.outline", Color.GREEN);
    }
}
