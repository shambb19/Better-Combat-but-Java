package campaign_creator_menu.input;

import _global_list.DamageImplements;
import campaign_creator_menu.CampaignCreatorMenu;
import combat_object.combatant.Combatant;
import combat_object.combatant.NPC;
import combat_object.combatant.PC;
import combat_object.combatant.info.Class5e;
import combat_object.combatant.info.Stats;
import combat_object.damage_implements.Spell;
import combat_object.damage_implements.Weapon;
import format.swing_comp.SwingComp;
import lombok.*;
import lombok.experimental.*;
import swing_custom.ValidatedField;
import util.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static format.ColorStyles.*;
import static format.swing_comp.SwingComp.fluent;
import static format.swing_comp.SwingComp.*;
import static format.swing_comp.SwingPane.fluent;
import static format.swing_comp.SwingPane.*;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@ExtensionMethod(StringUtils.class)
public class CombatantInputPanel extends JPanel {

    CampaignCreatorMenu root;
    JLabel formModeLabel = new JLabel("New Combatant");
    ValidatedField
            nameField = new ValidatedField("Name", this::onChange),
            maxHpField = new ValidatedField("Max HP", this::onChange, 100),
            acField = new ValidatedField("Armor Class", this::onChange, 40),
            curHpField = new ValidatedField("Current HP", this::onChange, 100),
            levelField = new ValidatedField("Level", this::onChange, 20);

    StatsInputPanel statPanel = new StatsInputPanel();
    ListSelectionPanel<Weapon> weaponPanel = new ListSelectionPanel<>(DamageImplements.toList(Weapon.class), "Weapons");
    ListSelectionPanel<Spell> spellPanel = new ListSelectionPanel<>(DamageImplements.toList(Spell.class), "Spells");
    @NonFinal JButton confirmButton;
    @NonFinal JComboBox<CombatantType> typeBox;
    @NonFinal JComboBox<Class5e> classBox;

    public CombatantInputPanel(CampaignCreatorMenu root) {
        this.root = root;

        fluent(nameField).withPreferredSize(150, nameField.getPreferredSize().height);

        fluent(this).arrangedAs(BORDER, 5, 5).withLabeledBorder("Combatant Input");

        panelIn(this, BorderLayout.NORTH).arrangedAs(VERTICAL_BOX)
                .collect(
                        getModeRow(), spacer(0, 8),
                        getNameRow(), spacer(0, 6),
                        getVitalsRow(),
                        getPcRow(), spacer(0, 10)
                );

        buildCenterPanel();
        buildSouthPanel();

        refreshFieldVisibility();
    }

    private JPanel getModeRow() {
        return newArrangedAs(FLOW_LEFT)
                .collect(fluent(formModeLabel).withDerivedFont(Font.BOLD, 13f))
                .component();
    }

    private JPanel getNameRow() {
        typeBox = SwingComp.comboBox(CombatantType.values())
                .applied(b -> b.addActionListener(e -> refreshFieldVisibility()))
                .withPreferredSize(160, 26)
                .component();

        return newArrangedAs(FLOW_LEFT)
                .collect("Name:", spacer(6, 0), nameField, spacer(14, 0), typeBox)
                .transparent().component();
    }

    private JPanel getVitalsRow() {
        return newArrangedAs(FLOW_LEFT)
                .collect(
                        "Max HP:", spacer(4, 0),
                        maxHpField, spacer(10, 0),
                        "Current HP:", spacer(4, 0),
                        curHpField, spacer(10, 0),
                        "AC:", spacer(4, 0),
                        acField
                )
                .transparent().component();
    }

    private JPanel getPcRow() {
        classBox = SwingComp.comboBox(Class5e.values())
                .applied(b -> b.setSelectedIndex(-1))
                .withPreferredSize(150, 26)
                .component();

        return newArrangedAs(FLOW_LEFT)
                .collect(
                        "Level:", spacer(4, 0),
                        levelField, spacer(10, 0),
                        "Class:", spacer(4, 0),
                        classBox
                )
                .transparent()
                .applied(p -> p.setName("pcRow")).component();
    }

    private void buildCenterPanel() {
        panelIn(this, BorderLayout.CENTER).arrangedAs(BORDER)
                .borderCollect(
                        west(statPanel), center(newArrangedAs(SINGLE_ROW).collect(weaponPanel, spellPanel).component())
                ).applied(p -> p.setName("centerPanel"));
    }

    private void buildSouthPanel() {
        confirmButton = button("Create", SUCCESS, this::logAndGetCombatant).component();

        panelIn(this, BorderLayout.SOUTH).arrangedAs(FLOW_LEFT)
                .collect(
                        confirmButton, spacer(8, 0),
                        button("Cancel", CRITICAL, () -> root.setInputPanelEnabled(false))
                );
    }

    private void onChange() {
        boolean isValidInput = Stream.of(nameField, maxHpField, curHpField, acField, levelField).allMatch(ValidatedField::isValid);
        confirmButton.setEnabled(isValidInput);
    }

    public void openNew(boolean isEnemy) {
        root.setInputPanelEnabled(true);

        typeBox.setSelectedItem(isEnemy ? CombatantType.NPC_ENEMY : CombatantType.PC);
        formModeLabel.setText("New Combatant");
        formModeLabel.setForeground(FOREGROUND);
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
        Object selection = typeBox.getSelectedItem();
        return selection instanceof CombatantType t ? t : CombatantType.PC;
    }

    public void openExisting(Combatant selection) {
        root.setInputPanelEnabled(true);

        formModeLabel.setText("Editing: " + selection);
        formModeLabel.setForeground(EQUATOR);
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

        } catch (Exception e) {
            String msg = Objects.requireNonNullElse(e.getMessage(), "Error check all fields are filled");

            JOptionPane.showMessageDialog(root, "Error: " + msg,
                    CampaignCreatorMenu.TITLE, JOptionPane.ERROR_MESSAGE);
            Logger.getAnonymousLogger().severe(
                    "CombatantInputPanel.logAndGetCombatant: " + e.getClass().getSimpleName()
                            + " — " + e.getMessage());
        }
    }

    private void resetAndClose() {
        clearAllFields();
        root.setInputPanelEnabled(false);
    }

    @AllArgsConstructor private enum CombatantType {
        PC("Player Character"),
        NPC_ALLY("NPC Ally"),
        NPC_ENEMY("Enemy");

        final String label;

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