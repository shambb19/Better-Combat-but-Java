package __main;

import _global_list.Combatants;
import _global_list.DamageImplements;
import _global_list.Resource;
import _global_list.Scenarios;
import creator_menu.CampaignCreatorMenu;
import com.formdev.flatlaf.intellijthemes.FlatSpacegrayIJTheme;
import combat_menu.menu.CombatMenu;
import popup.CombatEndPopup;
import lombok.*;
import _manager.EncounterManager;
import org.intellij.lang.annotations.MagicConstant;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.Optional;

public class Main {

    public static final String VERSION = "v4.4.0";
    public static final String TITLE = " || DnD Red Bull Edition " + VERSION;
    public static final long START_TIME_MILLISECONDS;

    public static final int COMBAT = 0, CREATOR = 1, UPLOAD = 2;

    @Getter private static CombatMenu combatMenu;
    @Getter private static CampaignCreatorMenu creatorMenu;

    @Getter private static URL file;

    private static boolean isCombatFinished = false;

    static {
        FlatSpacegrayIJTheme.setup();
        START_TIME_MILLISECONDS = System.currentTimeMillis();
    }

    public static void main(String[] args) {
        DamageImplements.init();
        closeAndSwitch(null, UPLOAD);
    }

    public static void closeCombat() {
        combatMenu.dispose();
    }

    public static void closeAndSwitch(Window toClose, @MagicConstant(intValues = {COMBAT, CREATOR, UPLOAD}) int toOpen) {
        Runnable onOpen = switch (toOpen) {
            case COMBAT -> () -> {
                EncounterManager.confirmQueueFinalized();
                combatMenu = new CombatMenu();
                refreshUI();
            };
            case CREATOR -> () -> creatorMenu = new CampaignCreatorMenu();
            case UPLOAD -> UploadMain::new;
            default -> throw new IndexOutOfBoundsException("Main.closeAndSwitch: unexpected toOpen int " + toOpen);
        };
        SwingUtilities.invokeLater(onOpen);

        Optional.ofNullable(toClose).ifPresent(Window::dispose);
    }

    public static void uploadCampaign(@NonNull URL file) {
        Main.file = file;
        Combatants.init(file);
        Scenarios.init(file);
        EncounterManager.setEncounter(Combatants.toBattle());
    }

    public static void refreshUI() {
        Optional.ofNullable(combatMenu).ifPresent(CombatMenu::update);

        if (!isCombatFinished && EncounterManager.getEncounter().isEncounterOver()) {
            CombatEndPopup.fireCombatEndedNaturally();
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