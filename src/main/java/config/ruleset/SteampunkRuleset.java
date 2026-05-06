package config.ruleset;

import __main.Main;
import combat_object.combatant.Combatant;
import combat_object.implement.Gun;
import combat_object.implement.Implement;
import combat_object.implement.Weapon;
import exception.InvalidParameterException;
import input.Key;
import input.Tag;
import input.TextReader;
import manager.EncounterManager;
import manager.MisfireManager;
import popup.GunAttackPopup;
import util.Message;

import java.util.EnumMap;
import java.util.List;
import java.util.Set;

public class SteampunkRuleset implements Ruleset {
    @Override public boolean logAttack(Combatant target, Implement implement, int roll) {
        Combatant attacker = EncounterManager.getCurrentCombatant();

        if (implement instanceof Weapon w) {
            return new StandardRuleset().logAttack(target, w, roll);
        }

        if (!(implement instanceof Gun gun))
            throw new ClassCastException("Ruleset$SteampunkRuleset.logAttack: Weapon or Gun expected");

        boolean continues;
        if (roll <= gun.getMisfireDc()) {
            MisfireManager.logMisfire(attacker, gun);
            Message.showAsInfoMessage(attacker + "'s " + gun.getName() + " has jammed.");
            continues = false;
        } else {
            continues = GunAttackPopup.runAndReturnHit(roll, gun);
        }

        Main.getCombatMenu().endActionState();
        Main.refreshUI();
        return continues;
    }

    @Override public void logDamage(Combatant target, Implement implement, int roll, int bonus) {
        Combatant attacker = EncounterManager.getCurrentCombatant();

        if (!implement.isManual())
            attacker.logRoll(roll, implement.getNumDice(), implement.getDieSize());

        target.damage(roll + bonus);
    }

    @Override public void validateCombatant(EnumMap<Key, Object> params, Set<Tag> tags) {
        new StandardRuleset().validateCombatant(params, tags);

        int maxHp = TextReader.getHp(params.get(Key.HP), true);
        boolean isHpValid = maxHp == 8 || tags.contains(Tag.SPECIAL);

        if (!isHpValid)
            throw new InvalidParameterException("Combatant", "maxHp", maxHp, "8 or <special tag");
    }

    @Override public List<Class<? extends Implement>> getAllowedImplementClasses() {
        return List.of(Weapon.class, Gun.class);
    }
}
