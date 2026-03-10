package scenario_info;

import character_info.combatant.Combatant;

import java.util.ArrayList;

public record Scenario(String name, ArrayList<Combatant> with, ArrayList<Combatant> against) {

    public ArrayList<String> toTxt() {
        ArrayList<String> txt = new ArrayList<>();
        txt.add(".scenario");
        txt.add("name <= " + name);

        if (!with.isEmpty()) {
            StringBuilder withString = new StringBuilder("with <= ");
            with.forEach(combatant -> withString.append(combatant.name()).append(","));
            txt.add(withString.toString());
        }

        StringBuilder againstString = new StringBuilder("against <= ");
        against.forEach(combatant -> againstString.append(combatant.name()).append(","));
        txt.add(againstString.toString());

        txt.add("");
        return txt;
    }

    @Override
    public String toString() {
        return name;
    }

}