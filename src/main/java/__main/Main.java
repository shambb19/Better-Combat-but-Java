package __main;

import __main.manager.EncounterManager;
import _global_list.Combatants;
import _global_list.DamageImplements;
import _global_list.Resource;
import _global_list.Scenarios;
import campaign_creator_menu.CampaignCreatorMenu;
import com.formdev.flatlaf.intellijthemes.FlatSpacegrayIJTheme;
import combat_menu.CombatMenu;
import combat_menu.popup.CombatEndPopup;
import combat_menu.popup.EncounterFinalizationPopup;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class Main {

    public static final String VERSION = "v4.3.1";
    public static final String TITLE = " || DnD Red Bull Edition " + VERSION;

    private static CampaignCreatorMenu CREATOR_MENU;

    private static CombatMenu COMBAT_MENU;
    private static URL INPUT;

    private static boolean isCombatFinished = false;

    public static void main(String[] args) {
        FlatSpacegrayIJTheme.setup();

        DamageImplements.init();

        SwingUtilities.invokeLater(UploadMain::showNewInstance);
    }

    public static void restart() {
        COMBAT_MENU.dispose();
        SwingUtilities.invokeLater(UploadMain::showNewInstance);
    }

    private static void completeSetup() {
        Combatants.init(INPUT);
        Scenarios.init(INPUT);
        EncounterManager.init(Combatants.toBattle());
        EncounterFinalizationPopup.run();
    }

    public static void switchToCombat(URL file) {
        CREATOR_MENU.dispose();
        runCombatEncounter(file);
    }

    public static void runCombatEncounter(URL file) {
        INPUT = file;
        completeSetup();
    }

    public static void runCampaignCreator(URL url) {
        CREATOR_MENU = CampaignCreatorMenu.newInstance(url);
    }

    public static void finalizeCombat() {
        SwingUtilities.invokeLater(() -> {
            EncounterManager.confirmQueueFinalized();
            COMBAT_MENU = CombatMenu.newInstance();
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
        if (isCombatFinished) return;

        if (EncounterManager.getBattle().isEncounterOver()) {
            boolean isVictory = EncounterManager.getBattle().isVictory();
            CombatEndPopup.run(isVictory);
            isCombatFinished = true;
        }
    }

    public static CombatMenu getMenu() {
        return COMBAT_MENU;
    }

    @NotNull
    public static Image getImage() {
        return getIcon().getImage();
    }

    @NotNull
    public static ImageIcon getIcon() {
        URL imgUrl = Resource.PROGRAM_LOGO.url();

        ImageIcon originalIcon = new ImageIcon(imgUrl);
        int width = originalIcon.getIconWidth() / 4;
        int height = originalIcon.getIconHeight() / 4;

        Image scaledImage = originalIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }

}