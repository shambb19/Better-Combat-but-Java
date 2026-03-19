package __main;

import _global_list.Combatants;
import _global_list.DamageImplements;
import _global_list.Scenarios;
import campaign_creator_menu.TxtMenu;
import com.formdev.flatlaf.intellijthemes.FlatSpacegrayIJTheme;
import combat_menu.CombatMenu;
import combat_menu.popup.CombatEndPopup;
import combat_menu.popup.EncounterFinalizationPopup;
import combat_menu.popup.FileGetter;

import javax.swing.*;
import java.net.URL;
import java.util.Objects;

public class Main {

    public static final URL WEAPON_RES = Main.class.getResource("/weapons.txt");
    public static final URL SPELL_RES = Main.class.getResource("/spells.txt");

    private static TxtMenu CREATOR_MENU;

    private static CombatMenu COMBAT_MENU;
    private static URL INPUT;

    public static void main(String[] args) {
        FlatSpacegrayIJTheme.setup();

        DamageImplements.init(WEAPON_RES);
        DamageImplements.init(SPELL_RES);

        SwingUtilities.invokeLater(UploadMain::showNewInstance);
    }

    public static void restartCombat() {
        COMBAT_MENU.dispose();
        INPUT = Objects.requireNonNullElse(INPUT, FileGetter.getUrl());
        completeSetup();
    }

    public static void switchToCombat(URL file) {
        CREATOR_MENU.dispose();
        runCombatEncounter(file);
    }

    public static void runCampaignCreator(URL url) {
        CREATOR_MENU = TxtMenu.newInstance(url);
    }

    public static void runCombatEncounter(URL file) {
        INPUT = file;
        completeSetup();
    }

    private static void completeSetup() {
        Combatants.init(INPUT);
        Scenarios.init(INPUT);
        EncounterInfo.init(Combatants.toBattle());
        EncounterFinalizationPopup.run();
    }

    public static void finalizeCombat() {
        SwingUtilities.invokeLater(()-> {
            EncounterInfo.confirmQueueFinalized();
            COMBAT_MENU = new CombatMenu();
            COMBAT_MENU.setVisible(true);
            logAction();
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
        if (EncounterInfo.getBattle().isEncounterOver()) {
            boolean isVictory = EncounterInfo.getBattle().isVictory();
            CombatEndPopup.run(isVictory);
        }
    }

    public static CombatMenu getMenu() {
        return COMBAT_MENU;
    }
}