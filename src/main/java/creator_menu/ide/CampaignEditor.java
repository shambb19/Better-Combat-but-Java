package creator_menu.ide;

import config.ruleset.Ruleset;
import lombok.*;
import org.fife.ui.rsyntaxtextarea.*;
import swing.ColorStyles;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static creator_menu.ide.CampaignToken.*;
import static swing.ColorStyles.*;

@Setter @Getter public class CampaignEditor extends RSyntaxTextArea {

    private Ruleset currentAppliedRuleset = Ruleset.STANDARD_RULESET;

    {
        setTemplatesEnabled(true);
        setCodeFoldingEnabled(true);

        setBackground(ColorStyles.BG_DEEP);
        setForeground(ColorStyles.VALUE);
        setCurrentLineHighlightColor(ColorStyles.BG_LOCKED);

        CodeTemplateManager codeTemplateManager = RSyntaxTextArea.getCodeTemplateManager();
        for (ItemType item : ItemType.values()) {
            codeTemplateManager.addTemplate(item.getCodeTemplate());
        }

        AbstractTokenMakerFactory tokenMakerFactory = (AbstractTokenMakerFactory) TokenMakerFactory.getDefaultInstance();
        tokenMakerFactory.putMapping("text/campaign-txt", CampaignTokenMaker.class.getName());
        setSyntaxEditingStyle("text/campaign-txt");

        setSyntaxSchemeStyle();
    }

    public void importText(List<String> lines) {
        StringBuilder text = new StringBuilder();
        for (String line : lines) {
            text.append(line).append("\n");
        }
        setText(text.toString());
        setCaretPosition(0);
    }

    private void setSyntaxSchemeStyle() {
        Style[] oldStyles = getSyntaxScheme().getStyles();
        Style[] newStyles = Arrays.copyOf(oldStyles, CampaignToken.TOKEN_COUNT);

        for (int i = oldStyles.length; i < newStyles.length; i++) {
            newStyles[i] = new Style();
        }

        SyntaxScheme schemeNew = new SyntaxScheme(true);
        schemeNew.setStyles(newStyles);

        Consumer<Map.Entry<Integer, Color>> setter = entry ->
                schemeNew.getStyle(entry.getKey()).foreground = entry.getValue();

        Map.of(
                CONFIG_TOKEN, CONFIG,
                HEADER_TOKEN, HEADER,
                TAG_TOKEN, CONFIG,
                NAME_TOKEN, KEY,
                KEY_TOKEN, KEY,
                KEY_OPTIONAL_TOKEN, KEY_OPTIONAL,
                VALUE_TOKEN, VALUE,
                COMMENT_TOKEN, COMMENT,
                UNEXPECTED_TOKEN, VALUE
        ).entrySet().forEach(setter);

        setSyntaxScheme(schemeNew);

        addParser(new CampaignParser());
    }

}
