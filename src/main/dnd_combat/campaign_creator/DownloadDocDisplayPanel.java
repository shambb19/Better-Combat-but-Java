package campaign_creator;

import _main.SystemMain;
import character_info.combatant.Combatant;
import character_info.combatant.NPC;
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

    private JTextArea display;

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

        display = new JTextArea();
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
        if (selection instanceof Combatant combatant) {
            if (combatant.isEnemy()) {
                addOrReplace(enemies, combatant);
            } else {
                addOrReplace(friendlies, combatant);
            }
        } else if (selection instanceof Scenario scenario) {
            addOrReplaceScenario(scenario);
        }
        setText();
    }

    private void setText() {
        display.setText(displayTextAsString());
        display.setCaretPosition(0);
    }

    private void addOrReplace(ArrayList<Combatant> destination, Combatant combatant) {
        Combatant oldVer = Locators.getCombatantWithNameFrom(destination, combatant.name());

        if (oldVer != null && destination.contains(oldVer)) {
            destination.set(destination.indexOf(oldVer), combatant);
        } else {
            if (combatant instanceof NPC) {
                destination.addLast(combatant);
            } else {
                destination.addFirst(combatant);
            }
        }

        destination.sort(Comparator.comparing(Combatant::name));
    }

    private void addOrReplaceScenario(Scenario scenario) {
        Scenario oldVer = Locators.getScenarioWithNameFrom(scenarios, scenario.name());

        if (oldVer != null && scenarios.contains(oldVer)) {
            scenarios.set(scenarios.indexOf(oldVer), scenario);
        } else {
            scenarios.add(scenario);
        }
        scenarios.sort(Comparator.comparing(Scenario::name));
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

    private String displayTextAsString() {
        StringBuilder string = new StringBuilder();
        CampaignWriter writer = new CampaignWriter(friendlies, enemies, scenarios);
        writer.getCode().forEach(line -> string.append(line).append("\n"));
        return string.toString();
    }

}