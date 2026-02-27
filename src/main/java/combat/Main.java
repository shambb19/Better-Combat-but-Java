package combat;

import com.formdev.flatlaf.intellijthemes.FlatHighContrastIJTheme;
import gui.popup.CombatEndPopup;
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

    /**
     * Opens a CombatEndPopup if one of the teams has been completely defeated.
     */
    public static void checkWinConditions() {
        if (!(battle.areAllEnemiesDefeated() || battle.areAllFriendliesDefeated())) {
            return;
        }
        boolean isVictory = battle.areAllEnemiesDefeated();
        new CombatEndPopup(isVictory).setVisible(true);
    }

}
