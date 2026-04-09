package __main;

import _global_list.Resource;
import campaign_creator_menu.ColoredTxtDisplay;
import combat_menu.popup.FileGetter;
import format.ColorStyles;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class UploadMain extends JDialog {

    private static final Color BG_MAIN = new Color(0x1E, 0x21, 0x28);
    private static final Color BG_SIDEBAR = new Color(0x19, 0x1C, 0x22);
    private static final Color BG_FIELD = new Color(0x25, 0x28, 0x30);
    private static final Color BORDER = new Color(0x2A, 0x2E, 0x3A);
    private static final Color BORDER_BTN = new Color(0x3A, 0x3E, 0x4A);
    private static final Color FG_HINT = new Color(0x50, 0x55, 0x68);
    private static final Color COLOR_OK = new Color(0x1D, 0x9E, 0x75);
    private static final Color COLOR_OK_FG = new Color(0x5D, 0xCA, 0xA5);
    private static final Color COLOR_ERR = new Color(0xE2, 0x4B, 0x4A);
    private static final Color COLOR_NONE = new Color(0x50, 0x55, 0x68);

    private static final String INSTRUCTIONS =
            "Select a file from the options below. The native file includes " +
                    "starter code for the Kyreun Campaign and an example Orc scenario.";

    private JButton combatButton;
    private JButton creatorButton;
    private JPanel accentStrip;
    private JLabel statusDot;
    private JLabel statusText;
    private JScrollPane scrollPane;
    private ColoredTxtDisplay codeDisplay;
    private JTextArea fallbackDisplay;

    private URL currentFile = null;

    private UploadMain() {
        setTitle("Campaign File Selection");
        setModal(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(860, 500));
        setIconImage(Main.getImage());
        setBackground(BG_MAIN);

        setLayout(new BorderLayout());
        add(buildSidebar(), BorderLayout.WEST);
        add(buildPreview(), BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(BG_SIDEBAR);
        sidebar.setPreferredSize(new Dimension(300, 0));
        sidebar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER),
                new EmptyBorder(22, 20, 18, 20)));

        JLabel icon = new JLabel(Main.getIcon());
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
        sidebar.add(uploadButton("Load Kyreun starter", () -> logNewInput(Resource.STARTER_CODE.url())));

        sidebar.add(Box.createVerticalGlue());

        sidebar.add(sectionLabel("Run mode"));
        sidebar.add(vgap(8));

        combatButton = runButton("Start", true);
        creatorButton = runButton("Edit", false);
        combatButton.setEnabled(false);
        creatorButton.setEnabled(false);

        combatButton.addActionListener(e -> {
            Main.runCombatEncounter(currentFile);
            dispose();
        });
        creatorButton.addActionListener(e -> {
            Main.runCampaignCreator(currentFile);
            dispose();
        });

        JPanel runRow = new JPanel(new GridLayout(1, 2, 6, 0));
        runRow.setOpaque(false);
        runRow.setAlignmentX(LEFT_ALIGNMENT);
        runRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        runRow.add(combatButton);
        runRow.add(creatorButton);
        sidebar.add(runRow);

        return sidebar;
    }

    private JPanel buildPreview() {
        JPanel preview = new JPanel(new BorderLayout());
        preview.setBackground(BG_MAIN);

        accentStrip = new JPanel();
        accentStrip.setPreferredSize(new Dimension(0, 2));
        accentStrip.setBackground(COLOR_NONE);
        preview.add(accentStrip, BorderLayout.NORTH);

        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        header.setBackground(BG_SIDEBAR);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));

        statusDot = new JLabel();
        statusDot.setPreferredSize(new Dimension(8, 8));
        statusDot.setOpaque(true);
        statusDot.setBackground(COLOR_NONE);
        statusDot.setBorder(BorderFactory.createLineBorder(COLOR_NONE, 4));

        statusText = new JLabel("No file selected");
        statusText.setFont(statusText.getFont().deriveFont(Font.PLAIN, 12f));
        statusText.setForeground(ColorStyles.TEXT_MUTED);

        header.add(statusDot);
        header.add(statusText);
        preview.add(header, BorderLayout.NORTH);

        JPanel northWrap = new JPanel(new BorderLayout());
        northWrap.setOpaque(false);
        northWrap.add(accentStrip, BorderLayout.NORTH);
        northWrap.add(header, BorderLayout.CENTER);
        preview.add(northWrap, BorderLayout.NORTH);

        codeDisplay = new ColoredTxtDisplay(null);
        fallbackDisplay = new JTextArea();
        fallbackDisplay.setEditable(false);
        fallbackDisplay.setBackground(BG_MAIN);
        fallbackDisplay.setForeground(COLOR_ERR);
        fallbackDisplay.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        fallbackDisplay.setBorder(new EmptyBorder(12, 14, 12, 14));

        scrollPane = new JScrollPane(buildEmptyState());
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(BG_MAIN);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        preview.add(scrollPane, BorderLayout.CENTER);

        return preview;
    }

    private static Component vgap(int h) {
        return Box.createRigidArea(new Dimension(0, h));
    }

    @NotNull
    private static JTextArea instructionsArea() {
        JTextArea instructions = new JTextArea(INSTRUCTIONS);
        instructions.setFont(instructions.getFont().deriveFont(Font.PLAIN, 12f));
        instructions.setForeground(ColorStyles.TEXT_MUTED);
        instructions.setBackground(BG_SIDEBAR);
        instructions.setEditable(false);
        instructions.setFocusable(false);
        instructions.setLineWrap(true);
        instructions.setWrapStyleWord(true);
        instructions.setAlignmentX(LEFT_ALIGNMENT);
        instructions.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        return instructions;
    }

    private static JLabel sectionLabel(String text) {
        JLabel l = new JLabel(text.toUpperCase());
        l.setFont(l.getFont().deriveFont(Font.PLAIN, 10f));
        l.setForeground(FG_HINT);
        l.setAlignmentX(LEFT_ALIGNMENT);
        l.setBorder(new EmptyBorder(0, 0, 0, 0));
        return l;
    }

    private JButton uploadButton(String label, Runnable action) {
        JButton btn = new JButton(label);
        btn.setFont(btn.getFont().deriveFont(Font.PLAIN, 12f));
        btn.setBackground(BG_FIELD);
        btn.setForeground(ColorStyles.TEXT_PRIMARY);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_BTN, 1),
                new EmptyBorder(7, 12, 7, 12)));
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        btn.addActionListener(e -> action.run());

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(0x2E, 0x32, 0x40));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(BG_FIELD);
            }
        });
        return btn;
    }

    private void logNewInput(URL input) {
        this.currentFile = input;

        if (currentFile == null) {
            showNewCampaignState();
            return;
        }

        boolean valid = txt_input.Reader5e.fileCompiles(currentFile);
        updateAccent(valid ? COLOR_OK : COLOR_ERR);
        updateStatus(valid);
        combatButton.setEnabled(valid);
        creatorButton.setEnabled(true);
        previewFileContent(valid);
    }

    private JButton runButton(String label, boolean isPrimary) {
        JButton btn = new JButton(label);
        btn.setFont(btn.getFont().deriveFont(Font.PLAIN, 12f));
        btn.setBackground(isPrimary ? COLOR_OK : BG_FIELD);
        btn.setForeground(isPrimary ? new Color(0xD8, 0xF4, 0xEC) : ColorStyles.TEXT_PRIMARY);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(isPrimary ? COLOR_OK : BORDER_BTN, 1),
                new EmptyBorder(7, 10, 7, 10)));
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setHorizontalAlignment(SwingConstants.CENTER);
        return btn;
    }

    private JLabel buildEmptyState() {
        JLabel lbl = new JLabel("No file loaded — select an option from the left", SwingConstants.CENTER);
        lbl.setFont(lbl.getFont().deriveFont(Font.PLAIN, 13f));
        lbl.setForeground(FG_HINT);
        return lbl;
    }

    private void showNewCampaignState() {
        updateAccent(ColorStyles.NPC);
        statusDot.setBackground(ColorStyles.NPC);
        statusDot.setBorder(BorderFactory.createLineBorder(ColorStyles.NPC, 4));
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
        Color dotColor = valid ? COLOR_OK : COLOR_ERR;
        Color fgColor = valid ? COLOR_OK_FG : COLOR_ERR;
        String text = valid ? "✔  Valid configuration found"
                : "✘  Syntax error — ensure formatting matches current version";

        statusDot.setBackground(dotColor);
        statusDot.setBorder(BorderFactory.createLineBorder(dotColor, 4));
        statusText.setForeground(fgColor);
        statusText.setText(text);
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
        JLabel l = new JLabel("A new campaign file will be generated on save.", SwingConstants.CENTER);
        l.setFont(l.getFont().deriveFont(Font.PLAIN, 13f));
        l.setForeground(ColorStyles.TEXT_MUTED);
        return l;
    }

    public static void showNewInstance() {
        SwingUtilities.invokeLater(() -> new UploadMain().setVisible(true));
    }
}