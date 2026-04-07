package __main;

import _global_list.Resource;
import campaign_creator_menu.ColoredTxtDisplay;
import combat_menu.popup.FileGetter;
import format.ColorStyle;
import format.swing_comp.SwingComp;
import format.swing_comp.SwingPane;
import txt_input.Reader5e;
import util.Message;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class UploadMain extends JDialog {

    private static final String INSTRUCTIONS = "Select a file from the options below. " +
            "The native file includes starter code for the Kyreun Campaign and an example Orc scenario.";

    private JButton combatButton, creatorButton;
    private JPanel displayPanel;
    private JLabel header;
    private JScrollPane scrollPane;
    private ColoredTxtDisplay codeDisplay;
    private JTextArea fallbackDisplay;

    private URL currentFile = null;

    private UploadMain() {
        setTitle("Campaign File Selection");
        setModal(false);
        setAlwaysOnTop(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(900, 500));
        setLayout(new GridLayout(1, 2));
        setIconImage(Main.getImage());

        initActionPanel();
        initDisplayPanel();

        pack();
        setLocationRelativeTo(null);
    }

    private void initActionPanel() {
        JPanel actionPanel = SwingPane.panel()
                .withLayout(SwingPane.VERTICAL_BOX)
                .withEmptyBorder(20)
                .build();

        SwingComp.label(Main.getIcon()).centered().in(actionPanel);

        JTextPane instructionPanel = new JTextPane();
        instructionPanel.setText(INSTRUCTIONS);
        instructionPanel.setEditable(false);
        instructionPanel.setBackground(null);
        actionPanel.add(instructionPanel);

        JPanel uploadPanel = SwingPane.panelIn(actionPanel).withLayout(SwingPane.FLOW).build();

        SwingComp.label("Upload Options: ").in(uploadPanel);

        SwingComp.button("New Campaign", () -> logNewInput(null)).in(uploadPanel);
        SwingComp.button("Upload Existing (.txt)", () -> logNewInput(FileGetter.getUrl(this))).in(uploadPanel);
        SwingComp.button("Load Kyreun Starter", () -> logNewInput(Resource.STARTER_CODE.url())).in(uploadPanel);

        JPanel runPanel = SwingPane.panelIn(actionPanel).withLayout(SwingPane.FLOW).build();

        SwingComp.label("Run Mode Options: ").in(runPanel);

        combatButton = SwingComp.button("Start Combat Encounter",
                () -> {
                    Main.runCombatEncounter(currentFile);
                    dispose();
                }
        ).disabled().in(runPanel).build();

        creatorButton = SwingComp.button("Open Campaign Creator",
                () -> {
                    Main.runCampaignCreator(currentFile);
                    dispose();
                }
        ).disabled().in(runPanel).build();

        add(actionPanel);
    }

    private void initDisplayPanel() {
        header = SwingComp.label("No File Selected")
                .withForeground(Color.WHITE)
                .withSize(0, 40)
                .centered()
                .build();

        codeDisplay = new ColoredTxtDisplay(null);
        fallbackDisplay = new JTextArea();
        fallbackDisplay.setEditable(false);

        scrollPane = SwingComp.scrollPane(codeDisplay).withEmptyBorder(5).build();

        displayPanel = SwingPane.panelIn(this).withLayout(SwingPane.BORDER)
                .with(header, BorderLayout.NORTH)
                .with(scrollPane, BorderLayout.CENTER)
                .build();

        setBorderTo(Color.GRAY);
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

    private void setBorderTo(Color accent) {
        SwingPane.modifiable(displayPanel).withHighlight(accent, SwingComp.TOP);
    }

    private void resetDisplay() {
        header.setText("Mode: Create New");
        scrollPane.setViewportView(new JLabel("New Campaign File will be generated.", SwingConstants.CENTER));
        setBorderTo(ColorStyle.NPC.getColor());
        combatButton.setEnabled(false);
        creatorButton.setEnabled(true);
    }

    private void updateUIState(boolean compiles) {
        Color highlight = compiles ? ColorStyle.PARTY.getColor() : ColorStyle.ORANGE_ISH_RED.getColor();
        setBorderTo(highlight);
        combatButton.setEnabled(compiles);
        creatorButton.setEnabled(true);
    }

    private void previewFileContent(boolean compiles) {
        try {
            try (InputStream is = currentFile.openStream();
                 BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(is, StandardCharsets.UTF_8))) {

                List<String> lines = reader.lines().toList();

                if (compiles) {
                    codeDisplay.setLines(lines);
                    codeDisplay.setCaretPosition(0);
                    scrollPane.setViewportView(codeDisplay);
                    header.setText("✔ Valid Configuration Found");
                } else {
                    fallbackDisplay.setText(String.join("\n", lines));
                    fallbackDisplay.setCaretPosition(0);
                    scrollPane.setViewportView(fallbackDisplay);
                    header.setText("✘ Syntax Error(s): Ensure Formatting Matches Current Version (see README.md)");
                }
            }
        } catch (IOException e) {
            Message.fileError(e);
        }
    }

    public static void showNewInstance() {
        SwingUtilities.invokeLater(() -> new UploadMain().setVisible(true));
    }


}