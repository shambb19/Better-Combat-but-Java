package gui.popup.damage;

import combat.Main;
import combatants.Combatant;
import util.Locators;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class DamagePromptPopup extends JFrame {

    private final ArrayList<Combatant> targetList;
    private final Combatant currentCombatant;

    /**
     * Creates the damage prompt popup, including a tab for
     * spell input and one for weapon input.
     */
    public DamagePromptPopup() {
        currentCombatant = Main.queue.getCurrentCombatant();

        setTitle("Enter Attack Information");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(0, 1));

        targetList = Locators.getTargetList(true);

        add(getMainPanel());

        pack();
        setLocationRelativeTo(Main.menu);
    }

    private JTabbedPane getMainPanel() {
        JTabbedPane panel = new JTabbedPane();
        panel.setTabPlacement(SwingConstants.TOP);

        panel.addTab("Weapon", new WeaponPanel(getTargetComboBox(), currentCombatant, this));
        panel.addTab("Spell", new SpellPanel(getTargetComboBox(), currentCombatant, this));

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