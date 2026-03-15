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
        PARTY_HEADER, NPC_HEADER, ENEMY_HEADER, SCENARIO_HEADER,
        PARAMETER, PARAMETER_STAT, KEY, EQUATOR, VALUE,
        COMMENT, EMPTY
    }

    private static final Map<LineType, Color> codeColors = Map.of(
            LineType.PARTY_HEADER, new Color(106, 188, 255),
            LineType.NPC_HEADER, new Color(137, 80, 245),
            LineType.ENEMY_HEADER, new Color(245, 64, 146),
            LineType.SCENARIO_HEADER, new Color(255, 237, 122),
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

                String[] vals = listTextAsArray(line);
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
        if (line.startsWith("//") || line.startsWith("~") || line.startsWith("#")) {
            return LineType.COMMENT;
        }
        return switch (line) {
            case ".party" -> LineType.PARTY_HEADER;
            case ".npc" -> LineType.NPC_HEADER;
            case ".enemy" -> LineType.ENEMY_HEADER;
            case ".scenario" -> LineType.SCENARIO_HEADER;
            default -> LineType.PARAMETER;
        };
    }

}
