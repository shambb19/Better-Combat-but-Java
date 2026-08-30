package config.ruleset;

import __main.Main;
import _manager.EffectManager;
import _manager.EncounterManager;
import combat_object.combatant.Combatant;
import combat_object.combatant.info.AbilityModifier;
import combat_object.combatant.info.Class5e;
import combat_object.implement.Effect;
import combat_object.implement.Implement;
import combat_object.implement.Spell;
import combat_object.implement.Weapon;
import config.queue.PlayerQueue;
import config.queue.SteampunkQueue;
import exception.InvalidParameterException;
import input.TextReader;
import input.syntax.Key;
import input.syntax.Tag;
import util.Roll;

import javax.swing.*;
import java.util.EnumMap;
import java.util.List;
import java.util.Set;

import static config.ruleset.AttackResult.SUCCEEDED;

public class StandardRuleset implements Ruleset {

    @Override public AttackResult logAttack(Combatant target, Implement implement, int roll) {
        Combatant attacker = EncounterManager.getCurrentCombatant();

        boolean autoHits = implement instanceof Spell s && s.doesNotRequireAttackRoll();
        if (!autoHits) {
            if (implement instanceof Spell s && s.hasSave()) {
                target.logRoll(roll, Roll.d20());

                EffectManager.removeEffect(target, Effect.PENALTY_SAVE);
            } else {
                attacker.logRoll(roll, Roll.d20());
            }
        }

        AttackResult continues = Ruleset.getAttackResult(attacker, target, implement, roll);
        boolean hit = continues == SUCCEEDED;

        if (!hit && implement instanceof Spell s) {
            if (s.dealsHalfDamageAnyways() || autoHits) continues = SUCCEEDED;
        }

        if (continues == SUCCEEDED) {
            EffectManager.logEffect(target, attacker, implement);

            SwingUtilities.invokeLater(() ->
                    Main.getCombatMenu().getActionPanel().promptDamageAmount(implement, target, hit));
        }

        Main.getCombatMenu().endActionState();
        Main.refreshUI();
        return continues;
    }

    @Override public void logDamage(Combatant target, Implement implement, int roll, int bonus) {
        Combatant attacker = EncounterManager.getCurrentCombatant();

        if (!implement.isManual())
            attacker.logRoll(roll, implement.getRoll());

        if (implement.effectEquals(Effect.STAT_DROP)) {
            target.getStats().put(AbilityModifier.INT, 1);
            target.getStats().put(AbilityModifier.CHA, 1);
        } else if (implement.effectEquals(Effect.HEAL_SELF)) {
            attacker.heal(roll);
        }
        target.damage(roll + bonus);
    }

    @Override public void validateCombatant(EnumMap<Key, Object> params, Set<Tag> tags) {
        Object key = params.get(Key.HP);
        int hp = TextReader.getHp(key, false);
        int maxHp = TextReader.getHp(key, true);

        if (hp > maxHp) throw new InvalidParameterException("PC", "hp", hp, "hp <= hpMax");
    }

    @Override public PlayerQueue getPlayerQueue(List<Combatant> friendlies, List<Combatant> enemies) {
        return new SteampunkQueue(friendlies, enemies);
    }

    @Override public List<Class<? extends Implement>> getAllowedImplementClasses() {
        return List.of(Weapon.class, Spell.class);
    }

    @Override public List<Class5e> getAllowedClasses() {
        return List.of(Class5e.values());
    }

    @Override public String getConfigLine() {
        return "ruleset.standard";
    }
}
