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
        setLayout(new GridLayout(0, 3));

        statMap = Map.of(
                fieldTemplate("Strength"), Stats.stat.STR,
                fieldTemplate("Dexterity"), Stats.stat.DEX,
                fieldTemplate("Constitution"), Stats.stat.CON,
                fieldTemplate("Intelligence"), Stats.stat.INT,
                fieldTemplate("Wisdom"), Stats.stat.WIS,
                fieldTemplate("Charisma"), Stats.stat.CHA
        );

        statMap.forEach((panel, stat) -> add(panel));
    }

    private JPanel fieldTemplate(String name) {
        JPanel panel = new JPanel(new FlowLayout());

        JLabel label = new JLabel(name + ":");

        JTextField field = new JTextField();
        field.addKeyListener(new IntegerFieldListener());

        JCheckBox profBox = new JCheckBox("Proficient?");

        panel.add(label);
        panel.add(field);
        panel.add(profBox);

        return panel;
    }

    private int val(JPanel panel) {
        return Integer.parseInt(templateTextField(panel).getText());
    }

    private JTextField templateTextField(JPanel panel) {
        return (JTextField) panel.getComponent(1);
    }

    private boolean prof(JPanel panel) {
        return templateCheckBox(panel).isSelected();
    }

    private JCheckBox templateCheckBox(JPanel panel) {
        return (JCheckBox) panel.getComponent(2);
    }

    public void addTo(Stats directory) {
        statMap.forEach((panel, stat) -> directory.put(stat, val(panel), prof(panel)));
    }

    public void reset() {
        for (JPanel panel : statMap.keySet()) {
            templateTextField(panel).setText("");
            templateCheckBox(panel).setSelected(false);
        }
    }

    public void setTo(Combatant combatant) {
        Stats stats = combatant.stats();

        for (JPanel panel : statMap.keySet()) {
            Stats.stat stat = statMap.get(panel);

            templateTextField(panel).setText(String.valueOf(stats.get(stat)));
            templateCheckBox(panel).setSelected(stats.isProf(stat));
        }
    }

}
