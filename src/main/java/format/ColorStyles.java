package format;

import java.awt.*;

public class ColorStyles {

    public static final Color

            // ----- STATUS COLORS (Baseline) ----- //
            PERFECT = new Color(0xC6, 0x78, 0xDD),
            HEALTHY = new Color(0x5D, 0xCA, 0xA5),
            WARNING = new Color(0xEF, 0x9F, 0x27),
            CRITICAL = new Color(0xE2, 0x4B, 0x4A),
            UNKNOWN = new Color(0x3A, 0x2E, 0x3A),

    // ----- GUI COLORS ----- //
    SELECTION = new Color(0x9D, 0x8B, 0xEF),
            BACKGROUND = new Color(0x1E, 0x21, 0x28),
            SECTION_FG = new Color(0x50, 0x55, 0x68),
            TRACK = new Color(0x2A, 0x2E, 0x3A),
            DIVIDER = new Color(0x2E, 0x32, 0x40),

    // ----- UI TEXT COLORS ----- //
    TEXT_PRIMARY = new Color(0xD8, 0xDC, 0xE8),
            TEXT_MUTED = new Color(0x6B, 0x70, 0x80),
            FG_HINT = new Color(0x50, 0x55, 0x68),

    // ----- TYPE COLORS ----- //
    PARTY = new Color(0x6A, 0xBC, 0xFF),
            ALLY = new Color(0x9D, 0x8B, 0xEF),
            ENEMY = new Color(0xE2, 0x4B, 0x4A),
            SCENARIO = new Color(0xFF, 0xED, 0x7A),

    // ----- CODE / SYSTEM COLORS ----- //
    PARAMETER = new Color(0x18, 0x1A, 0x1F),
            STAT_PARAMETER = new Color(0x3E, 0x44, 0x51),
            KEY = new Color(0x98, 0xC3, 0x79),
            EQUATOR = new Color(0xC6, 0x78, 0xDD),
            VALUE = new Color(0xAB, 0xB2, 0xBF),
            COMMENT = new Color(0x5C, 0x63, 0x70),
            EMPTY = new Color(0x1E, 0x21, 0x28),

    // ----- GENERIC GOOD/BAD COLORS ----- //
    GREEN_APPLE = new Color(0xA3, 0xBE, 0x8C);

    public static Color getPercentColor(int val, int max) {
        if (max <= 0) return UNKNOWN;
        double percent = (double) val / max;

        if (percent > 0.6)
            return HEALTHY;
        else if (percent > 0.25)
            return WARNING;
        else
            return CRITICAL;
    }
}