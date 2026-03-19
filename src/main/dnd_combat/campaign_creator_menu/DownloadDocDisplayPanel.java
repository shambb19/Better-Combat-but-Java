package campaign_creator_menu;

import __main.Main;
import character_info.combatant.Combatant;
import encounter_info.Battle;
import encounter_info.Scenario;
import format.SwingStyles;
import txt_input.CampaignWriter;
import util.Locators;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static util.Message.template;

public class DownloadDocDisplayPanel extends JPanel {

    private ColoredTxtDisplay display;

    private final List<Combatant> friendlies;
    private final List<Combatant> enemies;
    private final List<Scenario> scenarios;

    public DownloadDocDisplayPanel(Battle input) {
        friendlies = input.friendlies();
        enemies = input.enemies();
        scenarios = input.scenarios();

        construct();
        setText();
    }

    private void construct() {
        setLayout(new BorderLayout());
        SwingStyles.addLabeledBorder(this, "Completed Campaign Code Preview");

        JScrollPane host = new JScrollPane();

        display = new ColoredTxtDisplay(null);
        host.setViewportView(display);

        JPanel sendPanel = new JPanel(new FlowLayout());

        JButton downloadButton = new JButton("Download");
        downloadButton.addActionListener(e -> download());

        JButton copyButton = new JButton("Copy to Clipboard");
        copyButton.addActionListener(e -> clipboardCopy());

        JButton runNowButton = new JButton("Start Combat (This Campaign)");
        runNowButton.addActionListener(e -> Main.switchToCombat(download()));

        SwingStyles.addComponents(sendPanel,
                downloadButton, copyButton, runNowButton);

        add(host, BorderLayout.CENTER);
        add(sendPanel, BorderLayout.SOUTH);
    }

    public void addElement(Object selection) {
        switch (selection) {
            case Combatant c -> addOrReplace(c);
            case Scenario s -> addOrReplace(s);
            default -> {
            }
        }
        setText();
    }

    private void setText() {
        display.setLines(displayTextAsList());
        display.setCaretPosition(0);
    }

    @SuppressWarnings("unchecked")
    private void addOrReplace(Object obj) {
        List destination = switch (obj) {
            case Combatant c when c.isEnemy() -> enemies;
            case Combatant c when !c.isEnemy() -> friendlies;
            case Scenario ignored -> scenarios;
            default -> throw new IllegalArgumentException("unexpected class parameter: need Combatant or Scenario");
        };

        Object oldVer = Locators.getWithNameFromDirectory(destination, obj);

        if (oldVer != null && destination.contains(oldVer)) {
            int idx = destination.indexOf(oldVer);
            destination.set(idx, obj);
        } else {
            destination.add(obj);
        }

        destination.sort(Comparator.comparing(Object::toString));
    }

    private URL download() {
        CampaignWriter writer = new CampaignWriter("New Campaign", friendlies, enemies, scenarios);
        URL savedFile = writer.getURL();

        if (savedFile != null) {
            template("Successfully saved to Downloads");
            return savedFile;
        } else {
            System.err.println("Failed to save the campaign file.");
            return null;
        }
    }

    private void clipboardCopy() {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection contents = new StringSelection(displayTextAsString());
        clipboard.setContents(contents, contents);
        template("Copied!");
    }

    private ArrayList<String> displayTextAsList() {
        CampaignWriter writer = new CampaignWriter(friendlies, enemies, scenarios);
        return writer.getCode();
    }

    private String displayTextAsString() {
        StringBuilder string = new StringBuilder();
        displayTextAsList().forEach(line -> string.append(line).append("\n"));
        return string.toString();
    }

}