package campaign_creator;

import _main.SystemMain;
import character_info.combatant.Combatant;
import scenario_info.Battle;
import scenario_info.Scenario;
import txt_input.CampaignWriter;
import util.Locators;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;

import static util.Message.template;

public class DownloadDocDisplayPanel extends JPanel {

    private ColoredTxtDisplay display;

    private final ArrayList<Combatant> friendlies;
    private final ArrayList<Combatant> enemies;
    private final ArrayList<Scenario> scenarios;

    public DownloadDocDisplayPanel() {
        friendlies = new ArrayList<>();
        enemies = new ArrayList<>();
        scenarios = new ArrayList<>();

        construct();
    }

    public DownloadDocDisplayPanel(Battle input) {
        friendlies = input.friendlies();
        enemies = input.enemies();
        scenarios = input.scenarios();

        construct();
        setText();
    }

    private void construct() {
        setLayout(new BorderLayout());

        JScrollPane host = new JScrollPane();

        display = new ColoredTxtDisplay(null);
        host.setViewportView(display);

        JPanel sendPanel = new JPanel(new FlowLayout());

        JButton downloadButton = new JButton("Download");
        downloadButton.addActionListener(e -> download());

        JButton copyButton = new JButton("Copy to Clipboard");
        copyButton.addActionListener(e -> clipboardCopy());

        JButton runNowButton = new JButton("Start Combat (This Campaign)");
        runNowButton.addActionListener(e -> SystemMain.switchToCombat(download()));

        sendPanel.add(downloadButton);
        sendPanel.add(copyButton);
        sendPanel.add(runNowButton);

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
        ArrayList destination = switch (obj) {
            case Combatant c when c.isEnemy() -> enemies;
            case Combatant c when !c.isEnemy() -> friendlies;
            case Scenario ignored -> scenarios;
            default -> throw new IllegalArgumentException("Unexpected class parameter: need Combatant or Scenario");
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

    private File download() {
        CampaignWriter writer = new CampaignWriter("New Campaign", friendlies, enemies, scenarios);
        File savedFile = writer.getFile();

        if (savedFile != null && savedFile.exists()) {
            template("Downloaded! Saved to: " + savedFile.getAbsolutePath());
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