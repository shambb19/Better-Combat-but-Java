package config.ruleset;

import combat_object.combatant.Combatant;
import combat_object.implement.Implement;
import combat_object.implement.Spell;
import input.Key;
import input.Tag;

import java.util.EnumMap;
import java.util.List;
import java.util.Set;

public interface Ruleset {

    Ruleset STANDARD_RULESET = new StandardRuleset();
    Ruleset STEAMPUNK_RULESET = new SteampunkRuleset();

    boolean logAttack(Combatant target, Implement implement, int roll);

    void logDamage(Combatant target, Implement implement, int roll, int bonus);

    void validateCombatant(EnumMap<Key, Object> params, Set<Tag> tags);

    List<Class<? extends Implement>> getAllowedImplementClasses();

    static boolean isAttackSuccess(Combatant attacker, Combatant target, Implement implement, int roll) {
        return switch (implement) {
            case Spell s when s.doesNotRequireAttackRoll() -> true;
            case Spell s when s.hasSave() -> target.getSaveThrow(roll, implement) < attacker.getStats().saveDc();
            default -> attacker.getAttackRoll(roll, implement) >= target.getArmorClass();
        };
    }

}