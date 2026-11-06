package creator_menu;

import __main.Main;
import creator_menu.ide.CampaignEditor;
import input.CampaignReader;
import lombok.*;
import lombok.experimental.*;
import swing.custom.MainFrame;
import swing.builder.PanelBuilder;

import java.awt.*;
import java.util.List;

@Getter @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@ExtensionMethod(util.StringUtil.class)
public class CampaignCreatorMenu extends MainFrame {

    public static final String TITLE = "Campaign Creator" + Main.TITLE;

    CampaignEditor editorPanel;

    public CampaignCreatorMenu() {
        setTitle(TITLE);

        editorPanel = new CampaignEditor();

        final String errorMessage = "CampaignCreatorMenu.<init>: could not read input file";
        List<String> lines = CampaignReader.getLines(Main.getFile(), errorMessage);
        String text = String.join("\n", lines);
        editorPanel.setText(text);

        PanelBuilder.modifyingPanel(this).layoutType(PanelBuilder.BORDER)
                .hostedComponent(editorPanel, BorderLayout.CENTER)
                .hostedComponent(new HelpMenu(), BorderLayout.EAST)
                .hostedComponent(new CampaignCreatorFooterBar(), BorderLayout.SOUTH).build().validated();

        setVisible(true);
    }

}
