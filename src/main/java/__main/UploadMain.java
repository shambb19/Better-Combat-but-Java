package __main;

import _global_list.Resource;
import campaign_creator_menu.ColoredTxtDisplay;
import combat_menu.popup.FileGetter;
import format.ColorStyles;
import lombok.*;
import lombok.experimental.*;
import org.jetbrains.annotations.NotNull;
import swing.swing_comp.SwingComp;
import swing.swing_comp.SwingPane;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor(staticName = "newInstance", force = true)
public class UploadMain extends JFrame {

    private static final String INSTRUCTIONS =
            "Select a file from the options below. The native file includes " +
                    "starter code for the Kyreun Campaign and an example Orc scenario.";

    JButton combatButton, creatorButton;
    JPanel accentStrip;
    JLabel statusDot, statusText;
    JScrollPane scrollPane;
    ColoredTxtDisplay codeDisplay;
    JTextArea fallbackDisplay;

    URL currentFile = null;

    {
        setTitle("Campaign File Selection" + Main.TITLE);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(860, 500));
        setIconImage(Main.getAppIcon().getImage());
        setBackground(ColorStyles.BACKGROUND);

        SwingPane.modifiable(this).withLayout(SwingPane.BORDER)
                .with(buildSidebar(), BorderLayout.WEST)
                .with(buildPreview(), BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }

    private JPanel buildSidebar() {
        JPanel sidebar = SwingPane.panel().withLayout(SwingPane.VERTICAL_BOX)
                .withBackground(ColorStyles.BG_DARK)
                .withPreferredSize(300, 0)
                .withPaddedMatteBorderOnSide(ColorStyles.TRACK, SwingComp.RIGHT, 22, 20, 18, 20)
                .component();

        JLabel icon = new JLabel(Main.getAppIcon());
        icon.setAlignmentX(CENTER_ALIGNMENT);
        sidebar.add(icon);
        sidebar.add(vgap(18));

        JTextArea instructions = instructionsArea();
        sidebar.add(instructions);
        sidebar.add(vgap(18));

        sidebar.add(sectionLabel("Upload options"));
        sidebar.add(vgap(8));
        sidebar.add(uploadButton("New campaign", () -> logNewInput(null)));
        sidebar.add(vgap(5));
        sidebar.add(uploadButton("Upload existing (.txt)", () -> logNewInput(FileGetter.getUrl(this))));
        sidebar.add(vgap(5));
        sidebar.add(uploadButton("Load Kyreun starter", () -> logNewInput(Resource.STARTER_CODE.getUrl())));

        sidebar.add(Box.createVerticalGlue());

        sidebar.add(sectionLabel("Run mode"));
        sidebar.add(vgap(8));

        combatButton = runButton("Start", true, () -> {
            Main.runCombatEncounter(currentFile);
            dispose();
        });
        creatorButton = runButton("Edit", false, () -> {
            Main.runCampaignCreator(currentFile);
            dispose();
        });

        SwingPane.panelIn(sidebar).withLayout(SwingPane.TWO_COLUMN)
                .collect(combatButton, creatorButton)
                .withGaps(6, 0)
                .onLeft()
                .withMaximumSize(Integer.MAX_VALUE, 36);

        return sidebar;
    }

    private JPanel buildPreview() {
        JPanel preview = SwingPane.panel().withLayout(SwingPane.BORDER).withBackground(ColorStyles.BACKGROUND).component();

        accentStrip = SwingPane.panelIn(preview, BorderLayout.NORTH)
                .withPreferredSize(0, 2)
                .withBackground(ColorStyles.FG_HINT)
                .component();

        JPanel header = SwingPane.panelIn(preview, BorderLayout.NORTH).withLayout(SwingPane.FLOW_LEFT)
                .withGaps(10, 8)
                .withBackground(ColorStyles.BG_DARK)
                .withBorder(new MatteBorder(0, 0, 1, 0, ColorStyles.TRACK))
                .component();

        statusDot = SwingComp.label().opaque()
                .withPreferredSize(8, 8)
                .withBackground(ColorStyles.FG_HINT)
                .withBorder(new LineBorder(ColorStyles.FG_HINT, 4))
                .in(header)
                .component();

        statusText = SwingComp.label("No file selected")
                .asStandardTextSize()
                .withForeground(ColorStyles.TEXT_MUTED)
                .in(header)
                .component();

        SwingPane.panelIn(preview, BorderLayout.NORTH).withLayout(SwingPane.BORDER)
                .with(accentStrip, BorderLayout.NORTH)
                .with(header, BorderLayout.CENTER)
                .transparent();

        codeDisplay = new ColoredTxtDisplay(null);
        fallbackDisplay = SwingComp.textArea("").asStandardTextSize()
                .withBackgroundAndForeground(ColorStyles.BACKGROUND, ColorStyles.CRITICAL)
                .withEmptyBorder(12, 14, 12, 14)
                .applied(f -> f.setEditable(false))
                .component();

        scrollPane = SwingComp.scrollPane(buildEmptyState()).withBorder(null)
                .applied(p -> p.getViewport().setBackground(ColorStyles.BACKGROUND))
                .in(preview, BorderLayout.CENTER)
                .component();

        return preview;
    }

