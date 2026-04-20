package __main;

import _global_list.Resource;
import campaign_creator_menu.ColoredTxtDisplay;
import combat_menu.EncounterSelectionPanel;
import combat_menu.popup.FileGetter;
import format.swing_comp.SwingPane;
import input.Reader5e;
import lombok.*;
import lombok.experimental.*;
import org.jetbrains.annotations.NotNull;

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

import static format.ColorStyles.*;
import static format.swing_comp.SwingComp.fluent;
import static format.swing_comp.SwingComp.*;
import static format.swing_comp.SwingPane.*;

@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor(staticName = "newInstance", force = true)
public class UploadMain extends JFrame {

    private static final String INSTRUCTIONS =
            "Select a file from the options below. The native file includes " +
                    "starter code for the Kyreun Campaign and an example Orc scenario.";

    JPanel sidebar;
    JButton combatButton, creatorButton;
    JPanel accentStrip;
    JLabel statusDot, statusText;
    JScrollPane scrollPane;
    ColoredTxtDisplay codeDisplay;
    JTextArea fallbackDisplay;

    URL currentFile = null;

    {
        setTitle("Campaign File Selection" + Main.TITLE);
        setIconImage(__main.Main.getAppIcon().getImage());
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        setResizable(false);
        setBackground(BACKGROUND);

        SwingPane.fluent(this).arrangedAs(BORDER).borderCollect(
                west(buildSidebar()), center(buildPreview()));

        GraphicsConfiguration config = getGraphicsConfiguration();
        Rectangle bounds = config.getBounds();
        Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(config);

        int SHADOW = 8;

        int x = bounds.x + insets.left - SHADOW;
        int y = bounds.y + insets.top;
        int width = bounds.width - insets.left - insets.right + (SHADOW * 2);
        int height = bounds.height - insets.top - insets.bottom + SHADOW;

        setBounds(x, y, width, height);

        setVisible(true);
    }

    private JPanel buildSidebar() {
        sidebar = newArrangedAs(VERTICAL_BOX, 0, 5).component();

        combatButton = uploadButton("Start", () -> {
            Main.uploadCampaign(currentFile);
            scrollPane.setViewportView(EncounterSelectionPanel.newInstance(this));
            for (Component c : sidebar.getComponents())
                c.setEnabled(c instanceof JLabel);
        });
        combatButton.setEnabled(false);

        creatorButton = uploadButton("Edit", () -> {
            Main.uploadCampaign(currentFile);
            Main.closeUploadAndRun(Main.CREATOR, this);
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
                        uploadButton("Load Kyreun Starter", () -> onInputChange(Resource.STARTER_CODE.getUrl())),
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
        JButton button = button(label, BG_SURFACE, action)
                .applied(b -> b.setHorizontalAlignment(SwingConstants.LEFT))
                .onLeft()
                .withMaximumSize(221, 34)
                .component();

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(0x2E, 0x32, 0x40));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(BG_SURFACE);
            }
        });
        return button;
    }

    private void onInputChange(URL input) {
        currentFile = input;

        boolean valid = Reader5e.fileCompiles(currentFile);

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
                codeDisplay.setLines(lines);
                codeDisplay.setCaretPosition(0);
                scrollPane.setViewportView(codeDisplay);
            } else {
                fallbackDisplay.setText(String.join("\n", lines));
                fallbackDisplay.setCaretPosition(0);
                scrollPane.setViewportView(fallbackDisplay);
            }

        } catch (IOException e) {
            util.Message.showFileErrorMessage(e);
        }
    }

    private static JLabel centeredLabel() {
        return label("A new campaign file will be generated on save.", Font.PLAIN, 13f, FG_MUTED)
                .component();
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

        codeDisplay = new ColoredTxtDisplay(null);
        fallbackDisplay = textArea("")
                .withText(Font.PLAIN, 12f, CRITICAL)
                .withBackground(BACKGROUND)
                .withEmptyBorder(12, 14, 12, 14)
                .applied(f -> f.setEditable(false))
                .component();

        scrollPane = scrollPane(buildEmptyState()).withBorder(null)
                .applied(p -> p.getViewport().setBackground(BACKGROUND))
                .in(preview, BorderLayout.CENTER);

        return preview;
    }

    private JTextArea buildEmptyState() {
        return textArea("No file loaded; select an option from the left")
                .withText(Font.PLAIN, 20f, FG_HINT)
                .applied(a -> a.setAlignmentY(Component.CENTER_ALIGNMENT))
                .component();
    }

}