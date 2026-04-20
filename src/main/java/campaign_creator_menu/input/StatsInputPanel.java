package campaign_creator_menu.input;

import combat_object.combatant.PC;
import combat_object.combatant.info.AbilityModifier;
import combat_object.combatant.info.Stats;
import format.swing_comp.SwingPane;
import lombok.experimental.*;
import swing_custom.ValidatedField;
import util.StringUtils;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

@ExtensionMethod(StringUtils.class)
public class StatsInputPanel extends JPanel {

    private final Map<ValidatedField, AbilityModifier> statMap = new HashMap<>();

    {
        SwingPane.fluent(this).arrangedAs(SwingPane.ONE_COLUMN)
                .withEmptyBorder(20, 20, 20, 20);

        for (AbilityModifier stat : AbilityModifier.values()) {
            if (stat.equals(AbilityModifier.OPTION)) continue;

            ValidatedField panel = fieldTemplate(stat.name());
            panel.setAlignmentX(RIGHT_ALIGNMENT);
            statMap.put(panel, stat);
        }
    }

    private ValidatedField fieldTemplate(String name) {
        ValidatedField inputField = new ValidatedField(name, null, 30);

        SwingPane.panelIn(this).collect(name + ":", inputField).arrangedAs(SwingPane.FLOW);

        return inputField;
    }

    public void addTo(Stats directory) {
        statMap.forEach((validatedField, stat) -> directory.put(stat, validatedField.getValue().toInt()));
    }

    public void reset() {
        for (ValidatedField validatedField : statMap.keySet())
            validatedField.setValue("");
    }

    public void setTo(PC combatant) {
        Stats stats = combatant.getStats();

        for (ValidatedField panel : statMap.keySet()) {
            AbilityModifier stat = statMap.get(panel);

            panel.setValue(String.valueOf(stats.get(stat)));
        }
    }

}
