package util;

import __main.Main;
import combat_object.combatant.Combatant;
import exception.InvalidSyntaxError;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;

@lombok.experimental.UtilityClass
public class StringUtil {

    public int toInt(String string) {
        try {
            return Integer.parseInt(withoutWhitespace(string));
        } catch (NumberFormatException ignored) {
            throw new InvalidSyntaxError(true, "non-integer input");
        }
    }

    public String withoutWhitespace(Object o) {
        String s = String.valueOf(o);
        if (o instanceof JTextField f) s = f.getText();

        return s.toLowerCase().replace(" ", "");
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

    /*
        The claude torture test said this method returned the unmodified input string, but I
        have not observed this issue. So, I'm gonna ignore it.
     */
    public String infoString(String str, Combatant attacker, Combatant target) {
        final String ATTACKER = "..attacker..";
        final String TARGET = "..target..";

        StringBuilder builder = new StringBuilder(str);

        class Replacer {
            void replaceKey(final String key, Combatant combatant) {
                while (builder.toString().contains(key)) {
                    int idxKey = builder.indexOf(key);
                    int idxKeyEnd = idxKey + key.length();
                    builder.replace(idxKey, idxKeyEnd, combatant.getName());
                }
            }
        }
        Replacer $Replacer = new Replacer();

        $Replacer.replaceKey(ATTACKER, attacker);
        $Replacer.replaceKey(TARGET, target);

        return builder.toString();
    }

    public String capitalized(String str) {
        return StringUtils.capitalize(str.toLowerCase());
    }

}