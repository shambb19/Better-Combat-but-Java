package campaign_creator_menu;

import _global_list.DamageImplements;
import combat_object.combatant.*;
import combat_object.damage_implements.Spell;
import combat_object.damage_implements.Weapon;
import format.ColorStyles;
import lombok.*;
import lombok.experimental.*;
import swing.ValidatedField;
import swing.swing_comp.SwingComp;
import util.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.logging.Logger;

import static swing.swing_comp.SwingPane.*;

@FieldDefaults(level = AccessLevel.PRIVATE)
@ExtensionMethod(StringUtils.class)
public class CombatantInputPanel extends JPanel {

    final CampaignCreatorMenu root;
    final JLabel formModeLabel = new JLabel("New Combatant");
    final ValidatedField
            nameField = new ValidatedField("Name"),
            maxHpField = new ValidatedField("Max HP"),
            acField = new ValidatedField("Armor Class"),
            curHpField = new ValidatedField("Current HP"),
            levelField = new ValidatedField("Level");
    JButton confirmButton;
    JComboBox<CombatantType> typeBox;
    JComboBox<Class5e> classBox;
    StatsInputPanel statPanel;
    ListSelectionPanel<Weapon> weaponPanel;
    ListSelectionPanel<Spell> spellPanel;

    public CombatantInputPanel(CampaignCreatorMenu root) {
        this.root = root;

        List.of(maxHpField, acField, curHpField, levelField)
                .forEach(f -> f.setValidator(s -> f.getValue().toInt() != Integer.MIN_VALUE));

        modifiable(nameField).withPreferredSize(150, nameField.getPreferredSize().height);

        modifiable(this)
                .withLayout(BORDER)
                .withGaps(5, 5)
                .withLabeledBorder("Combatant Input");

        JPanel northWrapper = panelIn(this, BorderLayout.NORTH)
                .withLayout(VERTICAL_BOX)
                .component();

        buildModeRow(northWrapper);
        gapIn(8, northWrapper);
        buildNameRow(northWrapper);
        gapIn(6, northWrapper);
        buildVitalsRow(northWrapper);
        buildPcRow(northWrapper);
        gapIn(10, northWrapper);

        buildCenterPanel();
        buildSouthPanel();

        refreshFieldVisibility();
    }

    private void buildModeRow(JPanel parent) {
        JPanel row = panelIn(parent)
                .withLayout(FLOW_LEFT)
                .transparent()
                .component();

        modifiable(formModeLabel).withDerivedFont(Font.BOLD, 13f)
                .withForeground(ColorStyles.TEXT_PRIMARY)
                .in(row);
    }

    private void buildNameRow(JPanel parent) {
        JPanel row = panelIn(parent)
                .withLayout(FLOW_LEFT)
                .transparent()
                .component();

        row.add(new JLabel("Name:"));
        row.add(Box.createHorizontalStrut(6));
        row.add(nameField);

        row.add(Box.createHorizontalStrut(14));

        typeBox = SwingComp.comboBox(List.of(CombatantType.values()))
                .withPreferredSize(160, 26)
                .component();
        typeBox.addActionListener(e -> refreshFieldVisibility());
        row.add(typeBox);
    }

    private void buildVitalsRow(JPanel parent) {
        JPanel row = panelIn(parent)
                .withLayout(FLOW_LEFT)
                .transparent()
                .component();

        row.add(new JLabel("Max HP:"));
        row.add(Box.createHorizontalStrut(4));
        SwingComp.modifiable(maxHpField).in(row);

        row.add(Box.createHorizontalStrut(10));
        row.add(new JLabel("Current HP:"));
        row.add(Box.createHorizontalStrut(4));
        SwingComp.modifiable(curHpField).in(row);

        row.add(Box.createHorizontalStrut(10));
        row.add(new JLabel("AC:"));
        row.add(Box.createHorizontalStrut(4));
        SwingComp.modifiable(acField).in(row);
    }

    private void buildPcRow(JPanel parent) {
        JPanel row = panelIn(parent)
                .withLayout(FLOW_LEFT)
                .transparent()
                .component();

        row.add(new JLabel("Level:"));
        row.add(Box.createHorizontalStrut(4));
        SwingComp.modifiable(levelField).in(row);

        row.add(Box.createHorizontalStrut(10));
        row.add(new JLabel("Class:"));
        row.add(Box.createHorizontalStrut(4));

        classBox = SwingComp.comboBox(List.of(Class5e.values()))
                .withoutSelection()
                .withPreferredSize(150, 26)
                .component();
        row.add(classBox);

        row.setName("pcRow");
        parent.add(row);
    }

    private void buildCenterPanel() {
        JPanel center = panelIn(this, BorderLayout.CENTER)
                .withLayout(BORDER)
                .component();
        center.setName("centerPanel");

        statPanel = SwingComp.modifiable(StatsInputPanel.newInstance())
                .in(center, BorderLayout.WEST)
                .component();

        weaponPanel = new ListSelectionPanel<>(
                DamageImplements.toList(Weapon.class), "Weapons");
        spellPanel = new ListSelectionPanel<>(
                DamageImplements.toList(Spell.class), "Spells");

        panelIn(center, BorderLayout.CENTER)
                .collect(weaponPanel, spellPanel)
                .centered();
    }

