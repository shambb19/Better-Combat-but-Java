package gui.popup;

import combat.Main;
import combatants.Combatant;
import txt_input.BattleReader;
import txt_input.PartyReader;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class FileInputPopup extends JFrame {

    private final FileSelectPanel partySelectPanel;
    private final FileSelectPanel battleSelectPanel;

    public FileInputPopup() {
        setTitle("Input Battle Scenario");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(0, 1));
        setAlwaysOnTop(true);

        partySelectPanel = new FileSelectPanel(this);
        battleSelectPanel = new FileSelectPanel(this);

        JButton okButton = new JButton("Confirm");
        okButton.putClientProperty("JButton.buttonType", "roundRect");
        okButton.addActionListener(e -> runOkButton());

        add(new JLabel("(Optional) Upload preset party:"));
        add(partySelectPanel);
        add(new JLabel("(Required) Upload battle scenario:"));
        add(battleSelectPanel);
        add(okButton);

        pack();
        setLocationRelativeTo(null);
    }

    private void runOkButton() {
        if (battleSelectPanel.getFile() == null) {
            JOptionPane.showMessageDialog(
                    Main.menu,
                    "Please select a file for the battle scenario.",
                    Main.TITLE,
                    JOptionPane.WARNING_MESSAGE
            );
            dispose();
            return;
        }
        ArrayList<Combatant> party = null;
        PartyReader partyReader;
        boolean isNeedsHpCur = true;
        if (partySelectPanel.getFile() != null) {
            try {
                partyReader = new PartyReader(partySelectPanel.getFile());
                party = partyReader.getCombatants();
                isNeedsHpCur = !partyReader.isHpRecorded();
            } catch (IOException e) {
                return;
            }
        }
        try {
            Main.battle = new BattleReader(battleSelectPanel.getFile(), party).getBattle();
        } catch (IOException ignored) {}
        new FinalizeCombatantsPopup(isNeedsHpCur).setVisible(true);
        dispose();
    }

    static class FileSelectPanel extends JPanel {

        private final JLabel uploadLabel;
        private final JButton uploadButton;
        private File selectedFile;

        public FileSelectPanel(JFrame parent) {
            setLayout(new FlowLayout());

            uploadLabel = new JLabel("No file selected");

            uploadButton = new JButton("Select a file");
            uploadButton.putClientProperty("JButton.buttonType", "roundRect");
            uploadButton.addActionListener(e -> {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileFilter(
                        new javax.swing.filechooser.FileNameExtensionFilter("Text Files", "txt")
                );
                int result = fileChooser.showOpenDialog(this);

                if (result == JFileChooser.APPROVE_OPTION) {
                    selectedFile = fileChooser.getSelectedFile();
                    uploadButton.setText("Select a different file");
                    uploadLabel.setText(fileChooser.getSelectedFile().getName());
                    parent.pack();
                }
            });

            add(uploadLabel);
            add(uploadButton);
        }

        public File getFile() {
            return selectedFile;
        }

    }

}
