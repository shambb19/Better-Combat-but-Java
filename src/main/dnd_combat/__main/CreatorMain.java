package __main;

import campaign_creator_menu.TxtMenu;

import java.net.URL;

public class CreatorMain {

    private static TxtMenu CREATOR_MENU;

    public static void run(URL url) {
        CREATOR_MENU = TxtMenu.newInstance(url);

    }

    public static void kill() {
        if (CREATOR_MENU != null) {
            CREATOR_MENU.dispose();
        }
    }

}
