package scenario_info;

import character_info.combatant.Combatant;
import character_info.combatant.NPC;
import character_info.combatant.PC;

import java.util.ArrayList;
import java.util.HashMap;

public record Scenario(String name, HashMap<Combatant, Integer> with, HashMap<Combatant, Integer> against) {

    public Scenario {
        with.keySet().removeIf(combatant -> combatant instanceof PC);
        against.keySet().removeIf(combatant -> combatant instanceof PC);
    }

    public boolean containsFriendlies() {
        return !with.isEmpty();
    }

    public ArrayList<Combatant> withListSingleOccurrence() {
        return new ArrayList<>(with.keySet());
    }

    public ArrayList<Combatant> withListAllOccurrences() {
        return listAllOccurrences(with);
    }

    public ArrayList<Combatant> againstListSingleOccurrence() {
        return new ArrayList<>(against.keySet());
    }

    public ArrayList<Combatant> againstListAllOccurrences() {
        return listAllOccurrences(against);
    }

    private ArrayList<Combatant> listAllOccurrences(HashMap<Combatant, Integer> source) {
        ArrayList<Combatant> list = new ArrayList<>();

        source.forEach(((combatant, qty) -> {
            if (qty == 1) {
                list.add(combatant);
            } else {
                for (int i = 0; i < qty; i++) {
                    String name = combatant.name() + " " + (i + 1);

                    list.add(new NPC(name, (NPC) combatant));
                }
            }
        }));

        return list;
    }

    public ArrayList<String> toTxt() {
        ArrayList<String> txt = new ArrayList<>();
        txt.add(".scenario");
        txt.add("name: " + name);

        if (!with.isEmpty()) {
            txt.add(getLineFor("with: ", with));
        }
        txt.add(getLineFor("against: ", against));

        txt.add("");
        return txt;
    }

    private String getLineFor(String key, HashMap<Combatant, Integer> source) {
        StringBuilder txt = new StringBuilder(key);
        txt.append("[");
        source.forEach((combatant, num) -> {
            txt.append(combatant.name());
            if (num > 1) {
                txt.append("_").append(num);
            }
            txt.append(", ");
        });
        return txt.delete(txt.length() - 2, txt.length()).append("]").toString();
    }

    @Override
    public String toString() {
        return name;
    }

}