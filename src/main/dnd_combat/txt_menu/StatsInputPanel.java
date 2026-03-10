package txt_menu;

import character_info.Combatant;
import character_info.Stats;
import combat_menu.listener.IntegerFieldListener;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class StatsInputPanel extends JPanel {

    private final Map<JPanel, Stats.stat> statMap;

    public StatsInputPanel() {
        setLayout(new GridLayout(0, 6));

        statMap = Map.of(
                fieldTemplate("STR"), Stats.stat.STR,
                fieldTemplate("DEX"), Stats.stat.DEX,
                fieldTemplate("CON"), Stats.stat.CON,
                fieldTemplate("INT"), Stats.stat.INT,
                fieldTemplate("WIS"), Stats.stat.WIS,
                fieldTemplate("CHA"), Stats.stat.CHA
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

    public void setTo(Combatant combatant) {
        Stats stats = combatant.stats();

        for (JPanel panel : statMap.keySet()) {
            Stats.stat stat = statMap.get(panel);

            templateTextField(panel).setText(String.valueOf(stats.get(stat)));
        }
    }

}
