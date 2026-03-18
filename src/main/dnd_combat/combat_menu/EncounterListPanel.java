package combat_menu;

import __main.CombatMain;
import character_info.combatant.Combatant;
import format.SwingStyles;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class EncounterListPanel extends JPanel {

    private final ArrayList<CombatantPanel> combatantPanels = new ArrayList<>();

    public static EncounterListPanel newInstance() {
        return new EncounterListPanel();
    }

    private EncounterListPanel() {
        setLayout(new GridLayout(0, 1));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        addPanel("Belligerent Enemies", CombatMain.getEnemies());
        addPanel("Party and Allies", CombatMain.getFriendlies());
    }

    public JScrollPane getScrollPane() {
        JScrollPane host = new JScrollPane();
        host.setViewportView(this);
        host.setBorder(null);
        return host;
    }

    private void addPanel(String label, List<Combatant> source) {
        JPanel panel = new JPanel(new GridLayout(0, 1));
        SwingStyles.addLabeledBorder(panel, label);

        source.forEach(combatant -> {
            CombatantPanel combatantPanel = new CombatantPanel(combatant);
            combatantPanels.add(combatantPanel);
            panel.add(combatantPanel);
        });

        add(panel);
    }

    public void refresh() {
        combatantPanels.forEach(CombatantPanel::update);
        combatantPanels.forEach(CombatantPanel::repaint);
        updateActiveCombatant();
    }

    public void updateActiveCombatant() {
        Combatant currentCombatant = CombatMain.getCurrentCombatant();

        for (CombatantPanel panel : combatantPanels) {
            if (panel.getThisCombatant().name().equals(currentCombatant.name())) {
                panel.beginTurn();
            } else {
                panel.endTurn();
            }
        }
    }

}
