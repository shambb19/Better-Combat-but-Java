package util;

import _manager.EffectManager;
import _manager.EncounterManager;
import combat_object.combatant.Combatant;
import combat_object.implement.Effect;

import java.util.*;
import java.util.function.Function;

public class Locators {

    /**
     * Gets the list of combatants appropriate for the queue's current combatant
     * to target for a given attack/heal action (for example, an enemy current combatant
     * with forDamage=false will return all the enemies that need healing)
     * @param forDamage True for an attack action, false for a heal action
     * @return The appropriate list for the parameter action
     */
    public static List<Combatant> getTargetList(boolean forDamage) {
        Combatant currentCombatant = EncounterManager.currentCombatant();
        boolean isEnemy = currentCombatant.isEnemy();

        Set<Combatant> sourceList = isEnemy == forDamage ? EncounterManager.friendlies() : EncounterManager.enemies();
        return sourceList.stream().filter(c -> {
            if (forDamage) return c.isConscious();

            boolean needsHeal = c.getMaxHp() != c.getHp();
            boolean notBlocked = !EffectManager.hasEffect(c, Effect.HEAL_BLOCK);
            return needsHeal && notBlocked;
        }).toList();
    }

    /**
     * Returns an optional of the first object in the source list for which the comparator matches
     * the output of the classifier function
     * @param source the collection to be filtered
     * @param classifier a function whose output is compared to the comparator
     * @param comparator the object to be compared to the classifier's output
     * @return an optional of the first result matching the above condition
     * @param <T> the type of the source
     * @param <U> the type of the filter value to be compared
     */
    public static <T, U> Optional<T> firstMatching(Collection<T> source, Function<T, U> classifier, U comparator) {
        return source.stream().filter(t -> classifier.apply(t).equals(comparator)).findFirst();
    }

    /**
     * Returns an optional of the first object in the source list for which the comparator matches
     * the output of the classifier function
     * @param source the array to be filtered
     * @param classifier a function whose output is compared to the comparator
     * @param comparator the object to be compared to the classifier's output
     * @return an optional of the first result matching the classifier condition
     * @param <T> the type of the source
     * @param <U> the type of the filter value to be compared
     */
    public static <T, U> Optional<T> firstMatching(T[] source, Function<T, U> classifier, U comparator) {
        return Arrays.stream(source).filter(t -> classifier.apply(t).equals(comparator)).findFirst();
    }

    /**
     * Returns an optional of the first object in a list mapped to a subclass of the source list, for
     * which the comparator matches the output of the classifier function
     * @param source the collection to be filtered
     * @param type the subclass of source's type to which it will be cast
     * @param classifier a function whose output is compared to the comparator
     * @param comparator the object to be compared to the classifier's output
     * @return an optional of the first result matching the subclass and classifier condition
     * @param <T> the type of the source
     * @param <C> the subclass of T to which the source will be cast
     * @param <U> the type of the filter value to be compared
     */
    public static <T, C extends T, U> Optional<C> firstMatching(
            Collection<T> source, Class<C> type, Function<C, U> classifier, U comparator
    ) {
        return firstMatching(Filterable.of(source, type).toList(), classifier, comparator);
    }

    /**
     * Returns the constant in the provided enum for which name() equals the name parameter
     * @param name the search parameter
     * @param enumClass the class whose values are to be checked
     * @return the enum constant with the given name, else throws NoSuchElementException
     * @param <T> the type of the enum
     */
    public static <T extends Enum<T>> T enumNameSearch(String name, Class<T> enumClass) {
        return firstMatching(enumClass.getEnumConstants(), Enum::name, name).orElse(null);
    }

}
