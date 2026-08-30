package __main;

import _global_list.Resource;
import combat_menu.encounter_selection.EncounterSelectionPanel;
import ide.CampaignEditor;
import input.CampaignReader;
import lombok.*;
import lombok.experimental.*;
import org.jetbrains.annotations.NotNull;
import popup.FileGetter;
import swing.custom.MainFrame;
import swing.fluent.SwingPane;
import util.Message;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static swing.ColorStyles.*;
import static swing.fluent.SwingComp.fluent;
import static swing.fluent.SwingComp.*;
import static swing.fluent.SwingPane.*;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class UploadMain extends MainFrame {

    private static final String INSTRUCTIONS = "Upload a file and select a run mode. " +
            "The Kyreun starter includes the Kyreun campaign party and an example orc enemy and scenario. " +
            "The Steampunk starter includes an example party, enemies, and scenarios to demonstrate syntax.";

    JPanel sidebar;
    JButton combatButton, creatorButton;
    JPanel accentStrip;
    JLabel statusDot, statusText;
    JScrollPane scrollPane;
    CampaignEditor codeDisplay;
    JTextArea fallbackDisplay;

    URL currentFile = null;

    {
        setTitle("Campaign File Selection" + Main.TITLE);

        SwingPane.fluent(this).arrangedAs(BORDER).borderCollect(
                west(buildSidebar()), center(buildPreview()));

        setVisible(true);
    }

    private JPanel buildSidebar() {
        sidebar = newArrangedAs(VERTICAL_BOX, 0, 5).component();

        combatButton = uploadButton("Start", () -> {
            Main.uploadCampaign(currentFile);
            scrollPane.setViewportView(new EncounterSelectionPanel(this));
            for (Component c : sidebar.getComponents())
                c.setEnabled(c instanceof JLabel);
        });
        combatButton.setEnabled(false);

        creatorButton = uploadButton("Edit", () -> {
            Main.uploadCampaign(currentFile);
            Main.closeAndSwitch(this, Main.CREATOR);
        });
        creatorButton.setEnabled(false);

        return SwingPane.fluent(sidebar)
                .collect(
                        new JLabel(Main.getAppIcon()),
                        spacer(0, 13),
                        instructionsArea(),
                        spacer(0, 13),
                        sectionLabel("Upload Options"), spacer(0, 3),
                        uploadButton("New Campaign", () -> onInputChange(null)),
                        uploadButton("Upload Existing (.txt)", () -> onInputChange(FileGetter.getUrl(this))),
                        uploadButton("Load Kyreun Starter", () -> onInputChange(Resource.STARTER_STANDARD.getUrl())),
                        uploadButton("Load Steampunk Starter", () -> onInputChange(Resource.STARTER_STEAMPUNK.getUrl())),
                        spacer(0, 7),
                        sectionLabel("Run mode"), spacer(0, 3),
                        combatButton, creatorButton
                ).withBackground(BG_DARK)
                .withPreferredSize(300, 0)
                .withEmptyBorder(22, 20, 18, 20)
                .component();
    }

    @NotNull
    private static JTextArea instructionsArea() {
        return textArea(INSTRUCTIONS)
                .withText(Font.PLAIN, 13f, FG_MUTED)
                .onLeft()
                .withMaximumSize(Integer.MAX_VALUE, 80).component();
    }

    private static JLabel sectionLabel(String text) {
        return label(text.toUpperCase(), Font.PLAIN, 10f, FG_HINT).onLeft().component();
    }

    private JButton uploadButton(String label, Runnable action) {
        return button(label, BG_SURFACE, action)
                .withMouseMoveListener(
                        b -> b.setBackground(DIVIDER),
                        b -> b.setBackground(BG_SURFACE)
                ).applied(b -> b.setHorizontalAlignment(SwingConstants.LEFT))
                .onLeft()
                .withMaximumSize(221, 34)
                .component();
    }

    private void onInputChange(URL input) {
        currentFile = input;

        boolean valid = CampaignReader.fileCompiles(currentFile);

        Color background, foreground;
        String text;
        if (currentFile == null) {
            background = FRIENDLY;
            foreground = FG_MUTED;
            text = "Mode: New Campaign";
            scrollPane.setViewportView(centeredLabel());
        } else if (valid) {
            background = SUCCESS;
            foreground = HEALTHY;
            text = "✔  Valid Configuration Found";
            previewFileContent(true);
        } else {
            background = CRITICAL;
            foreground = CRITICAL;
            text = "✘  Syntax error — ensure formatting matches current version";
            previewFileContent(false);
        }
        accentStrip.setBackground(background);
        statusDot.setBackground(background);
        statusDot.setBorder(new LineBorder(background, 4));

        statusText.setText(text);
        statusText.setForeground(foreground);

        fluent(statusDot)
                .withBackgroundAndForeground(background, foreground)
                .withBorder(new LineBorder(background, 4))
                .applied(d -> d.setText(text));

        combatButton.setEnabled(valid);
        creatorButton.setEnabled(valid || currentFile == null);
        previewFileContent(valid);
    }

    private void previewFileContent(boolean valid) {
        if (currentFile == null) return;

        try (InputStream is = currentFile.openStream();
             BufferedReader reader = new BufferedReader(
                     new InputStreamReader(is, StandardCharsets.UTF_8))) {

            List<String> lines = reader.lines().toList();

            if (valid) {
                codeDisplay.importText(lines);
                codeDisplay.setCaretPosition(0);
                scrollPane.setViewportView(codeDisplay);
            } else {
                fallbackDisplay.setText(String.join("\n", lines));
                fallbackDisplay.setCaretPosition(0);
                scrollPane.setViewportView(fallbackDisplay);
            }

        } catch (IOException e) {
            util.Message.showFileErrorMessage(e, Message.READ_ERROR);
        }
    }

    private static JLabel centeredLabel() {
        return label("A new campaign file will be generated on save.", 13f).muted().component();
    }

    private JPanel buildPreview() {
        JPanel preview = newArrangedAs(BORDER).withBackground(BACKGROUND).component();

        accentStrip = panelIn(preview, BorderLayout.NORTH)
                .withPreferredSize(0, 2)
                .withBackground(FG_HINT)
                .component();

        JPanel header = panelIn(preview, BorderLayout.NORTH).arrangedAs(FLOW_LEFT, 10, 8)
                .withBackground(BG_DARK)
                .withPaddedMatteBorderOnSide(TRACK, BOTTOM, 0, 0, 0, 0)
                .component();

        statusDot = label(null).opaque()
                .withPreferredSize(8, 8)
                .withBackground(FG_HINT)
                .withBorder(new LineBorder(FG_HINT, 4))
                .in(header);

        statusText = label("No file selected", FG_MUTED).in(header);

        panelIn(preview, BorderLayout.NORTH).arrangedAs(BORDER)
                .borderCollect(
                        north(accentStrip), center(header)
                ).transparent()
                .transparent();

        codeDisplay = new CampaignEditor();
        codeDisplay.setFocusable(false);
        fallbackDisplay = textArea("")
                .withText(Font.PLAIN, 12f, CRITICAL)
                .withBackground(BACKGROUND)
                .withEmptyBorder(12, 14, 12, 14)
                .applied(f -> f.setEditable(false))
                .component();

        scrollPane = textArea("No file loaded; select an option from the left")
                .withText(Font.PLAIN, 20f, FG_HINT)
                .applied(a -> a.setAlignmentY(Component.CENTER_ALIGNMENT))
                .toScroller()
                .in(preview, BorderLayout.CENTER);

        return preview;
    }

}