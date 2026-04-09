package __main;

import character_info.combatant.Combatant;
import combat_menu.CombatantPanel;
import combat_menu.action_panel.ActionPanel;
import damage_implements.Implement;
import damage_implements.Spell;
import damage_implements.Weapon;

import javax.swing.*;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

public class CombatManager {

    private static final AtomicReference<ActionPanel> ACTION_PANEL_ATOMIC_REFERENCE = new AtomicReference<>();

    public static void init(ActionPanel actionPanel) {
        ACTION_PANEL_ATOMIC_REFERENCE.set(actionPanel);
    }

    public static void confirmButtonStates() {
        ACTION_PANEL_ATOMIC_REFERENCE.get().returnToButtons();
        ACTION_PANEL_ATOMIC_REFERENCE.get().confirmButtonStates();
    }

    public static void cancelAction() {
        ACTION_PANEL_ATOMIC_REFERENCE.get().returnToButtons();
        Main.getMenu().setActionMode(CombatantPanel.TURN);
    }

    public static boolean logAttack(Combatant target, int roll, Implement implement) {
        Combatant attacker = EncounterInfo.getCurrentCombatant();

        if (implement instanceof Spell s && s.hasSave())
            target.logRoll(roll, 20);
        else
            attacker.logRoll(roll, 20);

        boolean hit = isAttackSuccess(attacker, target, implement, roll);
        boolean continues = hit || (implement instanceof Spell s && s.dealsHalfDamageAnyways());

        if (continues) {
            SwingUtilities.invokeLater(() -> ACTION_PANEL_ATOMIC_REFERENCE.get().promptDamageAmount(implement, target, hit));

            if (implement instanceof Spell s)
                attacker.putEffect(target, s.effect());
        }


        Main.logAction();
        Main.getMenu().setActionMode(CombatantPanel.TURN);

        return continues;
    }

    public static void logDamage(Combatant target, Implement implement, int roll, int bonus, boolean attackFailed) {
        Combatant attacker = EncounterInfo.getCurrentCombatant();

        if (!implement.isManual())
            attacker.logRoll(roll, implement.getMaxDamage());

        int damage = roll + bonus;
        if (attackFailed)
            damage /= 2;
        if (target.isHexedBy(attacker))
            damage += new Random().nextInt(0, 6);

        target.damage(damage);
        ACTION_PANEL_ATOMIC_REFERENCE.get().returnToButtons();
        Main.logAction();
    }

    public static void logHeal(Combatant target, int amount) {
        target.heal(amount);
        ACTION_PANEL_ATOMIC_REFERENCE.get().returnToButtons();
        Main.logAction();
        Main.getMenu().setActionMode(CombatantPanel.TURN);
    }

    private static boolean isAttackSuccess(Combatant attacker, Combatant target, Implement implement, int roll) {
        return switch (implement) {
            case Weapon w -> (roll + attacker.attackBonus(w)) >= target.ac();
            case Spell s when s.hasSave() -> (roll + target.mod(s.stat())) < attacker.saveDc();
            case Spell s when !s.hasSave() -> roll + attacker.spellAttackBonus() >= target.ac();
            default -> throw new ClassCastException();
        };
    }

}
