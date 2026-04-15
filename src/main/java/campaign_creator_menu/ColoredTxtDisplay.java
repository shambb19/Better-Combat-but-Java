package campaign_creator_menu;

import util.TxtReader;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static format.ColorStyles.*;
import static util.TxtReader.*;

public class ColoredTxtDisplay extends JTextPane {

    private ArrayList<String> lines;

    public ColoredTxtDisplay(ArrayList<String> lines) {
        this.lines = lines;

        setBorder(new EmptyBorder(new Insets(10, 10, 10, 10)));
        setMargin(new Insets(5, 5, 5, 5));

        if (lines != null)
            addLines();
    }

    public void setLines(List<String> lines) {
        this.lines = new ArrayList<>(lines);
        this.lines.replaceAll(TxtReader::withoutComments);
        addLines();
    }

    public void addLines() {
        setText("");

        for (String line : lines) {
            Color lineType = getLineType(line);

            if (lineType.equals(PARAMETER))
                appendParameter(line, "\n");
            else if (lineType.equals(STAT_PARAMETER))
                appendStatsParameter(line);
            else
                appendToPane(line + "\n", lineType);
        }
    }

    private void appendStatsParameter(String line) {
        appendToPane("stats", KEY);
        appendToPane(": ", EQUATOR);
        appendToPane("[", VALUE);

        String[] vals = listTextAsArray(line);
        for (int i = 0; i < vals.length; i++) {
            appendParameter(vals[i], "");

            if (i != vals.length - 1)
                appendToPane(", ", VALUE);
        }

        appendToPane("]", VALUE);
        appendToPane("\n", EMPTY);

    }

    private void appendToPane(String line, Color lineType) {
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet aSet = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, lineType);

        aSet = sc.addAttribute(aSet, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);

        int len = getDocument().getLength();
        setCaretPosition(len);
        setCharacterAttributes(aSet, false);
        replaceSelection(line);
    }

    private void appendParameter(String line, String end) {
        String key = key(line);
        String value = value(line);

        appendToPane(key, KEY);
        appendToPane(": ", EQUATOR);
        appendToPane(value + end, VALUE);
    }

    private Color getLineType(String line) {
        if (line.isBlank())
            return EMPTY;
        else if (line.startsWith("stats"))
            return STAT_PARAMETER;
        else if (line.startsWith("//") || line.startsWith("~") || line.startsWith("#"))
            return COMMENT;

        return switch (line) {
            case ".party" -> PARTY;
            case ".npc" -> ALLY;
            case ".enemy" -> ENEMY;
            case ".scenario" -> SCENARIO;
            default -> PARAMETER;
        };
    }

}
