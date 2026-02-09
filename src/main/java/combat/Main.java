package combat;

import com.formdev.flatlaf.intellijthemes.FlatHighContrastIJTheme;
import gui.popup.FinalizeCombatantsPopup;
import gui.Menu;
import scenarios.Battle;

import javax.swing.*;

public class Main {

    public static final String TITLE = "Better Combat but Java";

    public static Menu menu;

    public static Battle battle = null;
    public static PlayerQueue queue;

    public static void main(String[] args) {
        FlatHighContrastIJTheme.setup();
        promptBattleScenario();
        new FinalizeCombatantsPopup().setVisible(true);
    }

    public static void start() {
        SwingUtilities.invokeLater(()-> {
            queue = new PlayerQueue(battle.getFriendlies(), battle.getEnemies());
            menu = new Menu();
            menu.setVisible(true);
        });
    }

    public static void promptBattleScenario() {
        Battle battle = null;
        while (battle == null) {
            String name = JOptionPane.showInputDialog(menu, "Enter Battle Scenario Name", "Better Combat but Java", JOptionPane.QUESTION_MESSAGE);
            battle = Battle.get(name);
        }
        Main.battle = battle;
    }

    public static void checkWinConditions() {
        if (!(battle.areAllEnemiesDefeated() || battle.areAllFriendliesDefeated())) {
            return;
        }
        if (battle.areAllFriendliesDefeated()) {
            JOptionPane.showMessageDialog(
                    menu,
                    "Your party has been defeated. " +
                            "You were " + battle.percentToVictory() + " of the way to victory. " +
                            "Ask your DM for further instructions.",
                    TITLE,
                    JOptionPane.WARNING_MESSAGE
            );
        } else {
            JOptionPane.showMessageDialog(
                    menu,
                    "You are victorious! Here are your final healths:\n" + battle.getFinalHealths() +
                            "Happy campaigning!",
                    TITLE,
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
        System.exit(0);
    }

}
