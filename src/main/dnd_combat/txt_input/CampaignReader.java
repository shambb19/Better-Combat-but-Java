package txt_input;

import character_info.combatant.Combatant;
import scenario_info.Battle;
import scenario_info.Scenario;
import util.Locators;
import util.Message;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

import static util.TxtReader.*;

public class CampaignReader {

    private boolean errorOccurred = false;

    private final ArrayList<String> combatantLines = new ArrayList<>();
    private final ArrayList<Combatant> readFriendlies = new ArrayList<>();
    private final ArrayList<Combatant> readEnemies = new ArrayList<>();

    private final ArrayList<String> scenarioLines = new ArrayList<>();
    private final ArrayList<Scenario> readScenarios = new ArrayList<>();

    public CampaignReader(File scenario) {
        try {
            splitFile(scenario);
            handleCombatants();
            handleScenarios();
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
                (readFriendlies, new ArrayList<>(readFriendlies),
                readEnemies, new ArrayList<>(readEnemies),
                readScenarios);
    }

    private void splitFile(File scenario) throws IOException {
        ArrayList<String> allLines;
        try {
            allLines = new ArrayList<>(Files.readAllLines(scenario.toPath()));
            allLines.removeIf(String::isBlank);
        } catch (IOException e) {
            Message.fileError(e);
            throw e;
        }

        while (!allLines.isEmpty()) {
            String header = allLines.removeFirst();

            switch (header) {
                case "<Combatants>" -> addSectionToList(allLines, combatantLines);
                case "<Scenarios>" -> addSectionToList(allLines, scenarioLines);
                default -> allLines.removeFirst();
            }
        }
        combatantLines.replaceAll(String::trim);
        scenarioLines.replaceAll(String::trim);
    }

    private void addSectionToList(ArrayList<String> source, ArrayList<String> destination) {
        if (source.isEmpty()) {
            return;
        }

        while (!source.isEmpty() && !source.getFirst().startsWith("<")) {
            String line = source.removeFirst().trim();

            if (!line.isEmpty()) {
                destination.add(line);
            }
        }
    }

    private void handleCombatants() {
        while (!combatantLines.isEmpty()) {
            ArrayList<String> currentRead = getElement(combatantLines);
            String header = currentRead.removeFirst();

            switch (header) {
                case ".party" -> readFriendlies.add(new PartyReader(currentRead).get());
                case ".npc" -> readFriendlies.add(decodeNPC(currentRead, false));
                case ".enemy" -> readEnemies.add(decodeNPC(currentRead, true));
            }
        }
    }

    private void handleScenarios() {
        while (!scenarioLines.isEmpty()) {
            ArrayList<String> currentRead = getElement(scenarioLines);
            currentRead.removeFirst();

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

    private ArrayList<Combatant> getCombatantsFromString(String list, ArrayList<Combatant> source) {
        String[] names = list.split("/");
        ArrayList<Combatant> combatants = new ArrayList<>();

        for (String name : names) {
            Combatant selected = Locators.getCombatantWithNameFrom(source, name);
            assert selected != null;
            combatants.add(selected);
        }
        return combatants;
    }

    private ArrayList<String> getElement(ArrayList<String> source) {
        ArrayList<String> currentRead = new ArrayList<>();

        do {
            currentRead.add(source.removeFirst());
        } while (!source.isEmpty() && !source.getFirst().startsWith("."));

        return currentRead;
    }

}