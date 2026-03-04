package txt_menu;

import character_info.Combatant;
import scenario_info.Battle;
import util.Message;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

import static util.Message.template;

public class DownloadDocDisplayPanel extends JPanel {

    private JTextArea display;

    private static final String LINE_END = "\n";
    private static final String ALLY_BRACKET = "<Allies>";
    private static final String ENEMY_BRACKET = "<Enemies>";

    private final ArrayList<Combatant> friendlies;
    private final ArrayList<Combatant> enemies;

    public DownloadDocDisplayPanel() {
        friendlies = new ArrayList<>();
        enemies = new ArrayList<>();

        construct();
    }

    public DownloadDocDisplayPanel(Battle input) {
        friendlies = input.friendlies();
        enemies = input.enemies();

        construct();
    }

    private void construct() {
        setLayout(new BorderLayout());

        JScrollPane host = new JScrollPane();

        display = new JTextArea();
        host.setViewportView(display);

        JPanel sendPanel = new JPanel(new FlowLayout());

        JButton downloadButton = new JButton("Download");
        downloadButton.addActionListener(e -> download());

        JButton copyButton = new JButton("Copy to Clipboard");
        copyButton.addActionListener(e -> clipboardCopy());

        sendPanel.add(downloadButton);
        sendPanel.add(copyButton);

        add(host, BorderLayout.CENTER);
        add(sendPanel, BorderLayout.SOUTH);
    }

    public void addCombatant(Combatant selection) {
        if (selection.isEnemy()) {
            addOrReplace(enemies, selection);
        } else {
            addOrReplace(friendlies, selection);
        }

        StringBuilder displayText = new StringBuilder();
        displayTextAsList().forEach(line -> displayText.append(line).append(LINE_END));
        display.setText(displayText.toString());
    }

    private void addOrReplace(ArrayList<Combatant> destination, Combatant combatant) {
        if (destination.contains(combatant)) {
            destination.set(destination.indexOf(combatant), combatant);
        } else {
            if (combatant.isNPC()) {
                destination.addLast(combatant);
            } else {
                destination.addFirst(combatant);
            }
        }
    }

    private void download() {
        File file = new File(
            new File(System.getProperty("user.home"), "Downloads"),
            "New Campaign " + LocalDate.now() + ".txt"
        );

        try (FileWriter writer = new FileWriter(file)) {
            for (String line : displayTextAsList()) {
                writer.write(line + LINE_END);
            }
            Desktop.getDesktop().open(file);
        } catch (IOException io) {
            Message.throwFileDownloadError(this, io);
        }
    }

    private void clipboardCopy() {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection contents = new StringSelection(displayTextAsString());
        clipboard.setContents(contents, contents);
        template("Copied!");
    }

    private String displayTextAsString() {
        StringBuilder string = new StringBuilder();
        displayTextAsList().forEach(line -> string.append(line).append(LINE_END));
        return string.toString();
    }

    private ArrayList<String> displayTextAsList() {
        ArrayList<String> text = new ArrayList<>();
        text.addAll(getListText(ALLY_BRACKET, friendlies));
        text.addAll(getListText(ENEMY_BRACKET, enemies));
        return text;
    }

    private ArrayList<String> getListText(String bracket, ArrayList<Combatant> input) {
        ArrayList<String> text = new ArrayList<>();

        text.add(bracket);
        text.add("");
        if (input.isEmpty()) {
            text.add("");
        } else {
            input.forEach(combatant -> text.addAll(combatant.toTxt()));
        }
        text.add(bracket);
        text.add("");

        return text;
    }

}
