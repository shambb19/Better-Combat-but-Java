package main;

import combat_menu.popup.FinalizeCombatantsPopup;
import scenario_info.Battle;
import scenario_info.PlayerQueue;
import combat_menu.popup.CombatEndPopup;
import combat_menu.popup.FileGetter;
import combat_menu.CombatMenu;
import txt_input.BattleReader;

import javax.swing.*;
import java.io.File;

public class CombatMain {

    public static CombatMenu COMBAT_MENU;

    public static Battle BATTLE = null;
    public static PlayerQueue QUEUE;

    public static void run() {
        File input = new FileGetter().getFile();
        BATTLE = new BattleReader(input).getBattle();

        new FinalizeCombatantsPopup().setVisible(true);
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
        new CombatEndPopup(isVictory).setVisible(true);
    }

}
