package format;

import java.awt.*;

public enum ColorStyle {

    PARTY(new Color(106, 188, 255)),
    NPC(new Color(137, 80, 245)),
    ENEMY(new Color(250, 48, 127)),

    SCENARIO(new Color(255, 237, 122)),

    PARAMETER(Color.BLACK),
    STAT_PARAMETER(null),
    KEY(new Color(122, 255, 106)),
    EQUATOR(new Color(211, 134, 255)),
    VALUE(Color.WHITE),
    COMMENT(Color.GRAY),
    EMPTY(Color.BLACK),

    FLATLAF_TEXT_DEFAULT(new Color(167, 173, 186)),

    DARKER_GREEN(new Color(15, 68, 38)),
    GREEN_APPLE(new Color(172, 255, 36)),
    DARKER_RED(new Color(150, 0, 0)),
    ORANGE_ISH_RED(new Color(255, 98, 98));

    private final Color color;

    ColorStyle(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public static Color getPercentColor(int val, int max) {
        double percent = (double) val / max;
        if (percent > 0.6) {
            return GREEN_APPLE.getColor();
        } else if (percent > 0.25) {
            return Color.YELLOW;
        } else {
            return ORANGE_ISH_RED.getColor();
        }
    }

}
