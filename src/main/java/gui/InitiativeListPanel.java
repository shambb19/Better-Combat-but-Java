package gui;

import combat.Main;
import combatants.Combatant;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class InitiativeListPanel extends JPanel {

    private final ArrayList<CombatantPanel> combatantPanels = new ArrayList<>();

    public InitiativeListPanel() {
        setLayout(new GridLayout(0, 1));

        add(new JLabel("Belligerent Enemies"));
        addPanels(Main.battle.enemies());
        add(new JLabel("Party and Allies"));
        addPanels(Main.battle.friendlies());
    }

    private void addPanels(ArrayList<Combatant> combatants) {
        for (Combatant combatant : combatants) {
            CombatantPanel panel = new CombatantPanel(combatant);
            combatantPanels.add(panel);
            add(panel);
        }
        updateActiveCombatant();
    }

    public void refresh() {
        combatantPanels.forEach(CombatantPanel::update);
        updateActiveCombatant();
    }

    public void updateActiveCombatant() {
        Combatant currentCombatant = Main.queue.getCurrentCombatant();

        for (CombatantPanel panel : combatantPanels) {
            if (panel.getThisCombatant().name().equals(currentCombatant.name())) {
                panel.beginTurn();
            } else {
                panel.endTurn();
            }
        }
    }

}