    private void buildSouthPanel() {
        JPanel south = panelIn(this, BorderLayout.SOUTH)
                .withLayout(FLOW_LEFT)
                .component();

        confirmButton = SwingComp.button("Create", this::logAndGetCombatant)
                .component();
        south.add(confirmButton);
        south.add(Box.createHorizontalStrut(8));

        SwingComp.button("Cancel", () -> root.setInputPanelEnabled(false))
                .in(south);
    }

    public void openNew(boolean isEnemy) {
        root.setInputPanelEnabled(true);

        typeBox.setSelectedItem(isEnemy ? CombatantType.NPC_ENEMY : CombatantType.PC);
        formModeLabel.setText("New Combatant");
        formModeLabel.setForeground(ColorStyles.TEXT_PRIMARY);
        confirmButton.setText("Create");

        clearAllFields();
        nameField.setEditable(true);
        refreshFieldVisibility();
    }

    private void clearAllFields() {
        nameField.setValue("");
        List.of(maxHpField, acField, curHpField).forEach(f -> f.setValue(""));
        clearPcFields();
    }

    private void refreshFieldVisibility() {
        CombatantType type = selectedType();
        boolean isPc = !type.isNpc();

        levelField.setEnabled(isPc);
        classBox.setEnabled(isPc);
        statPanel.setVisible(isPc);

        for (Component c : ((JPanel) getComponent(0)).getComponents()) {
            if (c instanceof JPanel p && "pcRow".equals(p.getName()))
                p.setVisible(isPc);
        }

        for (Component c : getComponents()) {
            if (c instanceof JPanel p && "centerPanel".equals(p.getName()))
                p.setVisible(isPc);
        }

        revalidate();
        repaint();
    }

    private void clearPcFields() {
        List.of(levelField).forEach(f -> f.setValue(""));
        classBox.setSelectedIndex(-1);
        statPanel.reset();
        weaponPanel.reset();
        spellPanel.reset();
    }

    private CombatantType selectedType() {
        Object sel = typeBox.getSelectedItem();
        return sel instanceof CombatantType t ? t : CombatantType.PC;
    }

    public void openExisting(Combatant selection) {
        root.setInputPanelEnabled(true);

        formModeLabel.setText("Editing: " + selection.getName());
        formModeLabel.setForeground(ColorStyles.EQUATOR);
        confirmButton.setText("Save Changes");

        if (selection instanceof NPC)
            typeBox.setSelectedItem(
                    selection.isEnemy() ? CombatantType.NPC_ENEMY : CombatantType.NPC_ALLY);
        else
            typeBox.setSelectedItem(CombatantType.PC);

        nameField.setValue(selection.getName());
        nameField.setEditable(false);
        maxHpField.setValue(String.valueOf(selection.getMaxHp()));
        acField.setValue(String.valueOf(selection.getArmorClass()));
        curHpField.setValue(String.valueOf(selection.getHp()));

        clearPcFields();

        if (selection instanceof PC pc) {
            levelField.setValue(String.valueOf(pc.getStats().getLevel()));
            classBox.setSelectedItem(pc.getStats().getClass5e());
            statPanel.setTo(pc);
            weaponPanel.setTo(pc.getWeapons());
            spellPanel.setTo(pc.getSpells());
        }

        refreshFieldVisibility();
    }

    public void logAndGetCombatant() {
        try {
            String name = nameField.getValue().trim();
            int maxHp = maxHpField.getValue().toInt();
            int ac = acField.getValue().toInt();
            CombatantType type = selectedType();

            if (name.isEmpty())
                throw new IllegalArgumentException("Name cannot be empty");

            if (type.isNpc()) {
                NPC npc = NPC.create(name, maxHp, ac, type.isEnemy());
                if (!curHpField.getValue().isEmpty())
                    npc.setHp(curHpField.getValue().toInt());
                root.logCombatantCompleted(npc);
            } else {
                int level = levelField.getValue().toInt();
                Class5e class5e = (Class5e) classBox.getSelectedItem();

                if (class5e == null)
                    throw new IllegalArgumentException("Class must be selected for a Player Character");

                Stats stats = new Stats(class5e, level);
                statPanel.addTo(stats);

                PC pc = PC.create(name, maxHp, ac, stats,
                        weaponPanel.getSelected(), spellPanel.getSelected());

                if (!curHpField.getValue().isEmpty())
                    pc.setHp(curHpField.getValue().toInt());

                root.logCombatantCompleted(pc);
            }

            resetAndClose();

        } catch (Exception error) {
            String msg = error.getMessage() != null
                    ? error.getMessage()
                    : error.getClass().getSimpleName() + "; check all fields are filled";
            JOptionPane.showMessageDialog(root, "Error: " + msg,
                    CampaignCreatorMenu.TITLE, JOptionPane.ERROR_MESSAGE);
            Logger.getAnonymousLogger().severe(
                    "CombatantInputPanel.logAndGetCombatant: " + error.getClass().getSimpleName()
                            + " — " + error.getMessage());
        }
    }

    private void resetAndClose() {
        clearAllFields();
        root.setInputPanelEnabled(false);
    }

    private enum CombatantType {
        PC("Player Character"),
        NPC_ALLY("NPC Ally"),
        NPC_ENEMY("Enemy");

        final String label;

        CombatantType(String label) {
            this.label = label;
        }

        @Override public String toString() {
            return label;
        }

        boolean isNpc() {
            return this != PC;
        }

        boolean isEnemy() {
            return this == NPC_ENEMY;
        }
    }
}