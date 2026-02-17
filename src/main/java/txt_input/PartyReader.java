package txt_input;

import combatants.Combatant;
import combatants.Stats;
import damage.Spell;
import damage.Weapon;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

public class PartyReader {

    private final ArrayList<String> allLines;
    private final ArrayList<String> currentRead;
    private final ArrayList<Combatant> readCombatants;

    private String scenarioName;

    public PartyReader(File scenario) throws IOException {
        allLines = new ArrayList<>(Files.readAllLines(scenario.toPath()));
        removeSpaces();

        currentRead = new ArrayList<>();
        readCombatants = new ArrayList<>();

        while (!allLines.isEmpty()) {

        }
    }

    private Combatant stripAndGetCombatant() {
        if (allLines.getFirst().equals("{")) {
            allLines.removeFirst();
        } else {
            return null;
        }

        while (!allLines.getFirst().equals("}")) {
            currentRead.add(allLines.removeFirst());
        }
        currentRead.removeFirst();
    }

    private Combatant getCurrentRead() {
        String name = "name";
        int hp = 20, ac = 10;
        Stats stats = null;
        int prof = 2;
        Stats.stat spellMod = null;
        ArrayList<Weapon> weapons = new ArrayList<>();
        ArrayList<Spell> spells = new ArrayList<>();

        while (!currentRead.isEmpty()) {
            String identifier = identifier(currentRead.getFirst());
            String value = withoutIdentifier(currentRead.removeFirst());

            switch (identifier) {
                case "name" -> name = value;
                case "hp" -> hp = num(value);
                case "ac" -> ac = num(value);
                case "stats" -> stats = getStats(value);
                case "prof" -> prof = num(value);
                case "spellMod" -> spellMod = mod(value);
                case "weapons" -> addWeapons(weapons, value);
                case "spells" -> addSpells(spells, value);
            }
        }

        return new Combatant(name, hp, ac, true, stats, weapons, spells);
    }

    private Stats getStats(String statLine) {

    }

    private void addWeapons(ArrayList<Weapon> host, String line) {
        String[] weapons = line.split("/");
        for (String weapon : weapons) {
            host.add(Weapon.get(weapon));
        }
    }

    private void addSpells(ArrayList<Spell> host, String line) {

    }

    private void removeSpaces() {
        for (int i = 0; i < allLines.size(); i++) {
            String line = allLines.get(i);
            line = line.replace(" ", "");
            allLines.set(i, line);
        }
    }

    private Stats.stat mod(String key) {
        return switch (key) {
            case "str" -> Stats.stat.STR;
            case "dex" -> Stats.stat.DEX;
            case "con" -> Stats.stat.CON;
            case "int" -> Stats.stat.INT;
            case "wis" -> Stats.stat.WIS;
            case "cha" -> Stats.stat.CHA;
            default -> null;
        };
    }

    private String identifier(String line) {
        return line.substring(0, line.indexOf("="));
    }

    private String withoutIdentifier(String line) {
        return line.substring(line.indexOf("=") + 1);
    }

    private int num(String value) {
        return Integer.parseInt(value);
    }

}
