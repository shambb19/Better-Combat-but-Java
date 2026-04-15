package util;

import lombok.experimental.*;

@UtilityClass
public class StringUtils {

    public int toInt(String string) {
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException ignored) {
            return Integer.MIN_VALUE;
        }
    }

}