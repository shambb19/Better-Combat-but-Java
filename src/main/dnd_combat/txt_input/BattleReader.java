package txt_input;

import character_info.Combatant;
import combat_menu.CombatMenu;
import main.CombatMain;
import scenario_info.Battle;
import util.Message;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

import static util.Reader.identifier;
import static util.Reader.withoutIdentifier;

public class BattleReader {

    private boolean errorOccured;

    private final ArrayList<String> friendlyLines = new ArrayList<>();
    private final ArrayList<Combatant> readFriendlies = new ArrayList<>();

    private final ArrayList<String> enemyLines = new ArrayList<>();
    private final ArrayList<Combatant> readEnemies = new ArrayList<>();

    public BattleReader(File scenario) {
        try {
            splitFile(scenario);
            handleFriendlies();
            handleEnemies();
            errorOccured = false;
        } catch (Exception e) {
            Message.fileError(e);
            e.printStackTrace();
            errorOccured = true;
        }
    }

    public Battle getBattle() {
        if (errorOccured) {
            return null;
        }
        return new Battle(readFriendlies, readEnemies);
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

            if (line.equals("<Allies>")) {
                addTeamToList(allLines, friendlyLines, "<Allies>");
            } else if (line.equals("<Enemies>")) {
                addTeamToList(allLines, enemyLines, "<Enemies>");
            } else {
                allLines.removeFirst();
            }
        }

        friendlyLines.replaceAll(String::trim);
        enemyLines.replaceAll(String::trim);
    }

    private void addTeamToList(ArrayList<String> source, ArrayList<String> destination, String key) {

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
            ArrayList<String> currentRead = isolateAndGetNextFriendly();
            String header = currentRead.removeFirst().substring(1);

            switch (header) {
                case "party" -> readFriendlies.add(new PartyReader(currentRead).get());
                case "npc" -> readFriendlies.add(getNpc(currentRead, false));
            }
        }
    }

    private void handleEnemies() {
        while (!enemyLines.isEmpty()) {
            readEnemies.add(getNpc(isolateAndGetNextEnemy(), true));
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

    private ArrayList<String> isolateAndGetNextFriendly() {
        return getCombatantWithHeader(friendlyLines);
    }

    private ArrayList<String> isolateAndGetNextEnemy() {
        ArrayList<String> currentRead = getCombatantWithHeader(enemyLines);
        currentRead.removeFirst();
        return currentRead;
    }

    private ArrayList<String> getCombatantWithHeader(ArrayList<String> source) {
        ArrayList<String> currentRead = new ArrayList<>();

        while (!source.getFirst().equals("}")) {
            currentRead.add(source.removeFirst());
        }
        source.removeFirst();

        return currentRead;
    }

}