package campaign_creator;

import _global_list.DamageImplements;
import character_info.Class5e;
import character_info.Stats;
import character_info.combatant.Combatant;
import character_info.combatant.NPC;
import character_info.combatant.PC;
import combat_menu.listener.IntegerFieldListener;
import damage_implements.Spell;
import damage_implements.Weapon;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class CombatantInputPanel extends JPanel {

    private final TxtMenu root;

    private final JCheckBox isNpcBox;
    private final JCheckBox isEnemyBox;

    private final JTextField nameField = new JTextField(10);
    private final JTextField maxHpField = new JTextField(5);
    private final JTextField acField = new JTextField(5);
    private final JTextField curHpField = new JTextField(5);
    private final JTextField levelField = new JTextField(5);
    private final JComboBox<Class5e> classBox = new JComboBox<>(Class5e.values());

    private final StatsInputPanel statPanel;
    private final ListSelectionPanel<Weapon> weaponPanel;
    private final ListSelectionPanel<Spell> spellPanel;

    private final JPanel listSplitPane;

    public CombatantInputPanel(TxtMenu root) {
        this.root = root;
        setLayout(new BorderLayout(5, 5));

        isNpcBox = new JCheckBox("NPC?");
        isNpcBox.addActionListener(e -> toggleNpc(isNpcBox.isSelected()));
        isEnemyBox = new JCheckBox("Enemy?");

        List.of(
                maxHpField, acField, curHpField, levelField
        ).forEach(field -> field.addKeyListener(new IntegerFieldListener()));
        classBox.setSelectedIndex(-1);

        statPanel = new StatsInputPanel();
        weaponPanel = new ListSelectionPanel<>(DamageImplements.toList(Weapon.class), "Weapons");
        spellPanel = new ListSelectionPanel<>(DamageImplements.toList(Spell.class), "Spells");

        JPanel headerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 5, 2, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addComponent(headerPanel, new JLabel("Name:"), gbc, 0, 0);
        gbc.weightx = 1.0;
        addComponent(headerPanel, nameField, gbc, 1, 0);
        gbc.weightx = 0;
        addComponent(headerPanel, isNpcBox, gbc, 2, 0);
        addComponent(headerPanel, isEnemyBox, gbc, 3, 0);

        addComponent(headerPanel, new JLabel("Max HP:"), gbc, 0, 1);
        addComponent(headerPanel, maxHpField, gbc, 1, 1);
        addComponent(headerPanel, new JLabel("AC:"), gbc, 2, 1);
        addComponent(headerPanel, acField, gbc, 3, 1);

        addComponent(headerPanel, new JLabel("Cur HP:"), gbc, 0, 2);
        addComponent(headerPanel, curHpField, gbc, 1, 2);
        addComponent(headerPanel, new JLabel("Level:"), gbc, 2, 2);
        addComponent(headerPanel, levelField, gbc, 3, 2);

        addComponent(headerPanel, new JLabel("Class:"), gbc, 0, 3);
        gbc.gridwidth = 3;
        addComponent(headerPanel, classBox, gbc, 1, 3);
        gbc.gridwidth = 1;

        listSplitPane = new JPanel(new GridLayout(0, 1));
        listSplitPane.add(weaponPanel);
        listSplitPane.add(spellPanel);

        JPanel northWrapper = new JPanel(new BorderLayout());
        northWrapper.add(headerPanel, BorderLayout.NORTH);
        northWrapper.add(statPanel, BorderLayout.SOUTH);

        JButton okButton = new JButton("Confirm");
        okButton.addActionListener(e -> logAndGetCombatant());
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> root.setInputPanelEnabled(false));

        add(northWrapper, BorderLayout.NORTH);
        add(listSplitPane, BorderLayout.CENTER);
        add(getButtonPanel(okButton, cancelButton), BorderLayout.SOUTH);
    }

    private void addComponent(JPanel p, Component c, GridBagConstraints gbc, int x, int y) {
        gbc.gridx = x;
        gbc.gridy = y;
        p.add(c, gbc);
    }

    public void logAndGetCombatant() {
        try {
            String name = nameField.getText();
            int hp = Integer.parseInt(maxHpField.getText());
            int ac = Integer.parseInt(acField.getText());
            boolean isEnemy = isEnemyBox.isSelected();

            nameField.setEnabled(true);

            if (isNpcBox.isSelected()) {
                root.logCombatantCompleted(new NPC(name, hp, ac, isEnemy));
                resetAndClose();
                return;
            }

            int level = Integer.parseInt(levelField.getText());
            Class5e class5e = (Class5e) classBox.getSelectedItem();
            Stats stats = new Stats(class5e, level);
            statPanel.addTo(stats);

            Combatant combatant = new PC(name, hp, ac, stats, weaponPanel.getSelected(), spellPanel.getSelected());

            if (!curHpField.getText().isEmpty()) {
                combatant.setHealth(Integer.parseInt(curHpField.getText()));
            }

            root.logCombatantCompleted(combatant);
            resetAndClose();
        } catch (Exception error) {
            JOptionPane.showMessageDialog(root, "Error: " + error.getMessage(), TxtMenu.TITLE, JOptionPane.ERROR_MESSAGE);
            error.printStackTrace();
        }
    }

    private void resetAndClose() {
        toggleNpc(false);
        root.setInputPanelEnabled(false);
    }

    public void openNew(boolean isEnemy) {
        root.setInputPanelEnabled(true);
        isNpcBox.setSelected(isEnemy);
        isEnemyBox.setSelected(isEnemy);

        List.of(
            nameField, maxHpField, acField, curHpField, levelField
        ).forEach(field -> field.setText(""));
        nameField.setEnabled(true);

        statPanel.reset();
        classBox.setSelectedIndex(-1);
        weaponPanel.reset();
        spellPanel.reset();

        toggleNpc(isEnemy);
    }

    public void openExisting(Combatant selection) {
        root.setInputPanelEnabled(true);
        isNpcBox.setSelected(selection instanceof NPC);
        isEnemyBox.setSelected(selection.isEnemy());

        nameField.setText(selection.name());
        nameField.setEnabled(false);
        maxHpField.setText(String.valueOf(selection.maxHp()));
        acField.setText(String.valueOf(selection.ac()));

        if (selection instanceof PC pc) {
            curHpField.setText(String.valueOf(pc.hp()));
            levelField.setText(String.valueOf(pc.stats().level()));
            classBox.setSelectedItem(pc.stats().class5e());
            statPanel.setTo(pc);
            weaponPanel.setTo(pc.weapons());
            spellPanel.setTo(pc.spells());
        }

        toggleNpc(selection instanceof NPC);
    }

    private void toggleNpc(boolean isNpc) {
        statPanel.setVisible(!isNpc);
        listSplitPane.setVisible(!isNpc);

        curHpField.setEnabled(!isNpc);
        levelField.setEnabled(!isNpc);
        classBox.setEnabled(!isNpc);

        revalidate();
        repaint();
    }

    private JPanel getButtonPanel(JButton okButton, JButton cancelButton) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.add(okButton);
        panel.add(cancelButton);
        return panel;
    }
}