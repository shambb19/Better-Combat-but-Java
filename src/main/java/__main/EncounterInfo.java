package __main;

import character_info.combatant.Combatant;
import character_info.combatant.PC;
import encounter_info.Battle;
import encounter_info.PlayerQueue;
import util.Filter;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;

public class EncounterInfo {

    private static Battle BATTLE = null;

    private static PlayerQueue QUEUE = null;
    private static final PropertyChangeSupport support = new PropertyChangeSupport(EncounterInfo.class);

    public static void init(Battle battle) {
        BATTLE = battle;
    }

    public static void confirmQueueFinalized() {
        QUEUE = new PlayerQueue(BATTLE.friendlies(), BATTLE.enemies());
        notifyQueueStateChange();
    }

    public static Battle getBattle() {
        return BATTLE;
    }

    public static List<PC> getParty() {
        return Filter.matchingClass(BATTLE.friendlies(), PC.class);
    }

    public static List<Combatant> getFriendlies() {
        return BATTLE.friendlies();
    }

    public static List<Combatant> getEnemies() {
        return BATTLE.enemies();
    }

    public static void addCombatant(Combatant newCombatant) {
        if (newCombatant.isEnemy())
            BATTLE.enemies().add(newCombatant);
        else
            BATTLE.friendlies().add(newCombatant);
    }

    public static void notifyQueueStateChange() {
        support.firePropertyChange("state", null, getCurrentCombatant());
    }

    public static void addQueueListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public static PlayerQueue getQueue() {
        return QUEUE;
    }

    public static Combatant getCurrentCombatant() {
        return QUEUE.getCurrentCombatant();
    }

}
