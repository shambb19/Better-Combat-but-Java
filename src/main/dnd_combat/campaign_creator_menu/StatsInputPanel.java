package campaign_creator_menu;

import character_info.AbilityModifier;
import character_info.Stats;
import character_info.combatant.PC;
import combat_menu.listener.IntegerFieldListener;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class StatsInputPanel extends JPanel {

    private final Map<JPanel, AbilityModifier> statMap;

    public StatsInputPanel() {
        setLayout(new GridLayout(0, 6));

        statMap = Map.of(
                fieldTemplate("STR"), AbilityModifier.STR,
                fieldTemplate("DEX"), AbilityModifier.DEX,
                fieldTemplate("CON"), AbilityModifier.CON,
                fieldTemplate("INT"), AbilityModifier.INT,
                fieldTemplate("WIS"), AbilityModifier.WIS,
                fieldTemplate("CHA"), AbilityModifier.CHA
        );

        statMap.forEach((panel, stat) -> add(panel));
    }

    private JPanel fieldTemplate(String name) {
        JPanel panel = new JPanel(new FlowLayout());

        JLabel label = new JLabel(name + ":");

        JTextField field = new JTextField();
        field.addKeyListener(new IntegerFieldListener());

        panel.add(label);
        panel.add(field);

        return panel;
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
        for (JPanel panel : statMap.keySet()) {
            templateTextField(panel).setText("");
        }
    }

    public void setTo(PC combatant) {
        Stats stats = combatant.stats();

        for (JPanel panel : statMap.keySet()) {
            AbilityModifier stat = statMap.get(panel);

            templateTextField(panel).setText(String.valueOf(stats.get(stat)));
        }
    }

}
