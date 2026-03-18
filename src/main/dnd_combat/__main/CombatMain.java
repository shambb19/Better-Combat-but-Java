package __main;

import _global_list.Combatants;
import _global_list.Scenarios;
import character_info.combatant.Combatant;
import combat_menu.CombatMenu;
import combat_menu.popup.CombatEndPopup;
import combat_menu.popup.EncounterFinalizationPopup;
import combat_menu.popup.FileGetter;
import encounter_info.Battle;
import encounter_info.PlayerQueue;

import javax.swing.*;
import java.io.File;
import java.util.List;
import java.util.Objects;

public class CombatMain {

    private static CombatMenu COMBAT_MENU;
    private static File INPUT;
    private static Battle BATTLE;
    private static PlayerQueue QUEUE;

    public static void run() {
        INPUT = Objects.requireNonNullElse(INPUT, FileGetter.getFile());
        completeSetup();
    }

    public static void runWith(File file) {
        INPUT = file;
        completeSetup();
    }

    private static void completeSetup() {
        Combatants.init(INPUT);
        Scenarios.init(INPUT);
        BATTLE = Combatants.toBattle();
        EncounterFinalizationPopup.run();
    }

    public static void start() {
        SwingUtilities.invokeLater(()-> {
            QUEUE = new PlayerQueue(BATTLE.friendlies(), BATTLE.enemies());
            COMBAT_MENU = new CombatMenu();
            COMBAT_MENU.setVisible(true);
        });
    }

    public static void logAction() {
        COMBAT_MENU.update();
        checkCombatOver();
    }

    /**
     * Opens a CombatEndPopup if one of the teams has been completely defeated.
     */
    public static void checkCombatOver() {
        if (BATTLE.isEncounterOver()) {
            boolean isVictory = BATTLE.isVictory();
            CombatEndPopup.run(isVictory);
        }
    }

    public static void kill() {
        COMBAT_MENU.dispose();
    }

    public static CombatMenu getMenu() {
        return COMBAT_MENU;
    }

    public static Battle getBattle() {
        return BATTLE;
    }

    public static List<Combatant> getFriendlies() {
        return BATTLE.friendlies();
    }

    public static List<Combatant> getEnemies() {
        return BATTLE.enemies();
    }

    public static PlayerQueue getQueue() {
        return QUEUE;
    }

    public static Combatant getCurrentCombatant() {
        return QUEUE.getCurrentCombatant();
    }

}
