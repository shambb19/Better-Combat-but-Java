package combat_menu.encounter_info;

import _manager.EncounterManager;
import combat_menu.action_panel.form.ActionFormPanel;
import combat_object.combatant.Combatant;
import lombok.*;
import lombok.experimental.*;
import org.intellij.lang.annotations.MagicConstant;
import swing.builder.ComponentBuilder;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static swing.ColorStyles.*;
import static swing.builder.Components.*;
import static swing.builder.PanelBuilder.*;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class EncounterListPanel extends JPanel {

    List<HealthBarPanel> allPanels = new ArrayList<>();

    {
        List<JComponent> friendlyPanels = getCombatantRowComponents(EncounterManager.getFriendlies());
        List<JComponent> enemyPanels = getCombatantRowComponents(EncounterManager.getEnemies());

        modifyBasicPanel(this, VERTICAL_BOX, BorderFactory.createEmptyBorder(20, 0, 0, 0));
        setPreferredSize(new Dimension(350, 0));

        addConstraintlessComponentsTo(this,
                getSectionLabel("Party and Allies"),
                friendlyPanels.toArray(new JComponent[0]),
                spacer(0, 10),
                getSectionLabel("Belligerent Enemies"),
                enemyPanels.toArray(new JComponent[0]),
                glue()
        );
    }

    private JLabel getSectionLabel(String sectionName) {
        return ComponentBuilder.createLabel(sectionName.toUpperCase())
                .fontBuilder(FontBuilder.of(Font.BOLD, 12f, FG_SECTION))
                .alignmentX(LEFT_ALIGNMENT)
                .border(BorderFactory.createEmptyBorder(10, 15, 5, 0)).build().get();
    }

    private List<JComponent> getCombatantRowComponents(List<Combatant> ref) {
        List<JComponent> comps = new ArrayList<>();
        for (Combatant c : ref) {
            HealthBarPanel healthBarPanel = new HealthBarPanel(c);
            allPanels.add(healthBarPanel);

            JSeparator divider = ComponentBuilder.modifying(new JSeparator())
                    .foreground(DIVIDER)
                    .fixedHeight(1).build().get();

            comps.add(healthBarPanel);
            comps.add(divider);
        }

        return comps;
    }

    public void updateAll() {
        allPanels.forEach(HealthBarPanel::update);
    }

    public void endActionState() {
        allPanels.forEach(HealthBarPanel::endActionState);
    }

    public void setActionMode(
            @MagicConstant(intValues = {HealthBarPanel.ATTACK, HealthBarPanel.HEAL}) int mode, ActionFormPanel dest
    ) {
        allPanels.forEach(p -> p.setActionMode(mode, dest));
    }
}