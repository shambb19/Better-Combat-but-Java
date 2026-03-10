package txt_menu;

import character_info.Class5e;
import character_info.Combatant;
import character_info.Stats;
import combat_menu.listener.IntegerFieldListener;
import damage_implements.Spell;
import damage_implements.Weapon;

import static util.Swing.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CombatantInputPanel extends JPanel {

    private final TxtMenu root;

    private final JCheckBox isNpcBox;
    private final JCheckBox isEnemyBox;

    private final JPanel namePanel;
    private final JPanel hpPanel;
    private final JPanel acPanel;

    private final JPanel hpCurPanel;
    private final JPanel levelPanel;
    private final JPanel spellCastPanel;
    private final StatsInputPanel statPanel;

    private final ListSelectionPanel<Weapon> weaponPanel;
    private final ListSelectionPanel<Spell> spellPanel;

    private final JPanel infoAll;
    private final JPanel infoPC;

    public CombatantInputPanel(TxtMenu root) {
        this.root = root;

        setLayout(new GridLayout(0, 1));

        isNpcBox = new JCheckBox("NPC?");
        isNpcBox.addActionListener(e -> toggleNpc(isNpcBox.isSelected()));

        isEnemyBox = new JCheckBox("Enemy?");

        namePanel = fieldTemplate("Name", false);
        hpPanel = fieldTemplate("Max HP", true);
        acPanel = fieldTemplate("Armor Class", true);

        hpCurPanel = fieldTemplate("Current HP (Optional)", true);
        levelPanel = fieldTemplate("Level", true);
        spellCastPanel = getClassPanel();
        statPanel = new StatsInputPanel();

        weaponPanel = new ListSelectionPanel<>(Weapon.getAllAsList(), "Weapons");
        spellPanel = new ListSelectionPanel<>(Spell.getAllAsList(), "Spells");

        JButton okButton = new JButton("Confirm");
        okButton.addActionListener(e -> logAndGetCombatant());

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> root.setInputPanelEnabled(false));

        infoAll = asFlowLayout(
                List.of(namePanel, isNpcBox, isEnemyBox, hpPanel, acPanel)
        );
        infoPC = asFlowLayout(
                hpCurPanel, levelPanel, spellCastPanel
        );

        add(infoAll);
        add(infoPC);
        add(statPanel);
        add(weaponPanel);
        add(spellPanel);
        add(getButtonPanel(okButton, cancelButton));
    }

    public void logAndGetCombatant() {
        try {
            Combatant combatant;

            boolean isEnemy = isEnemyBox.isSelected();
            String name = getTemplateValue(namePanel);
            int hp = Integer.parseInt(getTemplateValue(hpPanel));
            int ac = Integer.parseInt(getTemplateValue(acPanel));

            getTemplateField(namePanel).setEnabled(true);

            combatant = new Combatant(name, hp, ac, isEnemy);

            if (isNpcBox.isSelected()) {
                root.logCombatantCompleted(combatant);
                toggleNpc(false);
                root.setInputPanelEnabled(false);
                return;
            }

            int level = Integer.parseInt(getTemplateValue(levelPanel));
            Class5e class5e = getCharacterClass();
            Stats stats = new Stats(class5e, level);
            statPanel.addTo(stats);

            ArrayList<Weapon> weapons = weaponPanel.getSelected();
            ArrayList<Spell> spells = spellPanel.getSelected();

            String hpCur = getTemplateValue(hpCurPanel);

            combatant = new Combatant(
                    name, hp, ac,
                    stats, weapons, spells
            );

            if (!hpCur.isEmpty()) {
                combatant.setHealth(Integer.parseInt(hpCur));
            }

            root.logCombatantCompleted(combatant);
            toggleNpc(false);
            root.setInputPanelEnabled(false);
        } catch (Exception error) {
            JOptionPane.showMessageDialog(
                    root,
                    "Error Loading Combatant: " + error.getMessage(),
                    TxtMenu.TITLE,
                    JOptionPane.ERROR_MESSAGE
            );
            error.printStackTrace();
        }
    }

    public void openNew(boolean isEnemy) {
        root.setInputPanelEnabled(true);

        isNpcBox.setSelected(isEnemy);
        isEnemyBox.setSelected(isEnemy);

        List.of(
            namePanel, hpPanel, acPanel, hpCurPanel, levelPanel
        ).forEach(component -> getTemplateField(component).setText(""));

        statPanel.reset();
        resetClassPanel();

        weaponPanel.reset();
        spellPanel.reset();

        toggleNpc(isEnemy);
    }

    public void openExisting(Combatant selection) {
        root.setInputPanelEnabled(true);

        isNpcBox.setSelected(selection.isNPC());
        isEnemyBox.setSelected(selection.isEnemy());

        getTemplateField(namePanel).setText(selection.name());
        getTemplateField(namePanel).setEnabled(false);

        getTemplateField(hpPanel).setText(String.valueOf(selection.maxHp()));
        getTemplateField(acPanel).setText(String.valueOf(selection.ac()));

        if (!selection.isNPC()) {
            getTemplateField(hpCurPanel).setText(String.valueOf(selection.hp()));
            getTemplateField(levelPanel).setText(String.valueOf(selection.level()));

            statPanel.setTo(selection);
            setClassPanelTo(selection);

            weaponPanel.setTo(selection.weapons());
            spellPanel.setTo(selection.spells());
        }

        toggleNpc(selection.isNPC());
    }

    private JPanel fieldTemplate(String name, boolean isInputNum) {
        JPanel panel = new JPanel(new FlowLayout());

        JLabel label = new JLabel(name + ":");

        JTextField field = new JTextField();
        if (isInputNum) {
            field.addKeyListener(new IntegerFieldListener());
        }

        panel.add(label);
        panel.add(field);
        return panel;
    }

    private JTextField getTemplateField(JPanel panel) {
        return (JTextField) panel.getComponent(1);
    }

    private String getTemplateValue(JPanel panel) {
        return ((JTextField) panel.getComponent(1)).getText();
    }

    @SuppressWarnings("unchecked")
    private Class5e getCharacterClass() {
        return (Class5e) ((JComboBox<Stats.stat>) spellCastPanel.getComponent(1)).getSelectedItem();
    }

    @SuppressWarnings("unchecked")
    private void setClassPanelTo(Combatant combatant) {
        ((JComboBox<Class5e>) spellCastPanel.getComponent(1)).setSelectedItem(combatant.stats().class5e());
    }

    @SuppressWarnings("unchecked")
    private void resetClassPanel() {
        ((JComboBox<Class5e>) spellCastPanel.getComponent(1)).setSelectedIndex(-1);
    }

    private JPanel getClassPanel() {
        JPanel panel = new JPanel(new FlowLayout());

        JLabel label = new JLabel("Class:");

        JComboBox<Class5e> statsBox = new JComboBox<>();
        for (Class5e class5e : Class5e.values()) {
            statsBox.addItem(class5e);
        }
        statsBox.setSelectedIndex(-1);

        panel.add(label);
        panel.add(statsBox);

        return panel;
    }

    private JPanel getButtonPanel(JButton okButton, JButton cancelButton) {
        JPanel panel = new JPanel(new FlowLayout());

        panel.add(okButton);
        panel.add(cancelButton);

        return panel;
    }

    private void toggleNpc(boolean isNpc) {
        infoAll.setVisible(true);

        infoPC.setVisible(!isNpc);
        statPanel.setVisible(!isNpc);
        weaponPanel.setVisible(!isNpc);
        spellPanel.setVisible(!isNpc);

        revalidate();
        repaint();
    }

}