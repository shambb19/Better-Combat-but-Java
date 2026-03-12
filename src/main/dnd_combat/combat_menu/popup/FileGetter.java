package combat_menu.popup;

import txt_input_2.Txt5eReader;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;

public class FileGetter {

    private File file;

    public FileGetter() {
        file = null;

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter(
                "TEXT FILES",
                "txt",
                "text"
        ));

        while (file == null) {
            int result = fileChooser.showOpenDialog(null);
            File selection = fileChooser.getSelectedFile();
            if //(result == JFileChooser.APPROVE_OPTION && new CampaignReader(selection).getBattle() != null) {
            (result == JFileChooser.APPROVE_OPTION && Txt5eReader.getCode(selection) != null) {
                file = selection;
            } else {
                JOptionPane.showConfirmDialog(
                        null,
                        "Could not read selected .txt file.",
                        "Better Combat but Java",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    public File getFile() {
        return file;
    }

}
