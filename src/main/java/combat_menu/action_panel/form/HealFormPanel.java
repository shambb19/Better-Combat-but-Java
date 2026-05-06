package combat_menu.action_panel.form;

import __main.Main;
import combat_menu.encounter_info.HealthBarPanel;
import lombok.experimental.*;
import manager.CombatManager;
import swing.custom.ValidatedField;
import util.StringUtil;

@ExtensionMethod(StringUtil.class)
public class HealFormPanel extends ActionFormPanel {

    private ValidatedField amountField;

    public HealFormPanel() {
        super("Apply Heal");
        Main.getCombatMenu().setActionMode(HealthBarPanel.HEAL, this);
    }

    @Override
    protected void buildFields() {
        amountField = addLabeledField(fieldsPanel, "Heal Amount", "Enter Heal Amount").field();
        amountField.setValidator(s -> s.toInt() > 0);
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