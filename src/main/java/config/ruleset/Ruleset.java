package config.ruleset;

import _manager.MisfireManager;
import combat_object.combatant.Combatant;
import combat_object.combatant.info.Class5e;
import combat_object.implement.Gun;
import combat_object.implement.Implement;
import combat_object.implement.Spell;
import combat_object.implement.Weapon;
import config.queue.PlayerQueue;
import input.syntax.Key;
import input.syntax.Tag;
import popup.GunAttackPopup;

import java.util.EnumMap;
import java.util.List;
import java.util.Set;

import static config.ruleset.AttackResult.*;

public interface Ruleset {

    Ruleset STANDARD_RULESET = new StandardRuleset();
    Ruleset STEAMPUNK_RULESET = new SteampunkRuleset();

    AttackResult logAttack(Combatant target, Implement implement, int roll);

    void logDamage(Combatant target, Implement implement, int roll, int bonus);

    void validateCombatant(EnumMap<Key, Object> params, Set<Tag> tags);

    PlayerQueue getPlayerQueue(List<Combatant> friendlies, List<Combatant> enemies);

    List<Class<? extends Implement>> getAllowedImplementClasses();

    List<Class5e> getAllowedClasses();

    String getConfigLine();

    static AttackResult getAttackResult(Combatant attacker, Combatant target, Implement implement, int roll) {
        boolean armorClassAttackSucceeds = attacker.getAttackRoll(roll, implement) >= target.getArmorClass();
        AttackResult armorClassCheck = armorClassAttackSucceeds ? SUCCEEDED : AC_NOT_MET;

        return switch (implement) {
            case Spell s when s.doesNotRequireAttackRoll() -> SUCCEEDED;
            case Spell s when s.hasSave() -> {
                if (target.getSaveThrow(roll, implement) < attacker.getStats().saveDc()) yield SUCCEEDED;
                yield TARGET_SAVE_SUCCESSFUL;
            }
            case Spell ignored -> armorClassCheck;
            case Weapon ignored -> armorClassCheck;
            case Gun g -> {
                if (roll <= g.getMisfireDc()) {
                    MisfireManager.logMisfire(attacker, g);
                    yield MISFIRE;
                }
                if (roll < g.getShortHitDc()) yield SHOT_MISSED;
                yield GunAttackPopup.runAndReturnHit(roll, g);
            }
            default -> throw new ClassCastException("Ruleset.getAttackResult: Weapon, Spell, or Gun expected");
        };
    }

}