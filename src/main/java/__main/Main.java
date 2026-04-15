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
import combat_menu.popup.EncounterSelectionPopup;
import lombok.*;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class Main {

    public static final String VERSION = "v4.3.1";
    public static final String TITLE = " || DnD Red Bull Edition " + VERSION;

    private static URL input;

    private static CampaignCreatorMenu creatorMenu;
    @Getter private static CombatMenu combatMenu;

    private static boolean isCombatFinished = false;

    static {
        FlatSpacegrayIJTheme.setup();
    }

    public static void main(String[] args) {
        DamageImplements.init();
        clearAllAndShowUploadMenu();
    }

    public static void clearAllAndShowUploadMenu() {
        if (combatMenu != null) combatMenu.dispose();
        if (creatorMenu != null) creatorMenu.dispose();

        SwingUtilities.invokeLater(() -> UploadMain.newInstance().setVisible(true));
    }

    private static void uploadCampaignAndFinalizeEncounter() {
        // 1. Load all Combatants into the GlobalList first
        // This fills Combatants.INSTANCE.list
        Combatants.init(input);

        // 2. NOW load Scenarios.
        // When Scenario.from() is called inside this method,
        // Combatants.getEnemies() will actually have data!
        Scenarios.init(input);

        EncounterManager.setEncounter(Combatants.toBattle());
        EncounterSelectionPopup.newInstance().setVisible(true);
    }

    public static void closeCreatorAndOpenCombat(URL file) {
        creatorMenu.dispose();
        runCombatEncounter(file);
    }

    public static void runCombatEncounter(@NonNull URL file) {
        input = file;
        uploadCampaignAndFinalizeEncounter();
    }

    public static void runCampaignCreator(URL url) {
        creatorMenu = CampaignCreatorMenu.newInstance(url);
    }

    public static void finalizeAndStartCombat() {
        SwingUtilities.invokeLater(() -> {
            EncounterManager.confirmQueueFinalized();
            combatMenu = CombatMenu.newInstance();
            combatMenu.setVisible(true);
            refreshUI();
        });
    }

    public static void refreshUI() {
        if (combatMenu == null) return;

        combatMenu.update();
        checkVictoryConditions();
    }

    public static void checkVictoryConditions() {
        if (isCombatFinished) return;

        if (EncounterManager.getEncounter().isEncounterOver()) {
            boolean isVictory = EncounterManager.getEncounter().isVictory();
            CombatEndPopup.run(isVictory);
            isCombatFinished = true;
        }
    }

    @NotNull public static ImageIcon getAppIcon() {
        URL imgUrl = Resource.APP_ICON.getUrl();

        ImageIcon originalIcon = new ImageIcon(imgUrl);
        int width = originalIcon.getIconWidth() / 4;
        int height = originalIcon.getIconHeight() / 4;

        Image scaledImage = originalIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }

}