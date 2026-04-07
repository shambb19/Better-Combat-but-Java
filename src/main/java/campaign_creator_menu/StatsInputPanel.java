package campaign_creator_menu;

import character_info.AbilityModifier;
import character_info.Stats;
import character_info.combatant.PC;
import format.swing_comp.SwingComp;
import format.swing_comp.SwingPane;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class StatsInputPanel extends JPanel {

    private final Map<JPanel, AbilityModifier> statMap;

    public StatsInputPanel() {
        SwingPane.modifiable(this)
                .withLayout(SwingPane.ONE_COLUMN)
                .withEmptyBorder(20);

        statMap = new HashMap<>();
        for (AbilityModifier stat : AbilityModifier.values()) {
            if (stat.equals(AbilityModifier.OPTION)) continue;

            JPanel panel = fieldTemplate(stat.name());
            statMap.put(panel, stat);
        }
    }

    private JPanel fieldTemplate(String name) {
        return SwingPane.panelIn(this).collect(name + ":", SwingComp.field().onlyIntegers())
                .withLayout(SwingPane.FLOW)
                .build();
    }

    private int val(JPanel panel) {
        return Integer.parseInt(templateTextField(panel).getText());
    }

    private JTextField templateTextField(JPanel panel) {
        return (JTextField) panel.getComponent(1);
    }

    public void addTo(Stats directory) {
        statMap.forEach((panel, stat) -> directory.put(stat, val(panel)));
    }

    public void reset() {
        statMap.keySet().forEach(panel -> templateTextField(panel).setText(""));
    }

    public void setTo(PC combatant) {
        Stats stats = combatant.stats();

        for (JPanel panel : statMap.keySet()) {
            AbilityModifier stat = statMap.get(panel);

            templateTextField(panel).setText(String.valueOf(stats.get(stat)));
        }
    }

}
