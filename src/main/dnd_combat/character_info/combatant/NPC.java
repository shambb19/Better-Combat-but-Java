package character_info.combatant;

import java.util.ArrayList;

public class NPC extends Combatant {

    protected final boolean isEnemy;

    public NPC(String name, int hpMax, int armorClass, boolean isEnemy) {
        super(name, hpMax, armorClass);
        this.isEnemy = isEnemy;
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
        txt.add("name=" + name);
        txt.add("hp=" + hpCurrent + "/" + hpMax);
        txt.add("ac=" + armorClass);
        txt.add("");
        return txt;
    }

}
