package __main;

import _global_list.DamageImplements;
import com.formdev.flatlaf.intellijthemes.FlatSpacegrayIJTheme;

import javax.swing.*;
import java.net.URL;

public class SystemMain {

    public static final URL WEAPON_RES = SystemMain.class.getResource("/weapons.txt");
    public static final URL SPELL_RES = SystemMain.class.getResource("/spells.txt");

    //public static final ImageIcon ICON = new ImageIcon(Objects.requireNonNull(SystemMain.class.getResource("/logo.png")));

    public static void main(String[] args) {
        FlatSpacegrayIJTheme.setup();

        DamageImplements.init(WEAPON_RES);
        DamageImplements.init(SPELL_RES);

        SwingUtilities.invokeLater(UploadMain::showNewInstance);
    }

    public static void restartCombat() {
        CombatMain.kill();
        CombatMain.run();
    }

    public static void switchToCombat(URL file) {
        CreatorMain.kill();
        CombatMain.runWith(file);
    }
}