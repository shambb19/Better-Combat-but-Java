package creator_menu;

import __main.Main;
import ide.CampaignEditor;
import input.CampaignReader;
import lombok.*;
import lombok.experimental.*;
import swing.custom.MainFrame;
import util.Message;

import java.io.IOException;

import static swing.fluent.SwingPane.*;

@Getter @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@ExtensionMethod(util.StringUtil.class)
public class CampaignCreatorMenu extends MainFrame {

    public static final String TITLE = "Campaign Creator" + Main.TITLE;

    CampaignEditor editorPanel;

    public CampaignCreatorMenu() {
        setTitle(TITLE);

        editorPanel = new CampaignEditor();
        try {
            String text = CampaignReader.getLines(Main.getFile()).fromList(false);
            editorPanel.setText(text);
        } catch (IOException e) {
            Message.showFileErrorMessage(e, Message.READ_ERROR);
            throw new RuntimeException("CampaignCreatorMenu.<init>: could not read input file");
        }

        fluent(this).arrangedAs(BORDER).borderCollect(
                center(editorPanel),
                east(new HelpMenu()),
                south(new CampaignCreatorFooterBar())
        );

        setVisible(true);
    }

}
