package combat_menu.action_panel.damage_panel;

import character_info.combatant.Combatant;
import combat_menu.action_panel.ActionPanel;
import format.swing_comp.SwingComp;
import format.swing_comp.SwingPane;
import util.Filter;
import util.Locators;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import java.util.List;

public class AttackPanel extends JPanel {

    private final ActionPanel root;

    private final JTabbedPane tabbedPane;

    public static AttackPanel newInstance(ActionPanel root) {
        return new AttackPanel(root);
    }

    private AttackPanel(ActionPanel root) {
        this.root = root;

        addAncestorListener(resetOnSelection());

        tabbedPane = new JTabbedPane(SwingConstants.TOP);

        resetTabbedPane();

        SwingPane.modifiable(this).collect(tabbedPane).withLayout(SwingPane.ONE_COLUMN).withEmptyBorder(10);
    }

    private JComboBox<Combatant> getTargetComboBox() {
        List<Combatant> targetList = Locators.getTargetList(true);
        var targets = Filter.matchingCondition(targetList, target -> target.lifeStatus().isConscious());

        return SwingComp.comboBox(targets).unselected().build();
    }

    private void resetTabbedPane() {
        tabbedPane.removeAll();
        tabbedPane.addTab("Weapon", WeaponPanel.newInstance(getTargetComboBox(), root));
        tabbedPane.addTab("Spell", SpellPanel.newInstance(getTargetComboBox(), root));
    }

    private AncestorListener resetOnSelection() {
        return new AncestorListener() {
            @Override
            public void ancestorAdded(AncestorEvent event) {
                resetTabbedPane();
            }

            @Override
            public void ancestorRemoved(AncestorEvent event) {
            }

            @Override
            public void ancestorMoved(AncestorEvent event) {
            }
        };
    }

}