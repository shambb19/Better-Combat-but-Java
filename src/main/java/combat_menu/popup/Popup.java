package combat_menu.popup;

import javax.swing.*;

public abstract class Popup extends JDialog {

    {
        setIconImage(__main.Main.getAppIcon().getImage());
        setModal(false);
        setAlwaysOnTop(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }
}
