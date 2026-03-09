package txt_menu;

import character_info.Combatant;
import scenario_info.Battle;
import scenario_info.Scenario;
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
    private static final String EMPTY_LINE = "";
    private static final String ALLY_BRACKET = "<Allies>";
    private static final String ENEMY_BRACKET = "<Enemies>";
    private static final String SCENARIO_BRACKET = "<Scenarios>";

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

    private void buildText() {
        StringBuilder displayText = new StringBuilder();
        displayTextAsList().forEach(line -> displayText.append(line).append(LINE_END));
        display.setText(displayText.toString());
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
        buildText();
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

    private void addOrReplaceScenario(Scenario scenario) {
        if (scenarios.contains(scenario)) {
            scenarios.set(scenarios.indexOf(scenario), scenario);
        } else {
            scenarios.add(scenario);
        }
    }

    private void download() {
        int numRand = (int) (Math.random()*1000);
        File file = new File(
            new File(System.getProperty("user.home"), "Downloads"),
            "New Campaign " + LocalDate.now() + " " + numRand + ".txt"
        );

        try (FileWriter writer = new FileWriter(file)) {
            for (String line : displayTextAsList()) {
                writer.write(line + LINE_END);
            }
            template("Downloaded!");
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
        text.addAll(getScenarioListText());
        return text;
    }

    private ArrayList<String> getListText(String bracket, ArrayList<Combatant> input) {
        ArrayList<String> text = new ArrayList<>();

        text.add(bracket);
        text.add(EMPTY_LINE);
        if (input.isEmpty()) {
            text.add(EMPTY_LINE);
        } else {
            input.forEach(combatant -> text.addAll(combatant.toTxt()));
        }
        text.add(bracket);
        text.add(EMPTY_LINE);

        return text;
    }

    private ArrayList<String> getScenarioListText() {
        ArrayList<String> text = new ArrayList<>();

        text.add(SCENARIO_BRACKET);
        text.add(EMPTY_LINE);
        if (scenarios.isEmpty()) {
            text.add(EMPTY_LINE);
        } else {
            scenarios.forEach(scenario -> text.addAll(scenario.toTxt()));
        }
        text.add(SCENARIO_BRACKET);
        text.add(EMPTY_LINE);

        return text;
    }

}