    private static Component vgap(int h) {
        return Box.createRigidArea(new Dimension(0, h));
    }

    @NotNull
    private static JTextArea instructionsArea() {
        return SwingComp.textArea(INSTRUCTIONS)
                .asStandardTextSize()
                .withBackgroundAndForeground(ColorStyles.BG_DARK, ColorStyles.TEXT_MUTED)
                .onLeft()
                .withMaximumSize(Integer.MAX_VALUE, 80).component();
    }

    private static JLabel sectionLabel(String text) {
        return SwingComp.label(text.toUpperCase())
                .withDerivedFont(Font.PLAIN, 10f)
                .withForeground(ColorStyles.FG_HINT)
                .onLeft()
                .component();
    }

    private JButton uploadButton(String label, Runnable action) {
        JButton button = SwingComp.button(label, action).asStandardTextSize()
                .withBackgroundAndForeground(ColorStyles.BG_SURFACE, ColorStyles.TEXT_PRIMARY)
                .withPaddedBorder(new LineBorder(ColorStyles.BORDER_LIGHT, 1), 7, 12, 7, 12)
                .applied(b -> b.setHorizontalAlignment(SwingConstants.LEFT))
                .onLeft()
                .withMaximumSize(Integer.MAX_VALUE, 34)
                .component();

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(0x2E, 0x32, 0x40));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(ColorStyles.BG_SURFACE);
            }
        });
        return button;
    }

    private void logNewInput(URL input) {
        this.currentFile = input;

        if (currentFile == null) {
            showNewCampaignState();
            return;
        }

        boolean valid = txt_input.Reader5e.fileCompiles(currentFile);
        updateAccent(valid ? ColorStyles.SUCCESS : ColorStyles.CRITICAL);
        updateStatus(valid);
        combatButton.setEnabled(valid);
        creatorButton.setEnabled(true);
        previewFileContent(valid);
    }

    private JButton runButton(String label, boolean isPrimary, Runnable onClick) {
        Color bg, fg, border;
        if (isPrimary) {
            bg = ColorStyles.SUCCESS;
            fg = ColorStyles.TEXT_PRIMARY;
            border = ColorStyles.SUCCESS;
        } else {
            bg = ColorStyles.BG_SURFACE;
            fg = ColorStyles.TEXT_PRIMARY;
            border = ColorStyles.BORDER_LIGHT;
        }

        return SwingComp.button(label, onClick).asStandardTextSize()
                .withBackgroundAndForeground(bg, fg)
                .withPaddedBorder(new LineBorder(border, 1), 7, 10, 7, 10)
                .centered()
                .disabled()
                .component();
    }

    private JLabel buildEmptyState() {
        return SwingComp.label("No file loaded; select an option from the left").withDerivedFont(Font.PLAIN, 13f)
                .centered()
                .withForeground(ColorStyles.FG_HINT).component();
    }

    private void showNewCampaignState() {
        updateAccent(ColorStyles.ALLY);
        statusDot.setBackground(ColorStyles.ALLY);
        statusDot.setBorder(BorderFactory.createLineBorder(ColorStyles.ALLY, 4));
        statusText.setText("Mode: new campaign");
        statusText.setForeground(ColorStyles.TEXT_MUTED);
        scrollPane.setViewportView(
                centeredLabel());
        combatButton.setEnabled(false);
        creatorButton.setEnabled(true);
    }

    private void updateAccent(Color c) {
        accentStrip.setBackground(c);
        accentStrip.repaint();
    }

    private void updateStatus(boolean valid) {
        Color dotColor = valid ? ColorStyles.SUCCESS : ColorStyles.CRITICAL;
        Color fgColor = valid ? ColorStyles.HEALTHY : ColorStyles.CRITICAL;
        String text = valid ? "✔  Valid configuration found"
                : "✘  Syntax error — ensure formatting matches current version";

        SwingComp.modifiable(statusDot)
                .withBackgroundAndForeground(dotColor, fgColor)
                .withBorder(new LineBorder(dotColor, 4))
                .applied(d -> d.setText(text));
    }

    private void previewFileContent(boolean valid) {
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
            util.Message.fileError(this, e);
        }
    }

    private static JLabel centeredLabel() {
        return SwingComp.label("A new campaign file will be generated on save.")
                .withDerivedFont(Font.PLAIN, 13f)
                .withForeground(ColorStyles.TEXT_MUTED)
                .centered()
                .component();
    }

}