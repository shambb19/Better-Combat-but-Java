package character_info.combatant;

import txt_input.Key;
import util.TxtReader;

import java.util.ArrayList;
import java.util.EnumMap;

import static txt_input.Key.*;

public class NPC extends Combatant {

    protected final boolean isEnemy;

    public NPC(String name, int hpMax, int armorClass, boolean isEnemy) {
        super(name, hpMax, armorClass);
        this.isEnemy = isEnemy;
    }

    public NPC(EnumMap<Key, Object> values, boolean isEnemy) {
        super(
                (String) values.get(NAME),
                TxtReader.getHp((String) values.get(HP)),
                (int) values.get(AC)
        );
        this.isEnemy = isEnemy;
    }

    public NPC(String name, NPC source) {
        super(name, source.hpMax, source.armorClass);
        this.isEnemy = source.isEnemy;
    }

    public NPC copy() {
        return new NPC(name, hpMax, armorClass, isEnemy);
    }

    public boolean isAlly() {
        return !isEnemy;
    }

    public boolean isEnemy() {
        return isEnemy;
    }

    /**
     * @return the lines of text for this combatant to be logged in a .txt file for its party.
     */
    public ArrayList<String> toTxt() {
        ArrayList<String> txt = new ArrayList<>();
        if (isEnemy) {
            txt.add(".enemy");
        } else {
            txt.add(".npc");
        }
        txt.add("name: " + name);
        txt.add("hp: " + hpCurrent + "/" + hpMax);
        txt.add("ac: " + armorClass);
        txt.add("");
        return txt;
    }

}
