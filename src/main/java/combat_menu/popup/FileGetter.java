package combat_menu.popup;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class FileGetter {

    private File file;

    public static URL getUrl(Component parent) {
        try {
            FileGetter getter = new FileGetter(parent);
            if (getter.file != null) {
                return getter.file.toURI().toURL();
            }
        } catch (MalformedURLException ignored) {
        }
        return null;
    }

    private FileGetter(Component parent) {
        file = null;
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter(
                "TEXT FILES",
                "txt",
                "text"
        ));

        int result = fileChooser.showOpenDialog(parent);

        if (result == JFileChooser.APPROVE_OPTION) {
            file = fileChooser.getSelectedFile();
        }
    }
}