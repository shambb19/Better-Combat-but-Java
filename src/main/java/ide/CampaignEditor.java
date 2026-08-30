package ide;

import boilerplate.FilteredVals;
import config.ruleset.Ruleset;
import lombok.*;
import org.fife.ui.rsyntaxtextarea.*;
import swing.ColorStyles;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

@Setter @Getter public class CampaignEditor extends RSyntaxTextArea {

    public static final List<String>
            TUTORIAL_TEXT = List.of(
            "~ Welcome to the Campaign IDE!",
            "~ Use the help menu on the right for help."
    );

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
        SyntaxScheme schemeOld = getSyntaxScheme();
        Style[] oldStyles = schemeOld.getStyles();
        Style[] newStyles = Arrays.copyOf(oldStyles, CampaignToken.TOKEN_COUNT);

        for (int i = oldStyles.length; i < newStyles.length; i++) {
            newStyles[i] = new Style();
        }

        SyntaxScheme schemeNew = new SyntaxScheme(true);
        schemeNew.setStyles(newStyles);

        interface StyleSetter {
            void setStyle(int token, @FilteredVals.Color Color color);
        }
        StyleSetter s = (int token, @FilteredVals.Color Color color) -> schemeNew.getStyle(token).foreground = color;

        s.setStyle(CampaignToken.CONFIG, ColorStyles.CONFIG);
        s.setStyle(CampaignToken.HEADER, ColorStyles.HEADER);
        s.setStyle(CampaignToken.TAG, ColorStyles.CONFIG);
        s.setStyle(CampaignToken.NAME, ColorStyles.KEY);
        s.setStyle(CampaignToken.KEY, ColorStyles.KEY);
        s.setStyle(CampaignToken.KEY_OPTIONAL, ColorStyles.KEY_OPTIONAL);
        s.setStyle(CampaignToken.VALUE, ColorStyles.VALUE);
        s.setStyle(CampaignToken.COMMENT, ColorStyles.COMMENT);
        s.setStyle(CampaignToken.UNEXPECTED, ColorStyles.VALUE);

        setSyntaxScheme(schemeNew);

        addParser(new CampaignParser());
    }

}
