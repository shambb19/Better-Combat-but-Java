package __main;

import _global_list.Resource;
import combat_menu.encounter_selection.EncounterSelectionPanel;
import creator_menu.CampaignCreatorMenu;
import creator_menu.ide.CampaignEditor;
import input.CampaignReader;
import lombok.*;
import lombok.experimental.*;
import org.jetbrains.annotations.NotNull;
import swing.popup.FileGetter;
import swing.custom.MainFrame;
import swing.builder.ButtonBuilder;
import swing.builder.ComponentBuilder;
import swing.builder.TextItemBuilder;
import util.Message;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static swing.ColorStyles.*;
import static swing.builder.PanelBuilder.*;
import static swing.builder.Components.*;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class UploadMain extends MainFrame {

    private static final String INSTRUCTIONS = "Upload a file and select a run mode. " +
            "The Kryeun starter includes the Kryeun campaign party and an example orc enemy and scenario. " +
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

        modifyingPanel(this).layoutType(BORDER)
                .hostedComponent(buildSidebar(), BorderLayout.WEST)
                .hostedComponent(buildPreview(), BorderLayout.CENTER)
                .background(BG_DARK)
                .build().validated();

        setVisible(true);
    }

    private JPanel buildSidebar() {
        sidebar = createPanel(VERTICAL_BOX).vgap(5).build().get();

        combatButton = uploadButton("Start", () -> {
            Main.uploadCampaign(currentFile);
            scrollPane.setViewportView(new EncounterSelectionPanel(this));
            for (Component c : sidebar.getComponents())
                c.setEnabled(c instanceof JLabel);
        });
        combatButton.setEnabled(false);

        creatorButton = uploadButton("Edit", () -> {
            Main.uploadCampaign(currentFile);
            Main.closeAndSwitch(this, new CampaignCreatorMenu());
        });
        creatorButton.setEnabled(false);

        addConstraintlessComponentsTo(sidebar,
                new JLabel(Main.getAppIcon()),
                spacer(0, 13),
                instructionsArea(),
                spacer(0, 13),
                sectionLabel("Upload Options"), spacer(0, 3),
                uploadButton("New Campaign", () -> onInputChange(null)),
                uploadButton("Upload Existing (.txt)", () -> onInputChange(FileGetter.getUrl(this))),
                uploadButton("Load Kryeun Starter", () -> onInputChange(Resource.STARTER_STANDARD.getUrl())),
                uploadButton("Load Steampunk Starter", () -> onInputChange(Resource.STARTER_STEAMPUNK.getUrl())),
                spacer(0, 7),
                sectionLabel("Run mode"), spacer(0, 3),
                combatButton, creatorButton
        );
        return modifying(sidebar)
                .background(BG_DARK)
                .preferredSize(new Dimension(300, 0))
                .border(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 0, 2, TRACK),
                        BorderFactory.createEmptyBorder(22, 20, 18, 18)
                )).build().get();
    }

    @NotNull
    private static JTextArea instructionsArea() {
        return TextItemBuilder.<JTextArea>buildTextItem().component(new JTextArea(INSTRUCTIONS))
                .fontBuilder(ComponentBuilder.FontBuilder.of(Font.PLAIN, 13f, FG_MUTED))
                .alignmentX(LEFT_ALIGNMENT)
                .maximumSize(new Dimension(Integer.MAX_VALUE, 80)).build().get();
    }

    private static JLabel sectionLabel(String text) {
        return ComponentBuilder.createLabel(text.toUpperCase(), Font.PLAIN, 10f, FG_HINT)
                .alignmentX(LEFT_ALIGNMENT).build().get();
    }

    private JButton uploadButton(String label, Runnable action) {
        return ButtonBuilder.createButton(label, BG_SURFACE, action)
                .onMouseEnter(b -> b.setBackground(DIVIDER))
                .onMouseExit(b -> b.setBackground(BG_SURFACE))
                .background(BG_SURFACE)
                .alignmentX(LEFT_ALIGNMENT)
                .maximumSize(new Dimension(221, 34)).build().get();
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
        statusDot.setBorder(BorderFactory.createLineBorder(background, 4));

        statusText.setText(text);
        statusText.setForeground(foreground);

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
        final String text = "A new campaign file will be generated on save.";
        return ComponentBuilder.getBasicLabel(text, Font.PLAIN, 13f, FG_MUTED);
    }

    private JPanel buildPreview() {
        JPanel preview = createPanel(BORDER)
                .background(BG_DARK)
                .build().get();

        accentStrip = createPanel(FLOW)
                .preferredSize(new Dimension(0, 2))
                .background(FG_HINT).build().get();

        JPanel header = createPanel(FLOW_LEFT).hgap(10).vgap(8)
                .background(BG_DARK).build().get();

        statusDot = ComponentBuilder.modifying(new JLabel())
                .preferredSize(new Dimension(8, 8))
                .background(FG_HINT)
                .build().putIn(header);

        statusText = ComponentBuilder.createLabel("No file selected", Font.PLAIN, 12f, FG_MUTED)
                .build().putIn(header);

        createPanel(BORDER)
                .hostedComponent(accentStrip, BorderLayout.NORTH)
                .hostedComponent(header, BorderLayout.CENTER)
                .build().putIn(preview, BorderLayout.NORTH);

        codeDisplay = new CampaignEditor();

        fallbackDisplay = TextItemBuilder.createTextArea("", CRITICAL)
                .border(BorderFactory.createEmptyBorder(12, 14, 12, 14)).build().get();

        scrollPane = TextItemBuilder.createTextArea("", FG_HINT)
                .fontBuilder(FontBuilder.of(Font.PLAIN, 20f))
                .build().getAsScrollerIn(preview, BorderLayout.CENTER);
        scrollPane.setBackground(BG_DARK);

        return preview;
    }

}