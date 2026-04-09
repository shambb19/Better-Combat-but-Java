package campaign_creator_menu;

import __main.Main;
import character_info.combatant.Combatant;
import encounter_info.Battle;
import encounter_info.Scenario;
import txt_input.CampaignWriter;
import util.Locators;
import util.Message;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static swing.swing_comp.SwingComp.button;
import static swing.swing_comp.SwingComp.scrollPane;
import static swing.swing_comp.SwingPane.*;
import static util.Message.template;

public class DownloadDocDisplayPanel extends JPanel {

    private final List<Combatant> friendlies;
    private final List<Combatant> enemies;
    private final List<Scenario> scenarios;
    private final ColoredTxtDisplay display;

    public DownloadDocDisplayPanel(Battle input) {
        friendlies = input.friendlies();
        enemies = input.enemies();
        scenarios = input.scenarios();
        display = new ColoredTxtDisplay(null);

        modifiable(this).withLayout(BORDER).withLabeledBorder("Completed Campaign Code Preview");

        scrollPane(display).in(this, BorderLayout.CENTER);

        panelIn(this, BorderLayout.SOUTH)
                .collect(
                        button("Download", this::download),
                        button("Copy to Clipboard", this::clipboardCopy),
                        button("Start Combat (This Campaign)", () -> Main.switchToCombat(download()))
                ).withLayout(FLOW);

        setText();
    }

    public void addElement(Object selection) {
        addOrReplace(selection);
        setText();
    }

    private void setText() {
        display.setLines(displayTextAsList());
        display.setCaretPosition(0);
    }

    @SuppressWarnings("unchecked")
    private void addOrReplace(Object obj) {
        var destination = switch (obj) {
            case Combatant c when c.isEnemy() -> enemies;
            case Combatant c when !c.isEnemy() -> friendlies;
            case Scenario ignored -> scenarios;
            default -> throw new IllegalArgumentException("unexpected class parameter: need Combatant or Scenario");
        };

        Object oldVer = Locators.getWithNameFromDirectory(destination, obj);

        if (oldVer != null && destination.contains(oldVer)) {
            int confirmOption = Message.question("Another item has been found with the same name. " +
                    "Would you still like to add this? " +
                    "Doing so will overwrite the existing item.");
            if (confirmOption == JOptionPane.NO_OPTION) {
                return;
            }

            int idx = destination.indexOf(oldVer);
            ((List<Object>) destination).set(idx, obj);
        } else {
            ((List<Object>) destination).add(obj);
        }

        destination.sort(Comparator.comparing(Object::toString));
    }

    private URL download() {
        CampaignWriter writer = new CampaignWriter(friendlies, enemies, scenarios);

        int choice = Message.question("Would you like to download this campaign?");
        if (choice == JOptionPane.NO_OPTION) {
            return writer.getUrl("file_param", false);
        }

        URL savedFile = writer.getUrl("Campaign File", true);
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