package util;

import __main.manager.EffectManager;
import __main.manager.EncounterManager;
import combat_object.combatant.Combatant;
import combat_object.damage_implements.Effect;

import java.util.Arrays;
import java.util.List;

@lombok.experimental.ExtensionMethod(Filter.class)
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
        return source.firstWithToStringEquals(obj.toString());
    }

    public static <T extends Enum<T>> T enumNameSearch(String name, Class<T> enumClass) {
        return Arrays.asList(enumClass.getEnumConstants()).firstWithToStringEquals(name);
    }

}
