package combat;

import com.formdev.flatlaf.intellijthemes.FlatHighContrastIJTheme;
import gui.popup.FileInputPopup;
import gui.Menu;

import javax.swing.*;

public class Main {

    public static final String TITLE = "Better Combat but Java";

    public static Menu menu;

    public static Battle battle = null;
    public static PlayerQueue queue;

    public static void main(String[] args) {
        FlatHighContrastIJTheme.setup();
        new FileInputPopup().setVisible(true);
    }

    public static void start() {
        SwingUtilities.invokeLater(()-> {
            queue = new PlayerQueue(battle.friendlies(), battle.enemies());
            menu = new Menu();
            menu.setVisible(true);
        });
    }

    public static void checkWinConditions() {
        if (!(battle.areAllEnemiesDefeated() || battle.areAllFriendliesDefeated())) {
            return;
        }
        if (battle.areAllFriendliesDefeated()) {
            JOptionPane.showMessageDialog(
                    menu,
                    "Your party has been defeated. " +
                            "You were " + battle.percentToVictory() + " of the way to victory. " +
                            "Ask your DM for further instructions.",
                    TITLE,
                    JOptionPane.WARNING_MESSAGE
            );
        } else {
            JOptionPane.showMessageDialog(
                    menu,
                    "You are victorious! Here are your final healths:\n" + battle.getFinalHealths() +
                            "Happy campaigning!",
                    TITLE,
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
        System.exit(0);
    }

}
