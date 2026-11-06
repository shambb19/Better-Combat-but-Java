package swing.builder;

import javax.swing.*;
import java.awt.*;

public class Components {

    /**
     * Returns an instance of Box.createRigidArea with the given
     * x and y constraints.
     * @param x the x-constraint of the spacer
     * @param y the y-constraint of the spacer
     * @return the new Component object
     */
    public static Component spacer(int x, int y) {
        return Box.createRigidArea(new Dimension(x, y));
    }

    /**
     * Returns an instance of Box.createVerticalGlue
     * @return the new Component object
     */
    public static Component glue() {
        return Box.createVerticalGlue();
    }

}
