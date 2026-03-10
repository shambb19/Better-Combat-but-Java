package main;

import combat_menu.popup.FileGetter;
import org.apache.commons.io.FileUtils;
import org.aspectj.util.FileUtil;
import txt_menu.TxtMenu;

import java.io.File;
import java.net.URL;

public class CreatorMain {

    public static void run() {
        new TxtMenu().setVisible(true);
    }

    public static void runDefault() {
        URL campaign = SystemMain.class.getResource("/starter.txt");
        File file = FileUtils.toFile(campaign);
        new TxtMenu(file).setVisible(true);
    }

    public static void runWithPrompt() {
        File file = new FileGetter().getFile();
        new TxtMenu(file).setVisible(true);
    }

}
