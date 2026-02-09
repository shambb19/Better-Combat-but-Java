package combat;

import com.formdev.flatlaf.intellijthemes.FlatHighContrastIJTheme;
import gui.popup.FinalizeCombatantsPopup;
import gui.Menu;
import scenarios.Battle;

import javax.swing.*;

public class Main {

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

}
