package combat_menu.encounter_info;

import _manager.EncounterManager;
import combat_menu.action_panel.form.ActionFormPanel;
import combat_object.combatant.Combatant;
import lombok.*;
import lombok.experimental.*;
import org.intellij.lang.annotations.MagicConstant;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static swing.ColorStyles.DIVIDER;
import static swing.ColorStyles.FG_SECTION;
import static swing.fluent.SwingComp.fluent;
import static swing.fluent.SwingComp.glue;
import static swing.fluent.SwingComp.label;
import static swing.fluent.SwingComp.spacer;
import static swing.fluent.SwingPane.VERTICAL_BOX;
import static swing.fluent.SwingPane.fluent;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class EncounterListPanel extends JPanel {

    List<HealthBarPanel> allPanels = new ArrayList<>();

    {
        List<JComponent> friendlyPanels = getCombatantRowComponents(EncounterManager.getFriendlies());
        List<JComponent> enemyPanels = getCombatantRowComponents(EncounterManager.getEnemies());

        fluent(this).arrangedAs(VERTICAL_BOX)
                .collect(
                        getSectionLabel("Party and Allies"),
                        friendlyPanels.toArray(new JComponent[0]),
                        spacer(0, 10),
                        getSectionLabel("Belligerent Enemies"),
                        enemyPanels.toArray(new JComponent[0]),
                        glue()
                )
                .withPreferredSize(350, 0)
                .withEmptyBorder(20, 0, 0, 0)
                .opaque();
    }

    private JLabel getSectionLabel(String sectionName) {
        return label(sectionName.toUpperCase(), Font.BOLD, 12f, FG_SECTION)
                .onLeft().withEmptyBorder(10, 15, 5, 0).in(this);
    }

    private List<JComponent> getCombatantRowComponents(List<Combatant> ref) {
        List<JComponent> comps = new ArrayList<>();
        for (Combatant c : ref) {
            HealthBarPanel healthBarPanel = (HealthBarPanel) fluent(HealthBarPanel.getPanelFor(c))
                    .withMaximumSize(Integer.MAX_VALUE, 40).onLeft().component();
            allPanels.add(healthBarPanel);

            JSeparator divider = fluent(new JSeparator())
                    .withForeground(DIVIDER)
                    .withMaximumSize(Integer.MAX_VALUE, 1).component();

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