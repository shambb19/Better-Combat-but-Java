package combat_menu.popup.action_panel;

import character_info.combatant.Combatant;
import util.Locators;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import java.awt.*;
import java.util.List;

public class AttackPanel extends JPanel {

    private final List<Combatant> targetList;

    private final WeaponPanel weaponPanel;
    private final SpellPanel spellPanel;

    public static AttackPanel newInstance(ActionPanel root) {
        return new AttackPanel(root);
    }

    private AttackPanel(ActionPanel root) {
        setLayout(new GridLayout(0, 1));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        addAncestorListener(resetOnSelection());

        targetList = Locators.getTargetList(true);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setTabPlacement(SwingConstants.TOP);

        weaponPanel = WeaponPanel.newInstance(getTargetComboBox(), root);
        spellPanel = SpellPanel.newInstance(getTargetComboBox(), root);

        tabbedPane.addTab("Weapon", weaponPanel);
        tabbedPane.addTab("Spell", spellPanel);

        add(tabbedPane);
    }

    private JComboBox<Combatant> getTargetComboBox() {
        JComboBox<Combatant> box = new JComboBox<>();
        targetList.forEach(target -> {
            if (target.lifeStatus().isConscious()) {
                box.addItem(target);
            }
        });
        box.setSelectedIndex(-1);
        return box;
    }

    private AncestorListener resetOnSelection() {
        return new AncestorListener() {
            @Override
            public void ancestorAdded(AncestorEvent event) {
                weaponPanel.reset();
                spellPanel.reset();
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