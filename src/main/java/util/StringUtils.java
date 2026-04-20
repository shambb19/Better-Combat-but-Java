package util;

import combat_object.combatant.Combatant;

@lombok.experimental.UtilityClass
public class StringUtils {

    public int toInt(String string) {
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException ignored) {
            return Integer.MIN_VALUE;
        }
    }

    public String gameTimeString() {
        long exactElapsedSeconds = (System.currentTimeMillis() - __main.Main.START_TIME_MILLISECONDS) / 1000;

        long elapsedMinutes = exactElapsedSeconds / 60;
        long elapsedSeconds = exactElapsedSeconds % 60;

        return String.format("%d:%02d", elapsedMinutes, elapsedSeconds);
    }

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
        return org.apache.commons.lang3.StringUtils.capitalize(str);
    }

}