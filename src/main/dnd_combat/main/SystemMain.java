package main;

import com.formdev.flatlaf.intellijthemes.FlatHighContrastIJTheme;

import javax.swing.*;

public class SystemMain {

    public static void main(String[] args) {
        FlatHighContrastIJTheme.setup();
        promptRunMode();
    }

    private static void promptRunMode() {
        int result = JOptionPane.showOptionDialog(
                null,
                "Select a run version. This looks weird with so little text so did you know " +
                        "that when Viggo Mortensen kicked the orc helmet in The Two Towers he actually " +
                        "broke his toe, and so his yell was real and not acting?",
                "Better Combat but Java",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                new ImageIcon("/inspiration-button.png"),
                new String[] {"Run Combat", "Create New Campaign", "Edit Existing Campaign"},
                null
        );
        switch (result) {
            case 0 -> CombatMain.run();
            case 1 -> CreatorMain.run();
            case 2 -> CreatorMain.runWithInput();
        }
    }
}
