package swing.popup;

import _manager.CombatManager;
import _manager.EncounterManager;
import _manager.InspirationManager;
import combat_object.combatant.Combatant;
import combat_object.implement.Weapon;
import swing.custom.Popup;
import swing.custom.ValidatedField;
import swing.builder.ButtonBuilder;
import swing.builder.ComponentBuilder;
import util.Filterable;
import util.StringUtil;

import javax.swing.*;
import java.awt.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import static swing.ColorStyles.*;
import static swing.builder.Components.*;
import static swing.builder.PanelBuilder.*;

public class NonstandardActionPopup extends Popup {

    private enum ContentType {EMPTY, INSPIRATION, DAMAGE, HEAL, KILL, FORCE_TURN}

    private Combatant selectedCombatant;

    private final JPanel dynamicPanel;

    {
        setTitle("Nonstandard Actions");

        modifyingPanel(this).layoutType(BORDER).vgap(12)
                .border(BorderFactory.createEmptyBorder(12, 12, 12, 12)).build().validated();

        //
        // header
        Combatant[] availableSearchPool = Filterable.ofLists(
                EncounterManager.getFriendlies(), EncounterManager.getEnemies()
        ).toList().toArray(new Combatant[0]);
        JComboBox<Combatant> comboBox = ComponentBuilder.createComboBox(
                availableSearchPool,
                b -> selectedCombatant = (Combatant) b.getSelectedItem(),
                null
        ).build().get();

        createPanel(HORIZONTAL_BOX, "Select an actionable combatant:", spacer(6, 0), comboBox)
                .hgap(10)
                .border(BorderFactory.createEmptyBorder(0, 0, 8, 0))
                .build().putIn(this, BorderLayout.NORTH);

        //
        // content
        BiFunction<String, ContentType, JButton> bm = (name, contentType) ->
                ButtonBuilder.createButton(name, BG_SURFACE, () -> setDynamicContent(contentType))
                        .fixedSize(new Dimension(270, 35))
                        .enabled(false).build().get();

        JPanel buttonPanel = createPanel(VERTICAL_BOX,
                bm.apply("Log Inspiration Use", ContentType.INSPIRATION),
                bm.apply("Damage Manually", ContentType.DAMAGE),
                bm.apply("Heal Manually", ContentType.HEAL),
                bm.apply("Kill Combatant", ContentType.KILL),
                bm.apply("Force Combatant Turn", ContentType.FORCE_TURN)
        ).vgap(8).build().get();

        comboBox.addActionListener(e -> {
            for (Component c : buttonPanel.getComponents()) c.setEnabled(true);
        });

        dynamicPanel = createPanel(BORDER).build().get();

        createPanel(VERTICAL_BOX,
                buttonPanel, spacer(0, 8), dynamicPanel
                ).vgap(14)
                .build().putIn(this, BorderLayout.CENTER);

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
                    ComponentBuilder.createLabel("Nothing to see here until a selection is made",
                            Font.ITALIC, 13f, FG_MUTED).build().get();
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
            return ComponentBuilder.getBasicLabel("Inspiration Logged!", Font.ITALIC, 14f, SUCCESS);
        }

        JButton[] buttons = new JButton[4];
        for (int i = 1; i <= 4; i++) {
            final int roll = i;
            buttons[i - 1] = ButtonBuilder.createButton(String.valueOf(i), BACKGROUND, () -> logInspiration(roll))
                    .fontBuilder(FontBuilder.of(Font.BOLD, 14f)).build().get();
        }

        return createPanel(FLOW_LEFT, "Select d4 Inspiration Roll: ", buttons).hgap(8).vgap(4).build().get();
    }

    private JPanel getNumberInputPanel(String placeholder, Consumer<Integer> onConfirm) {
        ValidatedField input = new ValidatedField(placeholder, null, 1000);

        JButton confirmButton = ButtonBuilder.createButton("Confirm", SUCCESS, () -> {
            if (!input.isInputValid()) return;
            onConfirm.accept(StringUtil.toInt(input.getValue()));
            setDynamicContent(ContentType.EMPTY);
        }).build().get();

        input.allowEnter(confirmButton::doClick);

        return createPanel(HORIZONTAL_BOX,
                input, spacer(8, 0), confirmButton
                ).hgap(10).build().get();
    }

}