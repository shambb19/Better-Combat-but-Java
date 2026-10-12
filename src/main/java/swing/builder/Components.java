package swing.builder;

import javax.swing.*;
import java.awt.*;

public class Components {

    public static Component spacer(int x, int y) {
        return Box.createRigidArea(new Dimension(x, y));
    }

    public static Component glue() {
        return Box.createVerticalGlue();
    }

}
