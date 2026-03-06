package txt_input;

import character_info.Combatant;
import combat_menu.CombatMenu;
import main.CombatMain;
import scenario_info.Battle;
import scenario_info.Scenario;
import util.Message;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

import static util.Reader.identifier;
import static util.Reader.withoutIdentifier;

public class BattleReader {

    private boolean errorOccurred;

    private final ArrayList<String> friendlyLines = new ArrayList<>();
    private final ArrayList<Combatant> readFriendlies = new ArrayList<>();

    private final ArrayList<String> enemyLines = new ArrayList<>();
    private final ArrayList<Combatant> readEnemies = new ArrayList<>();

    private final ArrayList<String> scenarioLines = new ArrayList<>();
    private final ArrayList<Scenario> readScenarios = new ArrayList<>();

    public BattleReader(File scenario) {
        try {
            splitFile(scenario);
            handleFriendlies();
            handleEnemies();
            handleScenarios();
            errorOccurred = false;
        } catch (Exception e) {
            Message.fileError(e);
            e.printStackTrace();
            errorOccurred = true;
        }
    }

    public Battle getBattle() {
        if (errorOccurred) {
            return null;
        }
        return new Battle
                (readFriendlies, readFriendlies,
                readEnemies, readEnemies,
                readScenarios);
    }
    private void splitFile(File scenario) throws IOException {
        ArrayList<String> allLines;
        try {
            allLines = new ArrayList<>(Files.readAllLines(scenario.toPath()));
            allLines.removeIf(String::isBlank);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                    CombatMain.COMBAT_MENU,
                    "Battle Scenario File Read Error",
                    CombatMenu.TITLE,
                    JOptionPane.ERROR_MESSAGE
            );
            throw e;
        }

        while (!allLines.isEmpty()) {
            String line = allLines.getFirst();

            switch (line) {
                case "<Allies>" -> addSectionToList(allLines, friendlyLines, "<Allies>");
                case "<Enemies>" -> addSectionToList(allLines, enemyLines, "<Enemies>");
                case "<Scenarios>" -> addSectionToList(allLines, scenarioLines, "<Scenarios>");
                default -> allLines.removeFirst();
            }
        }

        friendlyLines.replaceAll(String::trim);
        enemyLines.replaceAll(String::trim);
    }

    private void addSectionToList(ArrayList<String> source, ArrayList<String> destination, String key) {

        if (source.isEmpty()) return;

        if (source.getFirst().equals(key)) {
            source.removeFirst();
        }

        while (!source.isEmpty() && !source.getFirst().equals(key)) {
            String line = source.removeFirst().trim();

            if (!line.isEmpty()) {
                destination.add(line);
            }
        }

        if (!source.isEmpty()) {
            source.removeFirst();
        }
    }

    private void handleFriendlies() {
        while (!friendlyLines.isEmpty()) {
            ArrayList<String> currentRead = nextElementWithHeader(friendlyLines);
            String header = currentRead.removeFirst().substring(1);

            switch (header) {
                case "party" -> readFriendlies.add(new PartyReader(currentRead).get());
                case "npc" -> readFriendlies.add(getNpc(currentRead, false));
            }
        }
    }

    private void handleEnemies() {
        while (!enemyLines.isEmpty()) {
            readEnemies.add(getNpc(nextElementWithoutHeader(enemyLines), true));
        }
    }

    private void handleScenarios() {
        while (!scenarioLines.isEmpty()) {
            ArrayList<String> currentRead = nextElementWithoutHeader(scenarioLines);

            String name = "name";
            ArrayList<Combatant> with = new ArrayList<>();
            ArrayList<Combatant> against = new ArrayList<>();

            while (!currentRead.isEmpty()) {
                String key = identifier(currentRead.getFirst());
                String value = withoutIdentifier(currentRead.removeFirst());
                switch (key) {
                    case "name" -> name = value;
                    case "with" -> with = getCombatantsFromString(value, readFriendlies);
                    case "against" -> against = getCombatantsFromString(value, readEnemies);
                }
            }

            readScenarios.add(new Scenario(name, with, against));
        }
    }

    private Combatant getNpc(ArrayList<String> currentRead, boolean isEnemyTeam) {
        String name = "name";
        int hp = 20, ac = 10;
        while (!currentRead.isEmpty()) {
            String key = identifier(currentRead.getFirst());
            String value = withoutIdentifier(currentRead.removeFirst());
            switch (key) {
                case "name" -> name = value;
                case "hp" -> hp = Integer.parseInt(value);
                case "ac" -> ac = Integer.parseInt(value);
            }
        }
        return new Combatant(name, hp, ac, isEnemyTeam);
    }

    private ArrayList<Combatant> getCombatantsFromString(String list, ArrayList<Combatant> source) {
        String[] names = list.split("/");
        ArrayList<Combatant> combatants = new ArrayList<>();

        for (String s : names) {
            for (Combatant combatant : source) {
                if (s.equals(combatant.name())) {
                    combatants.add(combatant);
                }
            }
        }

        return combatants;
    }

    private ArrayList<String> nextElementWithHeader(ArrayList<String> source) {
        ArrayList<String> currentRead = new ArrayList<>();

        while (!source.getFirst().equals("}")) {
            currentRead.add(source.removeFirst());
        }
        source.removeFirst();

        return currentRead;
    }

    private ArrayList<String> nextElementWithoutHeader(ArrayList<String> source) {
        ArrayList<String> currentRead = nextElementWithHeader(source);
        currentRead.removeFirst();
        return currentRead;
    }

}