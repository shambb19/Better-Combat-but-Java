package txt_menu;

import damage_implements.Effect;
import damage_implements.Spells;
import damage_implements.Weapons;
import main.SystemMain;
import util.Message;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class TextMenuBar extends JMenuBar {

    private static final String MANUAL_SPELL_TEMPLATE =
            """
                    {
                    name=
                    dmg=
                    save=
                    effect=
                    }""";

    private static final URL SPELL_URL = SystemMain.class.getResource("/spells.txt");

    private static final String MANUAL_WEAPON_TEMPLATE =
            """
                    {
                    name=
                    dmg=
                    stat=
                    }
                    """;

    private static final URL WEAPON_URL = SystemMain.class.getResource("/weapons.txt");

    public TextMenuBar() {
        JMenu bar = new JMenu("Manual Entry");

        JMenuItem spellItem = new JMenuItem("New Spell");
        spellItem.addActionListener(e -> manual("spell", MANUAL_SPELL_TEMPLATE).setVisible(true));

        JMenuItem weaponItem = new JMenuItem("New Weapon");
        weaponItem.addActionListener(e -> manual("weapon", MANUAL_WEAPON_TEMPLATE).setVisible(true));

        bar.add(spellItem);
        bar.add(weaponItem);
        add(bar);
    }

    private JFrame manual(String type, String template) {
        JFrame frame = new JFrame();
        JPanel panel = new JPanel(new GridLayout(0, 1));

        JLabel effectsList = new JLabel(Effect.getRawNamesString());
        JTextArea input = new JTextArea(template, 6, 0);

        JButton okButton = new JButton("Confirm");
        okButton.addActionListener(e -> {
            frame.setAlwaysOnTop(false);
            updateDoc(type, input);
            frame.dispose();
        });

        if (type.equals("spell")) {
            panel.add(effectsList);
        }
        panel.add(input);
        panel.add(okButton);

        frame.add(panel);
        frame.setAlwaysOnTop(true);
        frame.pack();
        frame.setLocationRelativeTo(null);

        return frame;
    }

    private void updateDoc(String type, JTextArea textArea) {
        String fileName = type + "s.txt";

        try {
            writeTo(fileName, textToList(textArea.getText()));

            Spells.init(SPELL_URL);
            Weapons.init(WEAPON_URL);

            JOptionPane.showMessageDialog(null, "Successfully added " + type);
        } catch (Exception error) {
            Message.template("The new " + type + " could not be saved.");
            error.printStackTrace();
        }
    }

    private void writeTo(String fileName, ArrayList<String> lines) throws IOException {
        File file = new File(fileName);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.newLine();
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        }
    }

    private ArrayList<String> textToList(String areaText) {
        return new ArrayList<>(Arrays.asList(areaText.split("\\r?\\n")));
    }

}
