package creator_menu;

import __main.Main;
import lombok.*;
import swing.builder.ButtonBuilder;
import util.Message;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;

import static swing.ColorStyles.*;
import static swing.builder.PanelBuilder.*;

public class CampaignCreatorFooterBar extends JPanel {

    {
        JButton copy = ButtonBuilder.createButton("Copy to Clipboard", SUCCESS, this::clipboardCopy).build().get();
        JButton download = ButtonBuilder.createButton("Download .txt File", SUCCESS, this::download).build().get();

        modifyingPanel(this).layoutType(FLOW).hgap(15).background(BG_DEEP).build().validated();
        addConstraintlessComponentsTo(this, copy, download);
    }

    private void clipboardCopy() {
        String text = Main.getCreatorMenu().getEditorPanel().getText();
        StringSelection selection = new StringSelection(text);

        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
        Message.showAsInfoMessage("Code copied to clipboard.");
    }

    private void download() {
        File directory = new File(System.getProperty("user.home"), "Downloads");

        String fileName = Message.promptString("Enter unique file name.", "File Name");

        File file = new File(directory, fileName + " " + LocalDate.now() + ".txt");

        try {
            @Cleanup FileWriter writer = new FileWriter(file);
            Main.getCreatorMenu().getEditorPanel().write(writer);
        } catch (IOException e) {
            Message.showFileErrorMessage(e, Message.WRITE_ERROR);
        }
    }

}
