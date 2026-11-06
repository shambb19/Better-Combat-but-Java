package util;

import combat_object.combatant.Combatant;
import exception.InvalidSyntaxError;
import lombok.experimental.*;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;

@UtilityClass public class StringUtil {

    /**
     * Returns the value of Integer.parseInt on the given String or
     * throws InvalidSyntaxError if it is not an Integer; essentially
     * a boilerplate-reducing version of Integer.parseInt
     * @param string the String to be converted to Int
     * @return the resulting int, else throws InvalidSyntaxError
     */
    public int toInt(String string) {
        if (string.isEmpty()) return 0;
        try {
            return Integer.parseInt(string.trim());
        } catch (NumberFormatException ignored) {
            throw new InvalidSyntaxError(true, "non-integer input");
        }
    }

    /**
     * Returns the given String if condition is met, otherwise returns an empty String
     * @param def the String to be returned
     * @param condition the condition that must be met to return the String
     * @return def if condition=true, else ""
     */
    public String stringIfElseBlank(String def, boolean condition) {
        return condition ? def : "";
    }

    /**
     * Returns the given String replacing the placeholders ..attacker..
     * and ..target.. with the names of their respectively given combatants
     * @param str the String to be filled in
     * @param attacker the attacker whose name will replace the ..attacker.. placeholder
     * @param target the target whose name will replace the ..target.. placeholder
     * @return the filled-in String
     */
    public String infoString(String str, Combatant attacker, Combatant target) {
        String template = str.replace("..attacker..", "%1$s")
                .replace("..target..", "%2$s");

        return template.formatted(attacker, target);
    }

    /**
     * Returns the given String with the first letter of each word capitalized
     * @param str the String to be capitalized
     * @return the new String
     */
    public String capitalized(String str) {
        return StringUtils.capitalize(str.toLowerCase());
    }

    /**
     * Returns the "Class.method" String for the source of a method call. If
     * isSecondCall is false, the source will be the caller of the method that
     * called getSource. Otherwise, the source will be the method that called that. The
     * primary use of this method is for exception message starters
     * <p>
     * For example, given the following call chain:
     * <p>
     * originalMethod calls nextMethod throws SomeException calls getSource
     * </p>
     * If isSecondCall is false, the String for nextMethod will be returned. Otherwise,
     * the String for originalMethod will be returned
     * </p>
     * @param isSecondCall whether the source for which a String requested is the second
     *                     or third method in the thread. If true, the source is the
     *                     third method, else the second
     * @return the String for the determined source method
     */
    public String getSource(boolean isSecondCall) {
        int stackTraceLocation = 3;
        if (isSecondCall) stackTraceLocation++;
        String sourceClass = Thread.currentThread().getStackTrace()[stackTraceLocation].getClassName();
        String sourceMethod = Thread.currentThread().getStackTrace()[stackTraceLocation].getMethodName();
        return String.format("%s.%s", sourceClass, sourceMethod);
    }

}