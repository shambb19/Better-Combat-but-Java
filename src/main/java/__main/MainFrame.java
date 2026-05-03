package __main;

import format.ColorStyles;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    {
        setIconImage(__main.Main.getAppIcon().getImage());
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        setBackground(ColorStyles.BACKGROUND);

        GraphicsConfiguration config = getGraphicsConfiguration();
        Rectangle bounds = config.getBounds();
        Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(config);

        int SHADOW = 8;

        int x = bounds.x + insets.left - SHADOW;
        int y = bounds.y + insets.top;
        int width = bounds.width - insets.left - insets.right + (SHADOW * 2);
        int height = bounds.height - insets.top - insets.bottom + SHADOW;

        setBounds(x, y, width, height);
    }

}
