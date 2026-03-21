package combat_menu.action_panel.damage_panel;

import character_info.combatant.Combatant;
import combat_menu.action_panel.ActionPanel;
import util.Locators;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import java.awt.*;

public class AttackPanel extends JPanel {

    private final ActionPanel root;

    private final JTabbedPane tabbedPane;

    public static AttackPanel newInstance(ActionPanel root) {
        return new AttackPanel(root);
    }

    private AttackPanel(ActionPanel root) {
        this.root = root;

        setLayout(new GridLayout(0, 1));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        addAncestorListener(resetOnSelection());

        tabbedPane = new JTabbedPane();
        tabbedPane.setTabPlacement(SwingConstants.TOP);

        resetTabbedPane();
        add(tabbedPane);
    }

    private JComboBox<Combatant> getTargetComboBox() {
        JComboBox<Combatant> box = new JComboBox<>();

        Locators.getTargetList(true).forEach(target -> {
            if (target.lifeStatus().isConscious()) {
                box.addItem(target);
            }
        });

        box.setSelectedIndex(-1);
        return box;
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