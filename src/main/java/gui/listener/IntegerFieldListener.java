package gui.listener;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class IntegerFieldListener implements KeyListener {

    /**
     * Deletes any characters that are not a number.
     * @param e the event to be processed
     */
    @Override
    public void keyTyped(KeyEvent e) {
        if (!Character.isDigit(e.getKeyChar())) {
            e.consume();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {
        keyTyped(e);
    }
}
