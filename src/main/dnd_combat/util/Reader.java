package util;

public class Reader {

    public static String identifier(String line) {
        return line.substring(0, line.indexOf("="));
    }

    public static String withoutIdentifier(String line) {
        return line.substring(line.indexOf("=") + 1);
    }

}
