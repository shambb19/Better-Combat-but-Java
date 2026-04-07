package combat_menu;

import __main.EncounterInfo;
import character_info.combatant.Combatant;
import format.swing_comp.SwingPane;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class EncounterListPanel extends JPanel {

    private final ArrayList<CombatantPanel> combatantPanels = new ArrayList<>();

    public static EncounterListPanel newInstance() {
        return new EncounterListPanel();
    }

    private EncounterListPanel() {
        SwingPane.modifiable(this).collect(
                        getPanel("Belligerent Enemies", EncounterInfo.getEnemies()),
                        getPanel("Party and Allies", EncounterInfo.getFriendlies())
                ).withLayout(SwingPane.VERTICAL_BOX)
                .withEmptyBorder(10);
    }

    private JPanel getPanel(String label, List<Combatant> source) {
        JPanel panel = SwingPane.panel()
                .withLayout(SwingPane.ONE_COLUMN)
                .withLabeledBorder(label)
                .build();

        source.forEach(combatant -> {
            CombatantPanel combatantPanel = CombatantPanel.getPanelFor(combatant);
            combatantPanels.add(combatantPanel);
            panel.add(combatantPanel);
        });

        return panel;
    }

    public void refresh() {
        combatantPanels.forEach(CombatantPanel::update);
        combatantPanels.forEach(CombatantPanel::repaint);
        updateActiveCombatant();
    }

    public void updateActiveCombatant() {
        Combatant currentCombatant = EncounterInfo.getCurrentCombatant();

        for (CombatantPanel panel : combatantPanels) {
            boolean isTurn = panel.getThisCombatant().equals(currentCombatant);
            panel.setIsTurn(isTurn);
        }
    }

}
