package main;

import combat_menu.popup.FileGetter;
import txt_menu.TxtMenu;

import java.io.File;

public class CreatorMain {

    public static void run() {
        new TxtMenu().setVisible(true);
    }

    public static void runWithInput() {
        File file = new FileGetter().getFile();
        new TxtMenu(file).setVisible(true);
    }

}
