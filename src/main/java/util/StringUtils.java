package util;

@lombok.experimental.UtilityClass
public class StringUtils {

    public int toInt(String string) {
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException ignored) {
            return Integer.MIN_VALUE;
        }
    }

    public String capitalized(String str) {
        return org.apache.commons.lang3.StringUtils.capitalize(str);
    }

}