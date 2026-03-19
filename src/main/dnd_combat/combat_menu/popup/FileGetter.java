package combat_menu.popup;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class FileGetter {

    private File file;

    public static File getFile() {
        return new FileGetter().file;
    }

    public static URL getUrl() {
        try {
            return new FileGetter().file.toURI().toURL();
        } catch (MalformedURLException ignored) {
            return null;
        }
    }

    private FileGetter() {
        file = null;

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter(
                "TEXT FILES",
                "txt",
                "text"
        ));

        int result = fileChooser.showOpenDialog(null);
        File selection = fileChooser.getSelectedFile();

        if (result == JFileChooser.APPROVE_OPTION) {
            file = selection;
        }
    }

}
