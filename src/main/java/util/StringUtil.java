package util;

import __main.Main;
import combat_object.combatant.Combatant;
import exception.InvalidSyntaxError;
import lombok.experimental.*;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.util.List;

@UtilityClass public class StringUtil {

    public int toInt(String string) {
        if (string.isEmpty()) return 0;
        try {
            return Integer.parseInt(string.trim());
        } catch (NumberFormatException ignored) {
            throw new InvalidSyntaxError(true, "non-integer input");
        }
    }

    public String stringIfElseBlank(String def, boolean condition) {
        return condition ? def : "";
    }

    public String gameTimeString() {
        long exactElapsedSeconds = (System.currentTimeMillis() - Main.START_TIME_MILLISECONDS) / 1000;

        long elapsedMinutes = exactElapsedSeconds / 60;
        long elapsedSeconds = exactElapsedSeconds % 60;

        return String.format("%d:%02d", elapsedMinutes, elapsedSeconds);
    }

    public String headerString(String str) {
        return "." + str.toLowerCase();
    }

    public String infoString(String str, Combatant attacker, Combatant target) {
        String template = str.replace("..attacker..", "%1$s")
                .replace("..target..", "%2$s");

        return template.formatted(attacker, target);
    }

    public String capitalized(String str) {
        return StringUtils.capitalize(str.toLowerCase());
    }

    public String fromList(List<String> lines, boolean emptyFirstLine) {
        String initial = emptyFirstLine ? "\n" : "";

        StringBuilder builder = new StringBuilder(initial);
        for (String line : lines) {
            builder.append(line).append("\n");
        }
        return builder.toString();
    }

}