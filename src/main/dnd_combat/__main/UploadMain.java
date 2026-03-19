package __main;

import campaign_creator_menu.ColoredTxtDisplay;
import combat_menu.popup.FileGetter;
import format.ColorStyle;
import format.SwingStyles;
import org.apache.commons.io.FileUtils;
import txt_input.Reader5e;
import util.Message;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;

public class UploadMain extends JDialog {

    private static final int PADDING = 20;
    private static final String INSTRUCTIONS = "Select a file from the options below. " +
            "The native file includes starter code for the Kyreun Campaign and an example Orc scenario.";

    private JButton combatButton, creatorButton;
    private JPanel displayPanel;
    private JLabel header;
    private JScrollPane scrollPane;
    private ColoredTxtDisplay codeDisplay;
    private JTextArea fallbackDisplay;

    private URL currentFile;

    public static void showNewInstance() {
        SwingUtilities.invokeLater(() -> new UploadMain().setVisible(true));
    }

    private UploadMain() {
        setTitle("Campaign File Selection");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(900, 500));
        setLayout(new GridLayout(1, 2));

        initActionPanel();
        initDisplayPanel();

        pack();
        setLocationRelativeTo(null);
    }

    private void initActionPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(PADDING, PADDING, PADDING, PADDING));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.weightx = 1.0;

        JTextPane instr = new JTextPane();
        instr.setText(INSTRUCTIONS);
        instr.setEditable(false);
        instr.setBackground(null);
        instr.setFont(new Font("SansSerif", Font.ITALIC, 12));
        panel.add(instr, gbc);

        panel.add(createSectionLabel("UPLOAD OPTIONS"), gbc);
        panel.add(SwingStyles.simpleButton("New Campaign", e -> logNewInput(null)), gbc);
        panel.add(SwingStyles.simpleButton("Upload Existing (.txt)", e -> logNewInput(FileGetter.getUrl())), gbc);
        panel.add(SwingStyles.simpleButton("Load Kyreun Starter", e -> logNewInput(getClass().getResource("/starter.txt"))), gbc);

        panel.add(createSectionLabel("RUN MODE"), gbc);
        combatButton = SwingStyles.simpleButton("Start Combat Encounter", e -> CombatMain.runWith(currentFile));
        creatorButton = SwingStyles.simpleButton("Open Campaign Creator", e -> CreatorMain.run(currentFile));

        panel.add(combatButton, gbc);
        panel.add(creatorButton, gbc);

        gbc.weighty = 1.0;
        panel.add(new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(0, 1000)), gbc);

        add(panel);
    }

    private void initDisplayPanel() {
        displayPanel = new JPanel(new BorderLayout());

        header = new JLabel("No File Selected", SwingConstants.CENTER);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 40));

        codeDisplay = new ColoredTxtDisplay(null);
        fallbackDisplay = new JTextArea();
        fallbackDisplay.setEditable(false);

        scrollPane = new JScrollPane(codeDisplay);
        scrollPane.setBorder(new EmptyBorder(5, 5, 5, 5));

        displayPanel.add(header, BorderLayout.NORTH);
        displayPanel.add(scrollPane, BorderLayout.CENTER);

        updateBorder(Color.GRAY);
        add(displayPanel);
    }

    private void logNewInput(URL input) {
        this.currentFile = input;

        if (currentFile == null) {
            resetDisplay();
            return;
        }

        boolean compiles = Reader5e.fileCompiles(currentFile);
        updateUIState(compiles);
        previewFileContent(compiles);
    }

    private void previewFileContent(boolean compiles) {
        try {
            List<String> lines = Files.readAllLines(FileUtils.toFile(currentFile).toPath());

            if (compiles) {
                codeDisplay.setLines(lines);
                scrollPane.setViewportView(codeDisplay);
                header.setText("✔ Valid Configuration Found");
            } else {
                fallbackDisplay.setText(String.join("\n", lines));
                scrollPane.setViewportView(fallbackDisplay);
                header.setText("✘ Syntax Error: Check Formatting");
            }
        } catch (IOException e) {
            Message.fileError(e);
        }
    }

    private void updateUIState(boolean compiles) {
        Color highlight = compiles ? ColorStyle.PARTY.getColor() : ColorStyle.ORANGE_ISH_RED.getColor();
        updateBorder(highlight);

        combatButton.setEnabled(compiles);
        creatorButton.setEnabled(compiles);
    }

    private void resetDisplay() {
        header.setText("Mode: Create New");
        scrollPane.setViewportView(new JLabel("New Campaign File will be generated.", SwingConstants.CENTER));
        updateBorder(ColorStyle.NPC.getColor());
        combatButton.setEnabled(false);
    }

    private void updateBorder(Color accent) {
        displayPanel.setBorder(new CompoundBorder(
                new MatteBorder(5, 0, 0, 0, accent),
                new EmptyBorder(10, 15, 10, 15)
        ));
    }

    private JLabel createSectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 10));
        label.setForeground(Color.GRAY);
        label.setBorder(new EmptyBorder(20, 0, 5, 0));
        return label;
    }
}