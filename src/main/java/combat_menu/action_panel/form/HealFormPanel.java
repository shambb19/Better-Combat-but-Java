package combat_menu.action_panel.form;

import __main.Main;
import __main.manager.CombatManager;
import combat_menu.encounter_info.HealthBarPanel;
import lombok.experimental.*;
import swing.custom_component.ValidatedField;
import util.StringUtils;

import javax.swing.*;

@ExtensionMethod(StringUtils.class)
public class HealFormPanel extends ActionFormPanel {

    private ValidatedField amountField;

    private HealFormPanel() {
        super("Apply Heal");
        Main.getCombatMenu().setActionMode(HealthBarPanel.HEAL, this);
    }

    public static HealFormPanel newInstance() {
        return new HealFormPanel();
    }

    @Override
    protected void buildFields(JPanel container) {
        amountField = addLabeledField(container, "Heal amount", "Enter amount…").field();
        amountField.setValidator(s -> s.toInt() != Integer.MIN_VALUE);
    }

    @Override
    protected void onConfirm() {
        int amount = amountField.getValue().toInt();

        CombatManager.logHeal(target, amount);
        amountField.clear();
    }

    @Override
    protected boolean isInputValid() {
        return amountField.isValid();
    }
}