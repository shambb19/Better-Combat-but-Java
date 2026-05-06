package swing.custom;

import javax.swing.*;

public abstract class Popup extends JDialog {

    {
        setIconImage(__main.Main.getAppIcon().getImage());
        setModal(false);
        setAlwaysOnTop(true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }
}
