package combat_menu.popup.damage;

import __main.CombatMain;
import character_info.combatant.Combatant;
import util.Locators;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AttackPopup extends JFrame {

    private final List<Combatant> targetList;

    public static void run() {
        new AttackPopup().setVisible(true);
    }

    private AttackPopup() {
        setTitle("Enter Attack Information");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(0, 1));

        targetList = Locators.getTargetList(true);

        add(getMainPanel());

        pack();
        setLocationRelativeTo(CombatMain.COMBAT_MENU);
    }

    private JTabbedPane getMainPanel() {
        JTabbedPane panel = new JTabbedPane();
        panel.setTabPlacement(SwingConstants.TOP);

        panel.addTab("Weapon", WeaponPanel.get(getTargetComboBox(), this));
        panel.addTab("Spell", SpellPanel.get(getTargetComboBox(), this));

        return panel;
    }

    private JComboBox<Combatant> getTargetComboBox() {
        JComboBox<Combatant> comboBox = new JComboBox<>();
        comboBox.putClientProperty("JComponent.roundRect", true);
        targetList.forEach(target -> {
            if (target.lifeStatus().isConscious()) {
                comboBox.addItem(target);
            }
        });
        return comboBox;
    }
}