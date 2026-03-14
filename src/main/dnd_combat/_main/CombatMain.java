package _main;

import combat_menu.CombatMenu;
import combat_menu.popup.CombatEndPopup;
import combat_menu.popup.FileGetter;
import combat_menu.popup.FinalizeCombatantsPopup;
import scenario_info.Battle;
import scenario_info.PlayerQueue;
import txt_input.Txt5eReader;

import javax.swing.*;
import java.io.File;
import java.util.Objects;

public class CombatMain {

    public static CombatMenu COMBAT_MENU;

    public static File INPUT = null;
    public static Battle BATTLE = null;
    public static PlayerQueue QUEUE;

    public static void run() {
        if (INPUT == null) {
            INPUT = FileGetter.getFile();
        }

        BATTLE = Objects.requireNonNull(Txt5eReader.getCode(INPUT)).getBattle();

        FinalizeCombatantsPopup.run();
    }

    public static void runWith(File file) {
        INPUT = file;

        BATTLE = Objects.requireNonNull(Txt5eReader.getCode(INPUT)).getBattle();

        FinalizeCombatantsPopup.run();
    }

    public static void start() {
        SwingUtilities.invokeLater(()-> {
            QUEUE = new PlayerQueue(BATTLE.friendlies(), BATTLE.enemies());
            COMBAT_MENU = new CombatMenu();
            COMBAT_MENU.setVisible(true);
        });
    }

    /**
     * Opens a CombatEndPopup if one of the teams has been completely defeated.
     */
    public static void checkWinConditions() {
        if (!(BATTLE.areAllEnemiesDefeated() || BATTLE.areAllFriendliesDefeated())) {
            return;
        }
        boolean isVictory = BATTLE.areAllEnemiesDefeated();
        CombatEndPopup.run(isVictory);
    }

    public static void kill() {
        COMBAT_MENU.dispose();
    }

}
