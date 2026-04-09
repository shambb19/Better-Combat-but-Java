package combat_menu.listener;

import format.ColorStyles;
import swing.swing_comp.SwingComp;

import javax.swing.*;
import javax.swing.text.JTextComponent;
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
    public IntegerRangeListener(int min, int max, JTextComponent root) {
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
        char c = e.getKeyChar();

        if (Character.isISOControl(c)) {
            super.keyTyped(e);
            return;
        }

        String prospectiveText = switch (root) {
            case JTextComponent f -> {
                String currentText = f.getText();

                int start = f.getSelectionStart();
                int end = f.getSelectionEnd();

                yield currentText.substring(0, start) + c + currentText.substring(end);
            }
            case JSpinner s -> String.valueOf(s.getValue());
            default -> throw new ClassCastException(
                    "keyTyped in IntegerRangeListener: JTextComponent or JSpinner expected"
            );
        };

        if (prospectiveText.isBlank()) {
            SwingComp.modifiable(root)
                    .withHighlight(Color.GRAY, SwingComp.BOTTOM, true);
            super.keyTyped(e);
            return;
        }

        boolean outsideRange;
        try {
            long value = Long.parseLong(prospectiveText);
            outsideRange = value < min || value > max;
        } catch (NumberFormatException ex) {
            outsideRange = true;
        }

        if (outsideRange) {
            e.consume();
        } else {
            SwingComp.modifiable(root)
                    .withHighlight(ColorStyles.GREEN_APPLE, SwingComp.BOTTOM, true);
            super.keyTyped(e);
        }
    }
}
