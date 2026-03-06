package main;

import com.formdev.flatlaf.intellijthemes.FlatSpacegrayIJTheme;
import damage_implements.Spells;
import damage_implements.Weapons;

import javax.swing.*;
import java.net.URL;
import java.util.Objects;

public class SystemMain {

    public static final URL WEAPON_RES = SystemMain.class.getResource("/weapons.txt");
    public static final URL SPELL_RES = SystemMain.class.getResource("/spells.txt");

    public static final ImageIcon ICON = new ImageIcon(Objects.requireNonNull(SystemMain.class.getResource("/logo.png")));

    public static void main(String[] args) {
        FlatSpacegrayIJTheme.setup();

        if (WEAPON_RES != null) {
            Weapons.init(WEAPON_RES);
        }
        if (SPELL_RES != null) {
            Spells.init(SPELL_RES);
        }

        SwingUtilities.invokeLater(SystemMain::promptRunMode);
    }

    private static void promptRunMode() {
        String message = "Select a run version. This looks weird with so little text so did you know " +
                "that when Viggo Mortensen kicked the orc helmet in The Two Towers he actually " +
                "broke his toe, and so his yell was real and not acting?";

        int result = JOptionPane.showOptionDialog(
                null,
                message,
                "Better Combat but Java",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                ICON,
                new String[] {"Run Combat", "Create New Campaign", "Edit Existing Campaign"},
                null
        );

        switch (result) {
            case 0 -> CombatMain.run();
            case 1 -> CreatorMain.run();
            case 2 -> CreatorMain.runWithInput();
        }
    }

    public static void restartCombat() {
        CombatMain.kill();
        CombatMain.run();
    }
}