package combat_menu.action_panel;

import combat_menu.action_panel.form.ActionFormPanel;

import javax.swing.*;

public record LabeledField(JLabel label, ActionFormPanel.ValidatedField field) {
}