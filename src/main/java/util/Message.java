package util;

import __main.Main;
import _manager.EncounterManager;
import combat_menu.CombatMenu;
import lombok.experimental.*;
import org.intellij.lang.annotations.MagicConstant;
import swing.fluent.SwingComp;
import util.PopupPrompt.ResultButton;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.Optional;

import static swing.ColorStyles.*;
import static util.PopupPrompt.of;
import static util.PopupPrompt.ofInput;

@ExtensionMethod(StringUtil.class)
public class Message {

    public static final String READ_ERROR = "reading", WRITE_ERROR = "writing";

    public static void showAsErrorMessage(String text) {
        of(
                CombatMenu.TITLE + ": Error", text,
                new ResultButton("Acknowledge", CRITICAL, 0)
        );
    }

    public static void showAsInfoMessage(String text) {
        of(Main.TITLE, text, new ResultButton("Close", BORDER_LIGHT, 0));
    }

    public static void showActionPrompt(String text, PopupPrompt.ActionButton[] buttons) {
        of(Main.TITLE, text, buttons);
    }

    public static String promptString(String message) {
        JTextField input = SwingComp.fluent(new JTextField())
                .withBackground(TRACK)
                .withForegroundAndCaretColor(Color.WHITE)
                .withPaddedBorder(new LineBorder(TRACK), 8, 8, 8, 8)
                .component();

        ofInput(
                Main.TITLE, message, input
        );
        return input.getText();
    }

    public static boolean wasPromptedRollSuccessful(String reason, Roll roll, int target, String failMessage) {
        return promptRoll(reason, roll, target, null, failMessage) >= target;
    }

    public static int promptRoll(String reason, Roll roll, int target, Runnable onSuccess, String failMessage) {
        String reasonFormatted = reason.replace("..name..", EncounterManager.getCurrentCombatant().toString());
        int result = promptIntWithLoop("Roll " + roll.toString() + " " + reasonFormatted + ".");

        if (result >= target) Optional.of(onSuccess).ifPresent(Runnable::run);
        else Optional.of(failMessage).ifPresent(Message::showAsInfoMessage);

        return result;
    }

    public static int promptIntWithLoop(String message) {
        while (true) {
            JTextField input = SwingComp.fluent(new JTextField())
                    .withBackground(TRACK)
                    .withForegroundAndCaretColor(Color.WHITE)
                    .withPaddedBorder(new LineBorder(TRACK), 8, 8, 8, 8)
                    .component();

            ofInput(
                    Main.TITLE, message, input
            );

            int value = input.getText().trim().toInt();
            if (value == Integer.MIN_VALUE)
                message = "Invalid input. Please enter a whole number.";
            else
                return value;
        }
    }

    public static void showFileErrorMessage(Exception error, @MagicConstant(valuesFromClass = Message.class) String errorType) {
        of(
                "File Error", "Error " + errorType + " file: " + error.getMessage(),
                new ResultButton("Acknowledge", CRITICAL, 0)
        );
    }

}