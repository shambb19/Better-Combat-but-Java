package combat_menu.action_panel.form;

import __main.Main;
import __main.manager.CombatManager;
import __main.manager.EncounterManager;
import combat_menu.CombatantPanel;

import javax.swing.*;

public class HealFormPanel extends ActionFormPanel {

    private ValidatedField amountField;

    private HealFormPanel() {
        super("Apply Heal");
        Main.getMenu().setActionMode(CombatantPanel.HEAL);
        setTargetValidator(c -> !c.equals(EncounterManager.getCurrentCombatant()) && c.hp() != c.maxHp() && c.lifeStatus().isConscious());
    }

    public static HealFormPanel newInstance() {
        return new HealFormPanel();
    }

    @Override
    protected void buildFields(JPanel container) {
        amountField = addLabeledField(container, "Heal amount", "Enter amount…").field();
        amountField.setValidator(s -> {
            try {
                int v = Integer.parseInt(s);
                return v > 0;
            } catch (NumberFormatException e) {
                return false;
            }
        });
    }

    @Override
    protected void onConfirm() {
        int amount = Integer.parseInt(amountField.getValue());

        CombatManager.logHeal(target, amount);
        clearTarget();
        amountField.clear();
    }

    @Override
    protected boolean isInputValid() {
        return amountField.isValid();
    }
}