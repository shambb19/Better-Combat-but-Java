package campaign_creator;

import combat_menu.listener.FieldEditListener;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.util.ArrayList;
import java.util.Map;

import static util.TxtReader.*;

public class ColoredTxtDisplay extends JTextPane {

    private enum LineType {
        SECTION_HEADER, COMPONENT_HEADER, ENEMY_HEADER,
        PARAMETER, PARAMETER_STAT, KEY, EQUATOR, VALUE,
        COMMENT, EMPTY
    }

    private static final Map<LineType, Color> codeColors = Map.of(
            LineType.SECTION_HEADER, new Color(255, 209, 106),
            LineType.COMPONENT_HEADER, new Color(106, 188, 255),
            LineType.ENEMY_HEADER, new Color(255, 84, 197),
            LineType.PARAMETER, Color.BLACK,
            LineType.KEY, new Color(122, 255, 106),
            LineType.EQUATOR, new Color(247, 155, 255),
            LineType.VALUE, Color.WHITE,
            LineType.COMMENT, Color.GRAY,
            LineType.EMPTY, Color.BLACK
    );

    private ArrayList<String> lines;

    public ColoredTxtDisplay(ArrayList<String> lines) {
        this.lines = lines;

        setBorder(new EmptyBorder(new Insets(10, 10, 10, 10)));
        setMargin(new Insets(5, 5, 5, 5));
        addKeyListener(new FieldEditListener(this));

        if (lines != null) {
            addLines();
        }
    }

    public void setLines(ArrayList<String> lines) {
        this.lines = lines;
        addLines();
    }

    public void addLines() {
        setText("");

        for (String line : lines) {
            LineType lineType = getLineType(line);

            if (lineType.equals(LineType.PARAMETER_STAT)) {
                appendToPane("stats", LineType.KEY);
                appendToPane(": ", LineType.EQUATOR);
                appendToPane("[", LineType.VALUE);

                String[] vals = stripped(line).split(", ");
                for (int i = 0; i < vals.length; i++) {
                    appendParameter(vals[i], "");

                    if (i != vals.length - 1) {
                        appendToPane(", ", LineType.VALUE);
                    }
                }

                appendToPane("]", LineType.VALUE);
                appendToPane("\n", LineType.EMPTY);

                continue;
            }

            if (lineType.equals(LineType.PARAMETER)) {
                appendParameter(line, "\n");
                continue;
            }

            appendToPane(line + "\n", lineType);
        }
    }

    private void appendToPane(String line, LineType lineType) {
        Color color = codeColors.get(lineType);

        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet aSet = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, color);

        aSet = sc.addAttribute(aSet, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);

        int len = getDocument().getLength();
        setCaretPosition(len);
        setCharacterAttributes(aSet, false);
        replaceSelection(line);
    }

    private void appendParameter(String line, String end) {
        String key = key(line);
        String value = value(line);

        appendToPane(key, LineType.KEY);
        appendToPane(": ", LineType.EQUATOR);
        appendToPane(value + end, LineType.VALUE);
    }

    private LineType getLineType(String line) {
        if (line.isBlank()) {
            return LineType.EMPTY;
        }
        if (line.startsWith("stats")) {
            return LineType.PARAMETER_STAT;
        }
        return switch (line.charAt(0)) {
            case '<' -> LineType.SECTION_HEADER;
            case '.' -> {
                if (line.startsWith(".enemy")) {
                    yield LineType.ENEMY_HEADER;
                } else {
                    yield LineType.COMPONENT_HEADER;
                }
            }
            case '/', '#', '~' -> LineType.COMMENT;
            default -> LineType.PARAMETER;
        };
    }

}
