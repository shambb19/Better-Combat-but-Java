package campaign_creator_menu;

import _global_list.DamageImplements;
import character_info.Class5e;
import character_info.Stats;
import character_info.combatant.Combatant;
import character_info.combatant.NPC;
import character_info.combatant.PC;
import damage_implements.Spell;
import damage_implements.Weapon;
import format.swing_comp.SwingComp;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.logging.Logger;

import static format.swing_comp.SwingComp.*;
import static format.swing_comp.SwingPane.modifiable;
import static format.swing_comp.SwingPane.*;

public class CombatantInputPanel extends JPanel {

    private final CampaignCreatorMenu root;

    private final JCheckBox isNpcBox, isEnemyBox;

    private final JTextField nameField = new JTextField(10);
    private final JTextField maxHpField = new JTextField(5);
    private final JTextField acField = new JTextField(5);
    private final JTextField curHpField = new JTextField(5);
    private final JTextField levelField = new JTextField(5);
    private final JComboBox<Class5e> classBox;

    private final StatsInputPanel statPanel;
    private final ListSelectionPanel<Weapon> weaponPanel;
    private final ListSelectionPanel<Spell> spellPanel;

    public CombatantInputPanel(CampaignCreatorMenu root) {
        this.root = root;

        // 1. Initialize Root Panel
        modifiable(this).withLayout(BORDER).withGaps(5, 5).withLabeledBorder("Combatant Input");

        // 2. Setup North Section (Header + Stats)
        JPanel northWrapper = panelIn(this, BorderLayout.NORTH).withLayout(VERTICAL_BOX).build();
        JPanel headerPanel = panelIn(northWrapper).withLayout(VERTICAL_BOX).build();

        // --- Row 1: Name and Logic Checks ---
        JPanel nameRow = panelIn(headerPanel)
                .collect("Name:", SwingComp.modifiable(nameField).withSize(150, 26))
                .withLayout(FLOW_LEFT)
                .withSize(150, 26)
                .transparent()
                .build();

        isNpcBox = checkBox("NPC?")
                .in(nameRow)
                .build();

        isEnemyBox = checkBox("Enemy?")
                .withAction(box -> {
                    if (box.isSelected())
                        isNpcBox.setSelected(true);
                })
                .in(nameRow)
                .build();

        gapIn(30, headerPanel);

        // --- Row 2: Stats Grid ---
        classBox = comboBox(List.of(Class5e.values()))
                .unselected()
                .withSize(150, 26)
                .enabledWhen(isNpcBox, false)
                .build();

        panelIn(headerPanel).collect(
                        "Max HP:", SwingComp.modifiable(maxHpField).onlyIntegers(),
                        "AC:", SwingComp.modifiable(acField).onlyIntegers(),
                        "Cur HP:", SwingComp.modifiable(curHpField).onlyIntegers().enabledWhen(isNpcBox, false),
                        "Level:", SwingComp.modifiable(levelField).onlyIntegers().enabledWhen(isNpcBox, false),
                        "Class:", classBox)
                .withLayout(FLOW_LEFT);

        gapIn(25, northWrapper);

        // Center Section (Stats on Left; Weapons, Spells on Right)
        JPanel centerPanel = panelIn(this, BorderLayout.CENTER)
                .withLayout(BORDER)
                .visibleWhen(isNpcBox, false)
                .build();

        statPanel = SwingComp.modifiable(new StatsInputPanel())
                .visibleWhen(isNpcBox, false)
                .in(centerPanel, BorderLayout.WEST)
                .build();

        weaponPanel = new ListSelectionPanel<>(DamageImplements.toList(Weapon.class), "Weapons");
        spellPanel = new ListSelectionPanel<>(DamageImplements.toList(Spell.class), "Spells");

        panelIn(centerPanel, BorderLayout.CENTER)
                .collect(weaponPanel, spellPanel)
                .visibleWhen(isNpcBox, false)
                .build();

        // 4. Setup South Section (Actions)
        button("Create", this::logAndGetCombatant)
                .withCancelOption(() -> root.setInputPanelEnabled(false))
                .in(this, BorderLayout.SOUTH);
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

            if (!curHpField.getText().isEmpty())
                combatant.setHealth(Integer.parseInt(curHpField.getText()));

            root.logCombatantCompleted(combatant);
            resetAndClose();
        } catch (Exception error) {
            JOptionPane.showMessageDialog(root, "Error: " + error.getMessage(), CampaignCreatorMenu.TITLE, JOptionPane.ERROR_MESSAGE);
            Logger.getAnonymousLogger().severe("logAndGetCombatant in CombatantInputPanel: error parsing fields");
        }
    }

    private void resetAndClose() {
        isNpcBox.setSelected(false);
        root.setInputPanelEnabled(false);
    }

    public void openNew(boolean isEnemy) {
        root.setInputPanelEnabled(true);
        isNpcBox.setSelected(isEnemy);
        isEnemyBox.setSelected(isEnemy);

        List.of(nameField, maxHpField, acField, curHpField, levelField)
                .forEach(field -> field.setText(""));
        nameField.setEnabled(true);

        statPanel.reset();
        classBox.setSelectedIndex(-1);
        weaponPanel.reset();
        spellPanel.reset();
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
    }
}