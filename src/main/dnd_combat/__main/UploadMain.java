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

    private static final String INSTRUCTIONS = "Select a file from the options below. " +
            "The native file includes starter code for the Kyreun Campaign and an example Orc scenario.";
    public static final ImageIcon ICON = getScaledIcon();

    private JButton combatButton, creatorButton;
    private JPanel displayPanel;
    private JLabel header;
    private JScrollPane scrollPane;
    private ColoredTxtDisplay codeDisplay;
    private JTextArea fallbackDisplay;

    private URL currentFile = null;

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
        JPanel actionPanel = new JPanel(new GridBagLayout());
        actionPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(5, 0, 5, 0);

        JLabel logoLabel = new JLabel(ICON);
        actionPanel.add(logoLabel, gbc);

        gbc.gridy++;
        JTextPane instructionPanel = new JTextPane();
        instructionPanel.setText(INSTRUCTIONS);
        instructionPanel.setEditable(false);
        instructionPanel.setBackground(null);
        actionPanel.add(instructionPanel, gbc);

        gbc.gridy++;
        JPanel uploadPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        uploadPanel.add(new JLabel("Upload Options: "));

        uploadPanel.add(SwingStyles.simpleButton(
                "New Campaign",
                e -> logNewInput(null))
        );
        uploadPanel.add(SwingStyles.simpleButton(
                "Upload Existing (.txt)",
                e -> logNewInput(FileGetter.getUrl()))
        );
        uploadPanel.add(SwingStyles.simpleButton(
                "Load Kyreun Starter",
                e -> logNewInput(getClass().getResource("/starter.txt")))
        );
        actionPanel.add(uploadPanel, gbc);

        gbc.gridy++;
        JPanel runPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        runPanel.add(new JLabel("Run Mode Options: "));

        combatButton = SwingStyles.simpleButton(
                "Start Combat Encounter",
                e -> {
                    Main.runCombatEncounter(currentFile);
                    dispose();
                }
        );
        combatButton.setEnabled(false);

        creatorButton = SwingStyles.simpleButton(
                "Open Campaign Creator",
                e -> {
                    Main.runCampaignCreator(currentFile);
                    dispose();
                }
        );
        creatorButton.setEnabled(false);

        runPanel.add(combatButton);
        runPanel.add(creatorButton);

        actionPanel.add(runPanel, gbc);

        gbc.gridy++;
        gbc.weighty = 1.0;
        actionPanel.add(Box.createVerticalGlue(), gbc);

        add(actionPanel);
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
                header.setText("✘ Syntax Error(s): Ensure Formatting Matches Current Version (see README.md)");
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

    private static ImageIcon getScaledIcon() {
        URL imgUrl = Main.class.getResource("/logo.png");
        if (imgUrl == null) return null;

        ImageIcon originalIcon = new ImageIcon(imgUrl);
        int width = (int) (originalIcon.getIconWidth() * 0.5);
        int height = (int) (originalIcon.getIconHeight() * 0.5);

        Image scaledImage = originalIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }
}