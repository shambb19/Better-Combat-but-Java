package format;

import java.awt.*;

public class ColorStyles {

    public static final Color
            // ----- STATUS COLORS ----- //
            PERFECT = new Color(0xC6, 0x78, 0xDD),
            HEALTHY = new Color(0x5D, 0xCA, 0xA5),
            WARNING = new Color(0xEF, 0x9F, 0x27),
            CRITICAL = new Color(0xE2, 0x4B, 0x4A),
            UNKNOWN = new Color(0x3A, 0x2E, 0x3A),
            SUCCESS = new Color(0x1D, 0x9E, 0x75),
            ERROR_BG = new Color(0x2A, 0x1A, 0x1A),

    // ----- GUI BACKGROUNDS ----- //
    BACKGROUND = new Color(0x1E, 0x21, 0x28),
            BG_DARK = new Color(0x19, 0x1C, 0x22),
            BG_DEEP = new Color(0x16, 0x18, 0x1E),
            BG_SURFACE = new Color(0x23, 0x26, 0x2E),

    // ----- INTERACTIVE STATES ----- //
    SELECTION = new Color(0x9D, 0x8B, 0xEF),
            ACTION_PRIMARY = new Color(0x3A, 0x34, 0x60),
            ACTION_HOVER = new Color(0x50, 0x48, 0xA0),
            HOVER_TINT = new Color(0x1A, 0x2A, 0x24),

    // ----- BORDERS & DIVIDERS ----- //
    TRACK = new Color(0x2A, 0x2E, 0x3A),
            DIVIDER = new Color(0x2E, 0x32, 0x40),
            BORDER_LIGHT = new Color(0x3A, 0x3E, 0x4A),

    // ----- UI TEXT COLORS ----- //
    TEXT_PRIMARY = new Color(0xD8, 0xDC, 0xE8),
            TEXT_MUTED = new Color(0x6B, 0x70, 0x80),
            FG_HINT = new Color(0x50, 0x55, 0x68),
            SECTION_FG = new Color(0x50, 0x55, 0x68),

    // ----- TYPE COLORS ----- //
    PARTY = new Color(0x6A, 0xBC, 0xFF),
            ALLY = new Color(0x9D, 0x8B, 0xEF),
            ENEMY = new Color(0xE2, 0x4B, 0x4A),
            SCENARIO = new Color(0xFF, 0xED, 0x7A),

    // ----- SPECIAL THEMES ----- //
    LOCKED = new Color(0x35, 0x38, 0x42),
            LOCKED_FG = new Color(0x40, 0x44, 0x50),
            GOLD = new Color(0xBA, 0x75, 0x17),
            GOLD_TINT = new Color(0x28, 0x22, 0x10),
            PURPLE_FILL = new Color(0x7F, 0x77, 0xDD),

    // ----- CODE / SYSTEM COLORS ----- //
    PARAMETER = new Color(0x18, 0x1A, 0x1F),
            STAT_PARAMETER = new Color(0x3E, 0x44, 0x51),
            KEY = new Color(0x98, 0xC3, 0x79),
            EQUATOR = new Color(0xC6, 0x78, 0xDD),
            VALUE = new Color(0xAB, 0xB2, 0xBF),
            COMMENT = new Color(0x5C, 0x63, 0x70),
            EMPTY = new Color(0x1E, 0x21, 0x28);
}