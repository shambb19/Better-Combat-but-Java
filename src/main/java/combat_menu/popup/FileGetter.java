package combat_menu.popup;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class FileGetter {

    private File file;

    public static URL getUrl(Container root) {
        try {
            return new FileGetter(root).file.toURI().toURL();
        } catch (MalformedURLException ignored) {
            return null;
        }
    }

    private FileGetter(Container root) {
        root.setVisible(false);

        file = null;

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter(
                "TEXT FILES",
                "txt",
                "text"
        ));

        int result = fileChooser.showOpenDialog(null);
        File selection = fileChooser.getSelectedFile();

        if (result == JFileChooser.APPROVE_OPTION)
            file = selection;

        root.setVisible(true);
    }

}
