package character_info.combatant;

import character_info.Stat;
import character_info.Stats;
import combat_menu.listener.DieRollListener;
import damage_implements.Spell;
import damage_implements.Weapon;
import util.Message;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class PC extends Combatant {

    private final Stats stats;
    private final ArrayList<Weapon> weapons;
    private final ArrayList<Spell> spells;

    public PC(String name, int hpMax, int armorClass,
              Stats stats, ArrayList<Weapon> weapons, ArrayList<Spell> spells
    ) {
        super(name, hpMax, armorClass);
        this.stats = stats;
        this.weapons = weapons;
        this.spells = spells;
    }

    public int mod(Stat stat) {
        return stats.mod(stat);
    }

    public int attackBonus(Weapon weapon) {
        return stats.prof() + stats.weaponAttackBonus(weapon);
    }

    public int spellAttackBonus() {
        return stats.spellAttackBonus();
    }

    public int saveDc() {
        return stats.saveDc();
    }

    public ArrayList<Weapon> weapons() {
        return weapons;
    }

    public String weaponStr() {
        StringBuilder weaponStr = new StringBuilder("weapons=");
        weapons.forEach(weapon -> weaponStr.append(weapon.name()).append("/"));
        return weaponStr.toString();
    }

    public ArrayList<Spell> spells() {
        return spells;
    }

    public String spellStr() {
        StringBuilder spellStr = new StringBuilder("spells=");
        spells.forEach(spell -> spellStr.append(spell.name()).append("/"));
        return spellStr.toString();
    }

    public Stats stats() {
        return stats;
    }

    public void levelUp() {
        hpMax = Message.getWithLoopUntilInt(
                name + "'s health increase after level up?",
                "We love level ups!"
        );
        stats.levelUp();
    }

    public JPanel getCombatantPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 3));

        JLabel label = new JLabel(name);

        JTextField initiativeField = new JTextField();
        initiativeField.addKeyListener(new DieRollListener(1, 20, initiativeField));

        JCheckBox absentBox = new JCheckBox();
        absentBox.addActionListener(e -> initiativeField.setEnabled(!absentBox.isSelected()));

        panel.add(label);
        panel.add(initiativeField);
        panel.add(absentBox);

        return panel;
    }

    /**
     * @return the lines of text for this combatant to be logged in a .txt file for its party.
     */
    public ArrayList<String> toTxt() {
        ArrayList<String> txt = new ArrayList<>();
        txt.add(".party");
        txt.add("name=" + name);
        txt.add("hp=" + hpCurrent + "/" + hpMax);
        txt.add("ac=" + armorClass);
        txt.add("level=" + stats.level());
        txt.add("class=" + stats.class5e());
        txt.add(stats.toString());

        if (!weapons.isEmpty()) {
            txt.add(weaponStr());
        }
        if (!spells.isEmpty()) {
            txt.add(spellStr());
        }

        txt.add("");
        return txt;
    }

}
