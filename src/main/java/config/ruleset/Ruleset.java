package config.ruleset;

import _manager.MisfireManager;
import combat_object.combatant.Combatant;
import combat_object.combatant.info.Class5e;
import combat_object.implement.Gun;
import combat_object.implement.Implement;
import combat_object.implement.Spell;
import combat_object.implement.Weapon;
import config.queue.PlayerQueue;
import exception.UnexpectedClassException;
import input.syntax.Key;
import input.syntax.Tag;
import swing.popup.GunAttackPopup;

import java.util.EnumMap;
import java.util.List;
import java.util.Set;

import static config.ruleset.AttackResult.*;

public interface Ruleset {

    Ruleset STANDARD_RULESET = new StandardRuleset();
    Ruleset STEAMPUNK_RULESET = new SteampunkRuleset();

    /**
     * Returns an AttackResult constant representing the result of the attack.
     * AttackResult returns SUCCEEDED if that attack hits in any context, else
     * returns the reason for the attack failing. Before returning the result,
     * logAttack logs the roll with the relevant Combatant, prompts a
     * DamageAmountPopup if SUCCEEDED is to be returned, and updates the UI
     * according to both.
     * <p>
     * The AttackResult value is determined with
     * Ruleset.getAttackResult, but the method will override this result
     * given conditions relating to the Implement, existing Effects, and
     * specific conditions of the given Ruleset
     * </p>
     * @param target the Combatant being targeted
     * @param implement the Implement used in the attack
     * @param roll the attack roll value rolled by the attacking Combatant
     *             (or by target if the Implement is a spell requiring a
     *             save roll)
     * @return the AttackResult constant relevant to this attack
     */
    AttackResult logAttack(Combatant target, Implement implement, int roll);

    /**
     * Logs the given roll with the attacker Combatant, applies any effects
     * for which dealing must be postponed until damage is calculated (i.e.
     * STAT_DROP), then damages the target Combatant for the given roll +
     * bonus
     * @param target the Combatant being damaged
     * @param implement the Implement used in the original attack
     * @param roll the damage value rolled by the attacking Combatant
     * @param bonus the bonus damage associated with the attack (as separate
     *              parameter to avoid influencing roll luck)
     */
    void logDamage(Combatant target, Implement implement, int roll, int bonus);

    /**
     * Checks the given parameters for a to-be-instantiated Combatant and
     * determines whether any invalid variables have snuck through the IDE's
     * independent value checker. As of v4.9.2 only hp is checked in this
     * method for all Ruleset implementations
     * @param params the Key, Object enum map of parameters for the
     *               to-be-instantiated Combatant
     * @param tags the set of Tags for the Combatant
     */
    void validateCombatant(EnumMap<Key, Object> params, Set<Tag> tags);

    /**
     * Creates and returns an instance of the PlayerQueue implementation
     * associated with this implementation of Ruleset, directly passing
     * the given set parameters into the PlayerQueue constructor. This
     * PlayerQueue implementation is only used if no override queue type
     * is provided in the source file; otherwise, the return value is
     * ignored
     * @param friendlies the set of friendly Combatants to be passed to
     *                   the PlayerQueue constructor
     * @param enemies the set of enemy Combatants to be passed to the
     *                PlayerQueue constructor
     * @return the new PlayerQueue implementation
     */
    PlayerQueue getPlayerQueue(Set<Combatant> friendlies, Set<Combatant> enemies);

    /**
     * Returns a list of Implement subclasses that are accepted in this
     * Ruleset implementation
     * @return the List of subclasses
     */
    List<Class<? extends Implement>> getAllowedImplementClasses();

    /**
     * Returns a list of Class5e constants that represents all player
     * classes allowed in this Ruleset implementation
     * @return the List of Class5e constants
     */
    List<Class5e> getAllowedClasses();

    /**
     * Returns a String representing the config line overriding a file
     * Ruleset to this Ruleset implementation in the format
     * "ruleset.type" (i.e. "ruleset.standard")
     * @return the String config line
     */
    String getConfigLine();

    /**
     * Returns the AttackResult constant representing the outcome of
     * an attack, specified by the given parameters. On a successful
     * attack, SUCCEEDED is returned. Otherwise, a constant describing
     * the reason for the attack rail is returned
     * @param attacker the Combatant doing the attacking
     * @param target the Combatant being attacked
     * @param implement the Implement used by the attacking Combatant for
     *                  this action
     * @param roll the attack roll value rolled by the attacking Combatant
     *             (or by the target if the Implement is a Spell requiring
     *             a save roll)
     * @return the appropriate AttackResult constant
     */
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
            default -> throw new UnexpectedClassException(implement, Weapon.class, Spell.class, Gun.class);
        };
    }

}