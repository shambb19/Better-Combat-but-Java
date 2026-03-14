package combat_menu.popup.damage;

import _main.CombatMain;
import character_info.combatant.Combatant;
import util.Locators;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class DamagePromptPopup extends JFrame {

    private final ArrayList<Combatant> targetList;

    public static void run() {
        new DamagePromptPopup().setVisible(true);
    }

    private DamagePromptPopup() {
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

        panel.addTab("Weapon", new WeaponPanel(getTargetComboBox(), this));
        panel.addTab("Spell", new SpellPanel(getTargetComboBox(), this));

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