package __main;

import _global_list.Combatants;
import _global_list.DamageImplements;
import _global_list.Resource;
import _global_list.Scenarios;
import _manager.EncounterManager;
import com.formdev.flatlaf.intellijthemes.FlatSpacegrayIJTheme;
import combat_menu.CombatMenu;
import config.Config;
import config.ruleset.Ruleset;
import creator_menu.CampaignCreatorMenu;
import ide.CampaignEditor;
import lombok.*;
import lombok.experimental.*;
import org.intellij.lang.annotations.MagicConstant;
import org.jetbrains.annotations.NotNull;
import popup.CombatEndPopup;
import swing.custom.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class Main {

    public static final String VERSION = "v4.4.0";
    public static final String TITLE = " || DnD Red Bull Edition " + VERSION;
    public static final long START_TIME_MILLISECONDS;

    public static final int COMBAT = 0, CREATOR = 1, UPLOAD = 2;

    @Getter static URL file;

    static Class<? extends MainFrame> activeMenu;
    @Getter static CombatMenu combatMenu;
    @Getter static CampaignCreatorMenu creatorMenu;

    static boolean isCombatFinished = false;

    static {
        FlatSpacegrayIJTheme.setup();
        DamageImplements.init();
        START_TIME_MILLISECONDS = System.currentTimeMillis();
    }

    public static void main(String[] args) {
        closeAndSwitch(null, UPLOAD);
        activeMenu = UploadMain.class;
    }

    public static void closeCombat() {
        combatMenu.dispose();
    }

    public static void closeAndSwitch(Window toClose, @MagicConstant(intValues = {COMBAT, CREATOR, UPLOAD}) int toOpen) {
        Runnable onOpen = switch (toOpen) {
            case COMBAT -> () -> {
                EncounterManager.confirmQueueFinalized();
                combatMenu = new CombatMenu();
                activeMenu = CombatMenu.class;
                refreshUI();
            };
            case CREATOR -> () -> {
                creatorMenu = new CampaignCreatorMenu();
                creatorMenu.getEditorPanel().importText(CampaignEditor.TUTORIAL_TEXT);
                activeMenu = CampaignCreatorMenu.class;
            };
            case UPLOAD -> () -> {
                new UploadMain();
                activeMenu = UploadMain.class;
            };
            default -> throw new IndexOutOfBoundsException("Main.closeAndSwitch: unexpected toOpen int " + toOpen);
        };
        SwingUtilities.invokeLater(onOpen);

        Optional.ofNullable(toClose).ifPresent(Window::dispose);
    }

    public static void uploadCampaign(URL file) {
        Main.file = file;
        Combatants.init(file);
        Scenarios.init(file);
        EncounterManager.setEncounter(Combatants.toBattle());
    }

    public static void applyRuleset(Ruleset ruleset) {
        Map<Class<? extends MainFrame>, Consumer<Ruleset>> rulesetApplierMap = Map.of(
                UploadMain.class, Config::setRuleset,
                CombatMenu.class, Config::setRuleset,
                CampaignCreatorMenu.class, r -> creatorMenu.getEditorPanel().setCurrentAppliedRuleset(r)
        );
        rulesetApplierMap.get(activeMenu).accept(ruleset);
    }

    public static Ruleset getRuleset() {
        Map<Class<? extends MainFrame>, Supplier<Ruleset>> rulesetGetterMap = Map.of(
                UploadMain.class, Config::getRuleset,
                CombatMenu.class, Config::getRuleset,
                CampaignCreatorMenu.class, () -> creatorMenu.getEditorPanel().getCurrentAppliedRuleset()
        );
        return rulesetGetterMap.get(activeMenu).get();
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