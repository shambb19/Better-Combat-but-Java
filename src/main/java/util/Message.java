package util;

import __main.manager.EncounterManager;
import combat_menu.CombatMenu;
import format.ColorStyles;
import lombok.experimental.*;
import org.intellij.lang.annotations.MagicConstant;
import swing.swing_comp.SwingComp;
import util.PopupPrompt.PromptButton;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

import static util.PopupPrompt.of;

@ExtensionMethod(StringUtils.class)
public class Message {

    public static final int CANCEL_OPTION = 2, REMOVE_OPTION = 1, EDIT_OPTION = 0;

    public static void error(String text) {
        of(
                CombatMenu.TITLE + ": Error", text,
                new PromptButton("Acknowledge", ColorStyles.CRITICAL, 0)
        );
    }

    public static void template(String text) {
        of(
                CombatMenu.TITLE, text, new PromptButton("Close", ColorStyles.BORDER_LIGHT, 0)
        );
    }

    public static int confirmIf(String reason) {
        return of(
                "Confirm Action", "Are you sure you would like to " + reason + "?",
                new PromptButton("Cancel", ColorStyles.BORDER_LIGHT, JOptionPane.CANCEL_OPTION),
                new PromptButton("Confirm", ColorStyles.SUCCESS, JOptionPane.OK_OPTION)
        ).getResult();
    }

    public static int getWithLoopUntilInt(String message, String title) {
        while (true) {
            PopupPrompt dialog = of(
                    title, message,
                    new PromptButton("Submit", ColorStyles.SUCCESS, 1)
            );

            Color inputColor = new Color(0x2a, 0x2e, 0x3a);
            JTextField input = SwingComp.fluent(new JTextField())
                    .withBackground(inputColor)
                    .withForegroundAndCaretColor(Color.WHITE)
                    .withPaddedBorder(new LineBorder(inputColor), 8, 8, 8, 8)
                    .component();

            dialog.contentArea.add(Box.createRigidArea(new Dimension(0, 15)));
            dialog.contentArea.add(input);

            dialog.pack();

            int value = input.getText().trim().toInt();
            if (value == Integer.MIN_VALUE)
                message = "Invalid input. Please enter a whole number.";
            else
                return value;
        }
    }

    public static @MagicConstant(intValues = {CANCEL_OPTION, REMOVE_OPTION, EDIT_OPTION}) int editOrRemoveOption(String name) {
        int result = of(
                "Manage " + name,
                "What would you like to do with this entry?",
                new PromptButton("Cancel", ColorStyles.BORDER_LIGHT, CANCEL_OPTION),
                new PromptButton("Remove", ColorStyles.CRITICAL, REMOVE_OPTION),
                new PromptButton("Edit", ColorStyles.SUCCESS, EDIT_OPTION)
        ).getResult();

        return switch (result) {
            case EDIT_OPTION -> EDIT_OPTION;
            case REMOVE_OPTION -> {
                if (question("Are you sure you want to delete " + name + "?") == JOptionPane.YES_OPTION)
                    yield REMOVE_OPTION;
                else
                    yield CANCEL_OPTION;
            }
            case CANCEL_OPTION -> CANCEL_OPTION;
            default -> throw new IndexOutOfBoundsException();
        };
    }

    public static void fileError(Exception error) {
        of(
                "File Error", "Error reading file: " + error.getMessage(),
                new PromptButton("Acknowledge", ColorStyles.CRITICAL, 0)
        );
    }

    public static int question(String text) {
        return of(
                "Question", text,
                new PromptButton("No", ColorStyles.BORDER_LIGHT, JOptionPane.NO_OPTION),
                new PromptButton("Yes", ColorStyles.SUCCESS, JOptionPane.YES_OPTION)
        ).getResult();
    }

    public static int getDeathSaveRoll() {
        String message = "Roll Death Save for " + EncounterManager.getCurrentCombatant() + ".";
        int roll;
        do {
            roll = getWithLoopUntilInt(message, CombatMenu.TITLE);
            message += "Enter flat d20 value on the range of 1-20 please.";
        } while (roll <= 0 || roll > 20);
        return roll;
    }
}