package txt_input;

import character_info.combatant.Combatant;
import character_info.combatant.NPC;
import character_info.combatant.PC;
import scenario_info.Battle;
import scenario_info.Scenario;

import java.util.ArrayList;

public class Txt5e {

    private final ArrayList<Object> objects;

    public Txt5e(ArrayList<Object> objects) {
        this.objects = objects;
    }

    public <T> ArrayList<T> toList(Class<T> collection) {
        ArrayList<T> list = new ArrayList<>();

        objects.forEach(object -> {
            if (collection.isInstance(object)) {
                list.add(collection.cast(object));
            }
        });

        return list;
    }

    public Battle getBattle() {
        ArrayList<Combatant> allCombatants = toList(Combatant.class);

        ArrayList<Combatant> friendlies = new ArrayList<>(allCombatants.stream()
                .filter(combatant -> {
                    if (combatant instanceof NPC npc) {
                        return npc.isAlly();
                    }
                    return combatant instanceof PC;
                }).toList());

        ArrayList<Combatant> enemies = new ArrayList<>(allCombatants.stream()
                .filter(combatant -> combatant instanceof NPC npc && npc.isEnemy())
                .toList());

        ArrayList<Scenario> scenarios = toList(Scenario.class);

        return new Battle(friendlies, friendlies, enemies, enemies, scenarios);
    }

}
