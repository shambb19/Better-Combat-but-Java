package campaign_creator_menu;

import __main.Main;
import combat_object.CombatObject;
import combat_object.combatant.Combatant;
import combat_object.scenario.Scenario;
import encounter.Encounter;
import format.swing_comp.SwingComp;
import format.swing_comp.SwingPane;
import input.CampaignWriter;
import lombok.*;
import lombok.experimental.*;
import util.Locators;
import util.Message;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Stream;

import static format.ColorStyles.FOREGROUND;
import static format.ColorStyles.SUCCESS;
import static format.swing_comp.SwingComp.button;
import static format.swing_comp.SwingComp.scrollPane;
import static format.swing_comp.SwingPane.*;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@ExtensionMethod(util.Message.class)
public class DownloadDocDisplayPanel extends JPanel {

    ArrayList<Combatant> friendlies, enemies;
    ArrayList<Scenario> scenarios;
    ColoredTxtDisplay display;

    public DownloadDocDisplayPanel(Encounter input) {
        friendlies = new ArrayList<>(input.getFriendlies());
        enemies = new ArrayList<>(input.getEnemies());
        scenarios = new ArrayList<>(input.getScenarios());
        display = new ColoredTxtDisplay(null);

        SwingPane.fluent(this).arrangedAs(BORDER).withLabeledBorder("Completed Campaign Code Preview");

        scrollPane(display).in(this, BorderLayout.CENTER);

        SwingComp<JButton> download = button("Download", SUCCESS, this::download);
        SwingComp<JButton> clipboard = button("Copy to clipboard", SUCCESS, this::clipboardCopy);
        SwingComp<JButton> start = button("Start combat (this campaign)", SUCCESS,
                () -> Main.closeCreatorAndOpenCombat(download()));

        Stream.of(download, clipboard, start)
                .forEach(b -> b.withBackgroundAndForeground(SUCCESS, FOREGROUND));

        panelIn(this, BorderLayout.SOUTH).collect(download, clipboard, start).arrangedAs(FLOW, 10, 0)
                .withEmptyBorder(10, 0, 10, 0);

        setText();
    }

    public void addElement(CombatObject selection) {
        addOrReplace(selection);
        setText();
    }

    private void setText() {
        display.setLines(displayTextAsList());
        display.setCaretPosition(0);
    }

    private void addOrReplace(CombatObject obj) {
        switch (obj) {
            case Combatant c when c.isEnemy() -> addOrReplaceFromList(c, enemies);
            case Combatant c -> addOrReplaceFromList(c, friendlies);
            case Scenario s -> addOrReplaceFromList(s, scenarios);
            default ->
                    throw new ClassCastException("DownloadDocDisplayPanel.addOrReplace: Combatant or Scenario expected");
        }
    }

    private <T extends CombatObject> void addOrReplaceFromList(T object, ArrayList<T> destination) {
        T oldVer = Locators.getWithNameFromDirectory(destination, object);

        if (oldVer != null && destination.contains(oldVer)) {
            int confirmOption = Message.askYesNoQuestion("Another item has been found with the same name. " +
                    "Would you still like to add this? " +
                    "Doing so will overwrite the existing item.");
            if (confirmOption == JOptionPane.NO_OPTION) {
                return;
            }

            int idx = destination.indexOf(oldVer);
            destination.set(idx, object);
        } else {
            destination.add(object);
        }

        destination.sort(Comparator.comparing(Object::toString));
    }

    private URL download() {
        CampaignWriter writer = new CampaignWriter(friendlies, enemies, scenarios);

        int choice = Message.askYesNoQuestion("Would you like to download this campaign?");
        if (choice == JOptionPane.NO_OPTION) {
            return writer.getUrl("file_param", false);
        }

        URL savedFile = writer.getUrl("Campaign File", true);

        if (savedFile != null) {
            "Successfully saved to Downloads".showAsInfoMessage();
            return savedFile;
        } else {
            return null;
        }
    }

    private void clipboardCopy() {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection contents = new StringSelection(displayTextAsString());
        clipboard.setContents(contents, contents);
        "Copied!".showAsInfoMessage();
    }

    private ArrayList<String> displayTextAsList() {
        return new CampaignWriter(friendlies, enemies, scenarios).getCode();
    }

    private String displayTextAsString() {
        return String.join(System.lineSeparator(), displayTextAsList());
    }

}