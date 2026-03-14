package _main;

import campaign_creator.TxtMenu;
import combat_menu.popup.FileGetter;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.net.URL;

public class CreatorMain {

    private static TxtMenu CREATOR_MENU;

    public static void run() {
        CREATOR_MENU = new TxtMenu();
    }

    public static void runDefault() {
        URL campaign = SystemMain.class.getResource("/starter.txt");
        File file = FileUtils.toFile(campaign);

        CREATOR_MENU = new TxtMenu(file);
    }

    public static void runWithPrompt() {
        CREATOR_MENU = new TxtMenu(FileGetter.getFile());
    }

    public static void kill() {
        if (CREATOR_MENU != null) {
            CREATOR_MENU.dispose();
        }
    }

}
