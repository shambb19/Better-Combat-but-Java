package util;

import __main.manager.EncounterManager;
import combat_menu.CombatMenu;
import format.swing_comp.SwingComp;
import lombok.experimental.*;
import org.intellij.lang.annotations.MagicConstant;
import util.PopupPrompt.ResultButton;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

import static format.ColorStyles.*;
import static util.PopupPrompt.of;
import static util.PopupPrompt.ofInput;

@ExtensionMethod(StringUtils.class)
public class Message {

    public static final int CANCEL_OPTION = 2, REMOVE_OPTION = 1, EDIT_OPTION = 0;

    public static void showAsErrorMessage(String text) {
        of(
                CombatMenu.TITLE + ": Error", text,
                new ResultButton("Acknowledge", CRITICAL, 0)
        );
    }

    public static void showAsInfoMessage(String text) {
        of(CombatMenu.TITLE, text, new ResultButton("Close", BORDER_LIGHT, 0));
    }

    public static void showActionPrompt(String text, PopupPrompt.ActionButton[] buttons) {
        of(CombatMenu.TITLE, text, buttons);
    }

    public static int promptIntWithLoop(String message, String title) {
        while (true) {

            JTextField input = SwingComp.fluent(new JTextField())
                    .withBackground(TRACK)
                    .withForegroundAndCaretColor(Color.WHITE)
                    .withPaddedBorder(new LineBorder(TRACK), 8, 8, 8, 8)
                    .component();

            ofInput(
                    title, message, input
            );

            int value = input.getText().trim().toInt();
            if (value == Integer.MIN_VALUE)
                message = "Invalid input. Please enter a whole number.";
            else
                return value;
        }
    }

    public static @MagicConstant(intValues = {CANCEL_OPTION, REMOVE_OPTION, EDIT_OPTION}) int showEditOrRemovePrompt(String name) {
        int result = of(
                "Manage " + name,
                "What would you like to do with this entry?",
                new ResultButton("Cancel", BORDER_LIGHT, CANCEL_OPTION),
                new ResultButton("Remove", CRITICAL, REMOVE_OPTION),
                new ResultButton("Edit", SUCCESS, EDIT_OPTION)
        ).getResult();

        return switch (result) {
            case EDIT_OPTION -> EDIT_OPTION;
            case REMOVE_OPTION -> {
                int deleteResult = askYesNoQuestion("Are you sure you want to delete " + name + "?");
                if (deleteResult == JOptionPane.YES_OPTION)
                    yield REMOVE_OPTION;
                else
                    yield CANCEL_OPTION;
            }
            case CANCEL_OPTION -> CANCEL_OPTION;
            default -> throw new IndexOutOfBoundsException();
        };
    }

    public static void showFileErrorMessage(Exception error) {
        of(
                "File Error", "Error reading file: " + error.getMessage(),
                new ResultButton("Acknowledge", CRITICAL, 0)
        );
    }

    public static int askYesNoQuestion(String text) {
        return of(
                "Question", text,
                new ResultButton("No", BORDER_LIGHT, JOptionPane.NO_OPTION),
                new ResultButton("Yes", SUCCESS, JOptionPane.YES_OPTION)
        ).getResult();
    }

    public static int promptDeathSaveRoll() {
        StringBuilder message = new StringBuilder("Roll Death Save for " + EncounterManager.getCurrentCombatant() + ".");
        int roll;
        do {
            roll = promptIntWithLoop(message.toString(), CombatMenu.TITLE);
            message.append("Enter flat d20 value on the range of 1-20 please.");
        } while (roll <= 0 || roll > 20);
        return roll;
    }
}