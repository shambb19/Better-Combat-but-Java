package util;

import combat.Main;
import combatants.Combatant;

import javax.swing.*;

public class Message {

    public static void informAttackFail() {
        JOptionPane.showMessageDialog(
                Main.menu,
                "The attack does not hit.",
                Main.TITLE,
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    public static void informHexSuccess(Combatant target) {
        JOptionPane.showMessageDialog(
                Main.menu,
                target.name() + " has been successfully hexed. " +
                        "They will now receive 1d6 additional damage from " +
                        Main.queue.getCurrentCombatant().name() + ".",
                Main.TITLE,
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    public static void informIllusion(Combatant target) {
        JOptionPane.showMessageDialog(
                Main.menu,
                target.name() + " is now under the illusion of your choice. " +
                        "Remember that or something, idk.",
                Main.TITLE,
                JOptionPane.INFORMATION_MESSAGE
        );
    }

}
