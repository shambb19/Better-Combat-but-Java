package __main;

import _global_list.Combatants;
import _global_list.DamageImplements;
import _global_list.Scenarios;
import campaign_creator_menu.CampaignCreatorMenu;
import com.formdev.flatlaf.intellijthemes.FlatSpacegrayIJTheme;
import combat_menu.CombatMenu;
import combat_menu.popup.CombatEndPopup;
import combat_menu.popup.EncounterFinalizationPopup;
import combat_menu.popup.FileGetter;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    @NotNull
    public static final URL WEAPON_RES = Objects.requireNonNull(Main.class.getResource("/weapons.txt"));
    @NotNull
    public static final URL SPELL_RES = Objects.requireNonNull(Main.class.getResource("/spells.txt"));

    private static CampaignCreatorMenu CREATOR_MENU;

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
        CREATOR_MENU = CampaignCreatorMenu.newInstance(url);
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
        SwingUtilities.invokeLater(() -> {
            EncounterInfo.confirmQueueFinalized();
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
        if (EncounterInfo.getBattle().isEncounterOver()) {
            boolean isVictory = EncounterInfo.getBattle().isVictory();
            CombatEndPopup.run(isVictory);
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
        InputStream imgUrl = Main.class.getResourceAsStream("/logo.png");
        if (imgUrl == null) {
            throw new NullPointerException();
        }

        try {
            ImageIcon originalIcon = new ImageIcon(ImageIO.read(imgUrl));
            int width = originalIcon.getIconWidth() / 2;
            int height = originalIcon.getIconHeight() / 2;

            Image scaledImage = originalIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        } catch (IOException e) {
            Logger.getAnonymousLogger().log(
                    Level.SEVERE, "getIcon in Main : could not read logo InputStream"
            );
            return new ImageIcon();
        }
    }

}