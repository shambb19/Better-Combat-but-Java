package txt_input;

import combat.Main;
import combatants.Combatant;
import scenarios.Battle;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

import static util.Reader.identifier;
import static util.Reader.withoutIdentifier;

public class BattleReader {

    private final ArrayList<String> allyLines = new ArrayList<>();
    private final ArrayList<Combatant> readAllies = new ArrayList<>();

    private final ArrayList<String> enemyLines = new ArrayList<>();
    private final ArrayList<Combatant> readEnemies = new ArrayList<>();

    public BattleReader(File scenario, ArrayList<Combatant> party) throws IOException {
        splitFile(scenario);
        handleParty(party);
        handleLinesFor(allyLines, readAllies, false);
        handleLinesFor(enemyLines, readEnemies, true);
    }

    public Battle getBattle() {
        return new Battle(readAllies, readEnemies);
    }

    private void splitFile(File scenario) throws IOException {
        ArrayList<String> allLines;
        try {
            allLines = new ArrayList<>(Files.readAllLines(scenario.toPath()));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                    Main.menu,
                    "Battle Scenario File Read Error",
                    Main.TITLE,
                    JOptionPane.ERROR_MESSAGE
            );
            throw e;
        }

        while (!allLines.isEmpty()) {
            String line = allLines.getFirst();

            if (line.equals("<Allies>")) {
                addTeamToList(allLines, allyLines, "<Allies>");
            } else if (line.equals("<Enemies>")) {
                addTeamToList(allLines, enemyLines, "<Enemies>");
            } else {
                allLines.removeFirst();
            }
        }

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


    private void handleParty(ArrayList<Combatant> party) {
        if (!allyLines.getFirst().startsWith("party=")) {
            return;
        }
        if (allyLines.getFirst().startsWith("party=") && party == null) {
            allyLines.removeFirst();
            return;
        }
        String[] partyMembers = withoutIdentifier(allyLines.removeFirst()).split("/");
        for (String partyMember : partyMembers) {
            for (Combatant combatant : party) {
                if (combatant.name().equalsIgnoreCase(partyMember)) {
                    readAllies.add(combatant);
                }
            }
        }
    }

    private void handleLinesFor(ArrayList<String> teamLines, ArrayList<Combatant> readTeam, boolean isEnemyTeam) {
        ArrayList<String> currentRead = new ArrayList<>();

        while (!teamLines.isEmpty()) {
            if (teamLines.getFirst().equals("{")) {
                isolateCurrentRead(currentRead, teamLines);

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
                readTeam.add(new Combatant(name, hp, ac, isEnemyTeam));
            } else {
                teamLines.removeFirst();
            }
        }

        currentRead.clear();
    }

    private void isolateCurrentRead(ArrayList<String> currentRead, ArrayList<String> teamLines) {
        teamLines.removeFirst();
        while (!teamLines.getFirst().equals("}")) {
            currentRead.add(teamLines.removeFirst());
        }
        teamLines.removeFirst();
    }

}
