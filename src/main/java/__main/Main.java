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
import lombok.*;
import org.intellij.lang.annotations.MagicConstant;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.Optional;

public class Main {

    public static final String VERSION = "v4.4.0";
    public static final String TITLE = " || DnD Red Bull Edition " + VERSION;

    public static final int COMBAT = 0, CREATOR = 1;

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
        Optional.ofNullable(combatMenu).ifPresent(Window::dispose);
        Optional.ofNullable(creatorMenu).ifPresent(Window::dispose);

        SwingUtilities.invokeLater(() -> UploadMain.newInstance().setVisible(true));
    }

    public static void closeCreatorAndOpenCombat(URL file) {
        creatorMenu.dispose();
        input = file;
        closeUploadAndRun(COMBAT, null);
    }

    public static void uploadCampaign(@NonNull URL file) {
        input = file;
        Combatants.init(input);
        Scenarios.init(input);
        EncounterManager.setEncounter(Combatants.toBattle());
    }

    public static void closeUploadAndRun(@MagicConstant(intValues = {COMBAT, CREATOR}) int runMode, UploadMain source) {
        if (runMode == COMBAT) {
            EncounterManager.confirmQueueFinalized();
            combatMenu = CombatMenu.newInstance();
            combatMenu.setVisible(true);
            refreshUI();
        } else {
            creatorMenu = CampaignCreatorMenu.newInstance();
        }

        Optional.ofNullable(source).ifPresent(Window::dispose);
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
            String endType = isVictory ? CombatEndPopup.VICTORY : CombatEndPopup.DEFEAT;
            CombatEndPopup.run(endType);
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