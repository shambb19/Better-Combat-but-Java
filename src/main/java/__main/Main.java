package __main;

import _global_list.Combatants;
import _global_list.DamageImplements;
import _global_list.Resource;
import _global_list.Scenarios;
import combat_object.combatant.Combatant;
import creator_menu.CampaignCreatorMenu;
import com.formdev.flatlaf.intellijthemes.FlatSpacegrayIJTheme;
import combat_menu.menu.CombatMenu;
import encounter.Encounter;
import exception.UnexpectedClassException;
import lombok.experimental.FieldDefaults;
import swing.custom.MainFrame;
import swing.popup.CombatEndPopup;
import lombok.*;
import _manager.EncounterManager;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.Optional;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class Main {


    public static final String VERSION = "v4.9.1";
    public static final String TITLE = "DnD Red Bull Edition " + VERSION;
    public static final long START_TIME_MILLISECONDS;

    @Getter static CombatMenu combatMenu;
    @Getter static CampaignCreatorMenu creatorMenu;

    @Getter static URL file;

    static boolean isCombatFinished = false;

    static {
        FlatSpacegrayIJTheme.setup();
        START_TIME_MILLISECONDS = System.currentTimeMillis();
    }

    public static void main(String[] args) {
        DamageImplements.init();
        closeAndSwitch(null, new UploadMain());
    }

    /**
     * Closes the combat menu if it exists
     */
    public static void closeCombat() {
        Optional.of(combatMenu).ifPresent(MainFrame::dispose);
    }

    /**
     * Closes the given {@code Window} and opens the given new instance of a {@code MainFrame}
     * subclass.
     * <p>
     * NOTE: this method is poorly written, and you have to manually double-check that the to
     * class is valid (valid types are {@code CombatMenu}, {@code CampaignCreatorMenu}, and
     * {@code UploadMain})
     * @param toClose the {@code Window} to be disposed
     * @param to the {@code MainFrame} subclass to be opened
     * @param <C> the type of the {@code MainFrame} subclass
     */
    public static <C extends MainFrame> void closeAndSwitch(Window toClose, C to) {
        Class<?>[] validFrames = {CombatMenu.class, CampaignCreatorMenu.class, UploadMain.class};

        Runnable onOpen = switch (to) {
            case CombatMenu cm -> () -> {
                EncounterManager.confirmQueueFinalized();
                combatMenu = cm;
                combatMenu.endActionState();
                refreshUI();
            };
            case CampaignCreatorMenu ccm -> () -> creatorMenu = ccm;
            case UploadMain um -> () -> um.setVisible(true);
            default -> throw new UnexpectedClassException(to, validFrames);
        };
        SwingUtilities.invokeLater(onOpen);

        Optional.ofNullable(toClose).ifPresent(Window::dispose);
    }

    /**
     * Receives the given campaign {@code URL}, uploads its information to all
     * relevant global lists, and sets the tentative campaign encounter
     * @param file the campaign {@code URL} to be uploaded
     */
    public static void uploadCampaign(@NonNull URL file) {
        Main.file = file;
        Combatants.init(file);
        Scenarios.init(file);
        EncounterManager.setEncounter(new Encounter(
                Scenarios.toSet(), Combatant.enemyPartition(Combatants.toSet())
        ));
    }

    /**
     * Forces a refresh of the combat menu if it exists and checks for
     * win conditions, ending the combat if any are found
     */
    public static void refreshUI() {
        Optional.ofNullable(combatMenu).ifPresent(CombatMenu::update);

        if (!isCombatFinished && EncounterManager.getEncounter().isEncounterOver()) {
            CombatEndPopup.fireCombatEndedNaturally();
            isCombatFinished = true;
        }
    }

    /**
     * Returns the app icon in {@link Resource} scaled to
     * be the correct size for all relevant locations
     * @return the application {@code ImageIcon}
     */
    @NotNull public static ImageIcon getAppIcon() {
        URL imgUrl = Resource.APP_ICON.getUrl();

        ImageIcon originalIcon = new ImageIcon(imgUrl);
        int width = originalIcon.getIconWidth() / 4;
        int height = originalIcon.getIconHeight() / 4;

        Image scaledImage = originalIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }

}