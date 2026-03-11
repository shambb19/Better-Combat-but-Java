package combat_menu;

import _main.CombatMain;
import character_info.combatant.Combatant;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class InitiativeListPanel extends JPanel {

    private final ArrayList<CombatantPanel> combatantPanels = new ArrayList<>();

    public InitiativeListPanel() {
        setLayout(new GridLayout(0, 1));

        add(new JLabel("Belligerent Enemies"));
        addPanels(CombatMain.BATTLE.enemies());
        add(new JLabel("Party and Allies"));
        addPanels(CombatMain.BATTLE.friendlies());
    }

    public JScrollPane getScrollPane() {
        JScrollPane host = new JScrollPane();
        host.setViewportView(this);
        host.setBorder(null);
        return host;
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
        combatantPanels.forEach(CombatantPanel::repaint);
        updateActiveCombatant();
    }

    public void updateActiveCombatant() {
        Combatant currentCombatant = CombatMain.QUEUE.getCurrentCombatant();

        for (CombatantPanel panel : combatantPanels) {
            if (panel.getThisCombatant().name().equals(currentCombatant.name())) {
                panel.beginTurn();
            } else {
                panel.endTurn();
            }
        }
    }

}
