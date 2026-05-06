package util;

import combat_object.combatant.Combatant;
import combat_object.implement.Effect;
import manager.EffectManager;
import manager.EncounterManager;

import java.util.List;

public class Locators {

    public static List<Combatant> getTargetList(boolean isForDamage) {
        Combatant currentCombatant = EncounterManager.getCurrentCombatant();
        boolean isEnemy = currentCombatant.isEnemy();

        var fullList = ((isEnemy == isForDamage) ? EncounterManager.getFriendlies() : EncounterManager.getEnemies()).stream()
                .filter(c -> !c.equals(currentCombatant) && c.getLifeStatus().isConscious());

        if (isForDamage)
            return fullList.filter(c -> c.getLifeStatus().isConscious()).toList();
        else
            return fullList.filter(c -> c.getMaxHp() != c.getHp() && !EffectManager.hasEffect(c, Effect.HEAL_BLOCK)).toList();
    }

    public static <T> T getWithNameFromDirectory(List<T> source, Object obj) {
        return Filterable.of(source).firstWithToStringEquals(String.valueOf(obj));
    }

    public static <T extends Enum<T>> T enumNameSearch(String name, Class<T> enumClass) {
        return Filterable.of(enumClass.getEnumConstants()).firstWithToStringEquals(name);
    }

}
