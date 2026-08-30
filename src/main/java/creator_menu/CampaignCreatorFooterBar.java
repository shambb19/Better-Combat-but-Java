package creator_menu;

import __main.Main;
import lombok.*;
import util.Message;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;

import static swing.ColorStyles.BG_DEEP;
import static swing.ColorStyles.SUCCESS;
import static swing.fluent.SwingComp.button;
import static swing.fluent.SwingPane.FLOW;
import static swing.fluent.SwingPane.fluent;

public class CampaignCreatorFooterBar extends JPanel {

    {
        fluent(this).arrangedAs(FLOW, 15, 0).collect(
                button("Copy to Clipboard", SUCCESS, this::clipboardCopy),
                button("Download .txt File", SUCCESS, this::download)
        ).withBackground(BG_DEEP);
    }

    private void clipboardCopy() {
        String text = Main.getCreatorMenu().getEditorPanel().getText();
        StringSelection selection = new StringSelection(text);

        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
        Message.showAsInfoMessage("Code copied to clipboard.");
    }

    private void download() {
        File directory = new File(System.getProperty("user.home"), "Downloads");

        String fileName = Message.promptString("Enter unique file name.");

        File file = new File(directory, fileName + " " + LocalDate.now() + ".txt");

        try {
            @Cleanup FileWriter writer = new FileWriter(file);
            Main.getCreatorMenu().getEditorPanel().write(writer);
        } catch (IOException e) {
            Message.showFileErrorMessage(e, Message.WRITE_ERROR);
        }
    }

}
