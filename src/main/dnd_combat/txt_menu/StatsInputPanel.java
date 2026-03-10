package txt_menu;

import character_info.Combatant;
import character_info.Stat;
import character_info.Stats;
import combat_menu.listener.IntegerFieldListener;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class StatsInputPanel extends JPanel {

    private final Map<JPanel, Stat> statMap;

    public StatsInputPanel() {
        setLayout(new GridLayout(0, 6));

        statMap = Map.of(
                fieldTemplate("STR"), Stat.STR,
                fieldTemplate("DEX"), Stat.DEX,
                fieldTemplate("CON"), Stat.CON,
                fieldTemplate("INT"), Stat.INT,
                fieldTemplate("WIS"), Stat.WIS,
                fieldTemplate("CHA"), Stat.CHA
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
            Stat stat = statMap.get(panel);

            templateTextField(panel).setText(String.valueOf(stats.get(stat)));
        }
    }

}
