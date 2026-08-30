package config.ruleset;

import __main.Main;
import _manager.EncounterManager;
import combat_object.combatant.Combatant;
import combat_object.combatant.info.Class5e;
import combat_object.implement.Gun;
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

import static combat_object.combatant.info.Class5e.*;

public class SteampunkRuleset implements Ruleset {

    @Override public AttackResult logAttack(Combatant target, Implement implement, int roll) {
        Combatant attacker = EncounterManager.getCurrentCombatant();

        if (implement instanceof Spell)
            throw new ClassCastException("Ruleset$SteampunkRuleset.logAttack: Weapon or Gun expected");

        AttackResult result = Ruleset.getAttackResult(attacker, target, implement, roll);
        if (result == AttackResult.SUCCEEDED) {
            SwingUtilities.invokeLater(() ->
                    Main.getCombatMenu().getActionPanel().promptDamageAmount(implement, target, true));
        }

        attacker.logRoll(roll, Roll.d20());
        if (result == AttackResult.COVER_SAVE_SUCCESSFUL)
            target.logRoll(12, Roll.d(12));

        Main.getCombatMenu().endActionState();
        Main.refreshUI();
        return result;
    }

    @Override public void logDamage(Combatant target, Implement implement, int roll, int bonus) {
        Combatant attacker = EncounterManager.getCurrentCombatant();

        if (!implement.isManual())
            attacker.logRoll(roll, implement.getRoll());

        target.damage(roll + bonus);
    }

    @Override public void validateCombatant(EnumMap<Key, Object> params, Set<Tag> tags) {
        new StandardRuleset().validateCombatant(params, tags);

        int maxHp = TextReader.getHp(params.get(Key.HP), true);
        boolean isHpValid = maxHp == 8 || tags.contains(Tag.SPECIAL);

        if (!isHpValid) throw new InvalidParameterException("Combatant", "maxHp", maxHp, "8 or <special tag");
    }

    @Override public PlayerQueue getPlayerQueue(List<Combatant> friendlies, List<Combatant> enemies) {
        return new SteampunkQueue(friendlies, enemies);
    }

    @Override public List<Class<? extends Implement>> getAllowedImplementClasses() {
        return List.of(Weapon.class, Gun.class);
    }

    @Override public List<Class5e> getAllowedClasses() {
        return List.of(BARBARIAN, BARD, CLERIC, FIGHTER, PALADIN, RANGER, ROGUE, GUNSLINGER, ARTIFICER);
    }

    @Override public String getConfigLine() {
        return "ruleset.steampunk";
    }
}
