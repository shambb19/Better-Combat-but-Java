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
                "Select a run version.",
                "Better Combat but Java",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                new ImageIcon("/inspiration-button.png"),
                new String[] {"Run Combat", "Create New Combat Scenario", "Edit Existing Scenario"},
                null
        );
        switch (result) {
            case 0 -> CombatMain.run();
            case 1 -> CreatorMain.run();
            case 2 -> CreatorMain.runWithInput();
        }
    }
}
