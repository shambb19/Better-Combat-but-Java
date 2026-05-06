package config.ruleset;

import __main.Main;
import combat_object.combatant.Combatant;
import combat_object.combatant.info.AbilityModifier;
import combat_object.implement.Effect;
import combat_object.implement.Implement;
import combat_object.implement.Spell;
import combat_object.implement.Weapon;
import exception.InvalidParameterException;
import input.Key;
import input.Tag;
import input.TextReader;
import manager.EffectManager;
import manager.EncounterManager;

import javax.swing.*;
import java.util.EnumMap;
import java.util.List;
import java.util.Set;

public class StandardRuleset implements Ruleset {
    @Override public boolean logAttack(Combatant target, Implement implement, int roll) {
        Combatant attacker = EncounterManager.getCurrentCombatant();

        boolean autoHits = implement instanceof Spell s && s.doesNotRequireAttackRoll();

        if (!autoHits) {
            if (implement instanceof Spell s && s.hasSave()) {
                target.logRoll(roll, 1, 20);

                EffectManager.removeEffectOn(target, Effect.PENALTY_SAVE);
            } else
                attacker.logRoll(roll, 1, 20);
        }

        boolean hit = Ruleset.isAttackSuccess(attacker, target, implement, roll);
        boolean continues = hit;
        if (!hit && implement instanceof Spell s) {
            continues = s.dealsHalfDamageAnyways() || autoHits;
        }

        if (continues) {
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
            attacker.logRoll(roll, implement.getNumDice(), implement.getDieSize());

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

    @Override public List<Class<? extends Implement>> getAllowedImplementClasses() {
        return List.of(Weapon.class, Spell.class);
    }
}
