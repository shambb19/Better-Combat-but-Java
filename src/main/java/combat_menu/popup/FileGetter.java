package combat_menu.popup;

import util.Message;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.net.MalformedURLException;
import java.net.URL;

public class FileGetter {

    private URL url;

    public static URL getUrl(Component parent) {
        return new FileGetter(parent).url;
    }

    private FileGetter(Component parent) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter(
                "TEXT FILES",
                "txt",
                "text"
        ));

        int result = fileChooser.showOpenDialog(parent);

        if (result == JFileChooser.APPROVE_OPTION)
            try {
                url = fileChooser.getSelectedFile().toURI().toURL();
            } catch (MalformedURLException e) {
                Message.showFileErrorMessage(e);
            }
    }
}