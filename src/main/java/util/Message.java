package util;

import __main.Main;
import __main.manager.EncounterManager;
import combat_menu.CombatMenu;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;

public class Message {

    private static final Color COLOR_CONFIRM = new Color(0x1D, 0x9E, 0x75);
    private static final Color COLOR_CANCEL = new Color(0x3A, 0x3E, 0x4A);
    private static final Color COLOR_DANGER = new Color(0xE2, 0x4B, 0x4A);

    public static void template(String text) {
        PopupPrompt dialog = new PopupPrompt(CombatMenu.TITLE);
        dialog.addMessage(text);
        dialog.footer.add(dialog.createButton("Close", COLOR_CANCEL, 0));
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    public static int confirmIf(String reason) {
        PopupPrompt dialog = new PopupPrompt("Confirm Action");
        dialog.addMessage("Are you sure you would like to " + reason + "?");
        dialog.footer.add(dialog.createButton("Cancel", COLOR_CANCEL, JOptionPane.CANCEL_OPTION));
        dialog.footer.add(dialog.createButton("Confirm", COLOR_CONFIRM, JOptionPane.OK_OPTION));

        dialog.pack();
        dialog.setLocationRelativeTo(Main.getMenu());
        dialog.setVisible(true);
        return dialog.getResult();
    }

    public static int getWithLoopUntilInt(String message, String title) {
        while (true) {
            PopupPrompt dialog = new PopupPrompt(title);
            dialog.addMessage(message);

            JTextField input = new JTextField();
            input.setBackground(new Color(0x2A, 0x2E, 0x3A));
            input.setForeground(Color.WHITE);
            input.setCaretColor(Color.WHITE);
            input.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(0x3A, 0x3E, 0x4A)),
                    new EmptyBorder(8, 8, 8, 8)
            ));
            dialog.contentArea.add(Box.createRigidArea(new Dimension(0, 15)));
            dialog.contentArea.add(input);

            dialog.footer.add(dialog.createButton("Submit", COLOR_CONFIRM, 1));

            dialog.pack();
            dialog.setLocationRelativeTo(null);
            dialog.setVisible(true);

            try {
                return Integer.parseInt(input.getText().trim());
            } catch (Exception e) {
                message = "Invalid input. Please enter a whole number.";
            }
        }
    }

    public static int editOrRemoveOption(String name) {
        PopupPrompt dialog = new PopupPrompt("Manage " + name);
        dialog.addMessage("What would you like to do with this entry?");

        dialog.footer.add(dialog.createButton("Cancel", COLOR_CANCEL, 2));
        dialog.footer.add(dialog.createButton("Remove", COLOR_DANGER, 1));
        dialog.footer.add(dialog.createButton("Edit", COLOR_CONFIRM, 0));

        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);

        int result = dialog.getResult();
        if (result == 1)
            if (question("Are you sure you want to delete " + name + "?") != JOptionPane.YES_OPTION) return 2;

        return result;
    }

    public static void fileError(Component parent, Exception error) {
        PopupPrompt dialog = new PopupPrompt("File Error");
        dialog.addMessage("Error reading file: " + error.getMessage());
        dialog.footer.add(dialog.createButton("Acknowledge", COLOR_DANGER, 0));
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    public static int question(String text) {
        PopupPrompt dialog = new PopupPrompt("Question");
        dialog.addMessage(text);
        dialog.footer.add(dialog.createButton("No", COLOR_CANCEL, JOptionPane.NO_OPTION));
        dialog.footer.add(dialog.createButton("Yes", COLOR_CONFIRM, JOptionPane.YES_OPTION));
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
        return dialog.getResult();
    }

    public static int getDeathSaveRoll() {
        return getWithLoopUntilInt(
                "Roll Death Save for " + EncounterManager.getCurrentCombatant().name() + ".",
                CombatMenu.TITLE
        );
    }

    public static void throwFileDownloadError(JPanel root, IOException error) {
        fileError(root, error);
    }
}