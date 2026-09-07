package popup;

import _manager.CombatManager;
import _manager.EncounterManager;
import _manager.InspirationManager;
import combat_object.combatant.Combatant;
import combat_object.implement.Weapon;
import swing.custom.Popup;
import swing.custom.ValidatedField;
import util.Filterable;
import util.StringUtil;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

import static swing.fluent.SwingComp.*;
import static swing.fluent.SwingPane.*;
import static swing.ColorStyles.*;

public class NonstandardActionPopup extends Popup {

    private enum ContentType {EMPTY, INSPIRATION, DAMAGE, HEAL, KILL, FORCE_TURN}

    private Combatant selectedCombatant;

    private final JPanel dynamicPanel;

    {
        setTitle("Nonstandard Actions");

        fluent(this).arrangedAs(BORDER, 0, 12).withEmptyBorder(12);

        //
        // header
        Combatant[] availableSearchPool = Filterable.ofLists(
                EncounterManager.getFriendlies(), EncounterManager.getEnemies()
        ).toList().toArray(new Combatant[0]);
        JComboBox<Combatant> comboBox = new JComboBox<>(availableSearchPool);
        comboBox.addActionListener(e -> selectedCombatant = (Combatant) comboBox.getSelectedItem());
        comboBox.setSelectedIndex(-1);

        panelIn(this, BorderLayout.NORTH)
                .arrangedAs(HORIZONTAL_BOX, 10, 0)
                .collect("Select an actionable combatant:", spacer(6, 0), comboBox)
                .withEmptyBorder(0, 0, 8, 0);

        //
        // content
        interface ButtonMaker {
            JButton create(String name, ContentType contentType);
        }
        ButtonMaker bm = (name, contentType) -> button(name, BG_SURFACE, () -> setDynamicContent(contentType))
                .withMinimumSize(270, 35)
                .withMaximumSize(270, 35)
                .enabled(false).component();

        JPanel buttonPanel = newArrangedAs(VERTICAL_BOX, 0, 8)
                .collect(
                        bm.create("Log Inspiration Use", ContentType.INSPIRATION),
                        bm.create("Damage Manually", ContentType.DAMAGE),
                        bm.create("Heal Manually", ContentType.HEAL),
                        bm.create("Kill Combatant", ContentType.KILL),
                        bm.create("Force Combatant Turn", ContentType.FORCE_TURN)
                ).component();

        comboBox.addActionListener(e -> {
            for (Component c : buttonPanel.getComponents()) c.setEnabled(true);
        });

        dynamicPanel = newArrangedAs(BORDER).component();

        panelIn(this, BorderLayout.CENTER).arrangedAs(VERTICAL_BOX, 0, 14)
                .collect(buttonPanel, spacer(0, 8), dynamicPanel);

        pack();
        setMinimumSize(new Dimension(340, getHeight()));
        setVisible(true);
        setDynamicContent(ContentType.EMPTY);
    }

    private void logInspiration(int value) {
        InspirationManager.MANAGER.useInspiration(selectedCombatant, false);
        InspirationManager.MANAGER.submitExcessRoll(value);
        setDynamicContent(ContentType.EMPTY);
    }

    private void logDamage(int amount) {
        CombatManager.logDamage(selectedCombatant, Weapon.createManual("Admin"), 0, amount);
    }

    private void logHeal(int amount) {
        CombatManager.logHeal(selectedCombatant, amount);
    }

    private void kill(int confirm) {
        if (confirm != 67) return;
        CombatManager.logDamage(selectedCombatant, Weapon.createManual("Admin"), 0, selectedCombatant.getHp());
    }

    private void setDynamicContent(ContentType contentType) {
        dynamicPanel.removeAll();
        dynamicPanel.add(switch (contentType) {
            case EMPTY ->
                    label("Nothing to see here until a selection is made.", Font.ITALIC, 13f, FG_MUTED)
                            .applied(l -> l.setHorizontalAlignment(SwingConstants.CENTER)).component();
            case INSPIRATION -> getInspirationPanel();
            case DAMAGE -> getNumberInputPanel("Damage Amount", this::logDamage);
            case HEAL -> getNumberInputPanel("Heal Amount", this::logHeal);
            case KILL -> getNumberInputPanel("Enter '67' to Confirm Kill", this::kill);
            case FORCE_TURN -> {
                EncounterManager.getQueue().insertCombatantTurn(selectedCombatant);
                dispose();
                yield new JLabel();
            }
        }, BorderLayout.CENTER);
        dynamicPanel.revalidate();
        dynamicPanel.repaint();

        pack();
        keepOnScreen();
    }

    private void keepOnScreen() {
        Rectangle screen = getGraphicsConfiguration().getBounds();
        Rectangle bounds = getBounds();

        int x = Math.min(bounds.x, screen.x + screen.width - bounds.width);
        int y = Math.min(bounds.y, screen.y + screen.height - bounds.height);
        x = Math.max(x, screen.x);
        y = Math.max(y, screen.y);

        setLocation(x, y);
    }

    private Component getInspirationPanel() {
        boolean rollRequired = selectedCombatant.getNumInspirationUsed() >= InspirationManager.FREE_USES;
        if (!rollRequired) {
            InspirationManager.MANAGER.useInspiration(selectedCombatant, false);
            return label("Inspiration Logged!", Font.ITALIC, 14f, SUCCESS)
                    .applied(l -> l.setHorizontalAlignment(SwingConstants.CENTER))
                    .component();
        }

        JButton[] buttons = new JButton[4];
        for (int i = 1; i <= 4; i++) {
            final int roll = i;
            buttons[i - 1] = button(i, BG_DEEP, () -> logInspiration(roll)).component();
        }

        return newArrangedAs(FLOW, 8, 4).collectArr(buttons).component();
    }

    private JPanel getNumberInputPanel(String placeholder, Consumer<Integer> onConfirm) {
        ValidatedField input = new ValidatedField(placeholder, null, 1000);

        JButton confirmButton = button(
                "Confirm", SUCCESS,
                () -> {
                    if (!input.isValid()) return;
                    onConfirm.accept(StringUtil.toInt(input.getValue()));
                    setDynamicContent(ContentType.EMPTY);
                })
                .component();

        return newArrangedAs(HORIZONTAL_BOX, 10, 0)
                .collect(input, spacer(8, 0), confirmButton).component();
    }

}