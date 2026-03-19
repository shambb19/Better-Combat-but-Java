package format;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SwingStyles {

    public static void addLabeledBorder(JComponent comp, String label) {
        comp.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                label,
                TitledBorder.LEFT,
                TitledBorder.TOP
        ));
    }

    public static void setHighlightsOnFocus(JComponent comp) {
        FocusListener onFocus = new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (e.getComponent() instanceof JTextField field) {
                    SwingUtilities.invokeLater(field::selectAll);
                }
            }
        };

        MouseListener onClick = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getComponent() instanceof JTextField field && !field.hasFocus()) {
                    SwingUtilities.invokeLater(field::selectAll);
                }
            }
        };

        switch (comp) {
            case JSpinner spinner -> {
                JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) spinner.getEditor();
                JTextField field = editor.getTextField();
                field.addFocusListener(onFocus);
                field.addMouseListener(onClick);
            }
            case JTextField field -> {
                field.addFocusListener(onFocus);
                field.addMouseListener(onClick);
            }
            default -> System.err.println("unexpected call to setHighlightsOnFocus: require jtextfield or jspinner");
        }
    }

    public static JPanel getConfirmCancelPanel(
            JButton confirm, ActionListener onConfirm, ActionListener onCancel) {
        JPanel panel = new JPanel(new FlowLayout());

        confirm.putClientProperty("JButton.buttonType", "roundRect");
        confirm.addActionListener(onConfirm);

        JButton cancel = new JButton("Cancel");
        cancel.putClientProperty("JButton.buttonType", "roundRect");
        cancel.addActionListener(onCancel);

        panel.add(confirm);
        panel.add(cancel);

        return panel;
    }

    public static JButton simpleButton(String name, ActionListener listener) {
        JButton button = new JButton(name);
        button.putClientProperty("JButton.buttonType", "roundRect");
        button.addActionListener(listener);
        return button;
    }

    @SuppressWarnings("unchecked")
    public static <T extends JComponent> T cloneComponent(T c) {
        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            ObjectOutputStream outputStream = new ObjectOutputStream(output);
            outputStream.writeObject(c);
            outputStream.flush();

            ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
            ObjectInputStream inputStream = new ObjectInputStream(input);

            return (T) inputStream.readObject();
        } catch (Exception e) {
            Logger.getAnonymousLogger().log(
                    Level.SEVERE, "cloneComponent in SwingStyles: could not copy component"
            );
            return null;
        }
    }

    public static void addComponents(Container host, Component... components) {
        for (Component component : components) {
            host.add(component);
        }
    }

}
