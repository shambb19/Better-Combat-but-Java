package __main;

import character_info.combatant.Combatant;
import encounter_info.Battle;
import encounter_info.PlayerQueue;

import java.util.List;

public class EncounterInfo {

    private static Battle BATTLE = null;
    private static PlayerQueue QUEUE = null;

    public static void init(Battle battle) {
        BATTLE = battle;
    }

    public static void confirmQueueFinalized() {
        QUEUE = new PlayerQueue(BATTLE.friendlies(), BATTLE.enemies());
    }

    public static Battle getBattle() {
        return BATTLE;
    }

    public static List<Combatant> getFriendlies() {
        return BATTLE.friendlies();
    }

    public static List<Combatant> getEnemies() {
        return BATTLE.enemies();
    }

    public static PlayerQueue getQueue() {
        return QUEUE;
    }

    public static Combatant getCurrentCombatant() {
        return QUEUE.getCurrentCombatant();
    }

}
