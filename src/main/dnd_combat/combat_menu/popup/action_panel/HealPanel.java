package combat_menu.popup.action_panel;

import __main.CombatMain;
import character_info.combatant.Combatant;
import character_info.combatant.NPC;
import format.ColorStyle;
import format.SwingStyles;
import util.Locators;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import java.awt.*;
import java.util.List;
import java.util.Vector;

public class HealPanel extends JPanel {

    private final ActionPanel root;

    private static final NPC DEFAULT_SELECTION = new NPC("Select a Combatant", 0, 0, false);

    private final JComboBox<Combatant> targetComboBox;
    private final JLabel minLabel;
    private final JLabel maxLabel;
    private final JSlider healthSlider;
    private final JButton okButton;

    public static HealPanel newInstance(ActionPanel root) {
        return new HealPanel(root);
    }

    private HealPanel(ActionPanel root) {
        this.root = root;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(40, 10, 40, 10));
        addAncestorListener(resetOnSelection());

        targetComboBox = new JComboBox<>(new Vector<>(getEligibleCombatants()));
        setupComboBoxAppearance();
        targetComboBox.addActionListener(e -> logComboBoxChange());

        minLabel = new JLabel("--");
        minLabel.setForeground(ColorStyle.ORANGE_ISH_RED.getColor());

        maxLabel = new JLabel("--");
        maxLabel.setForeground(ColorStyle.GREEN_APPLE.getColor());

        healthSlider = new JSlider(0, 0, 0);
        healthSlider.setEnabled(false);
        healthSlider.setMajorTickSpacing(1);
        healthSlider.setSnapToTicks(true);
        healthSlider.addChangeListener(e -> logSliderUpdate());

        okButton = new JButton("Select a Combatant");
        okButton.setBackground(ColorStyle.DARKER_GREEN.getColor());
        okButton.setForeground(Color.BLACK);
        okButton.setEnabled(false);

        JPanel confirmCancelPanel = SwingStyles.getConfirmCancelPanel(
                okButton,
                e -> logAndClose(),
                e -> root.returnToButtons()
        );

        add(new JLabel("Select Target:"));
        add(targetComboBox);
        add(getSliderPanel());
        add(confirmCancelPanel);
    }

    private void setupComboBoxAppearance() {
        targetComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Combatant c) {
                    setText(String.format("%s (%d/%d HP)", c.name(), c.hp(), c.maxHp()));
                } else if (value == null) {
                    setText("Choose a combatant...");
                }
                return this;
            }
        });
        targetComboBox.setSelectedIndex(-1);
    }

    private JPanel getSliderPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setBorder(BorderFactory.createCompoundBorder(
                new EmptyBorder(10, 10, 10, 10),
                new TitledBorder(
                        BorderFactory.createEtchedBorder(),
                        "Drag to Change Heal Amount",
                        TitledBorder.LEFT,
                        TitledBorder.TOP
                )
        ));

        panel.add(minLabel);
        panel.add(healthSlider);
        panel.add(maxLabel);
        return panel;
    }

    private void logAndClose() {
        try {
            Combatant target = (Combatant) targetComboBox.getSelectedItem();
            assert target != null;

            int healAmount = healthSlider.getValue() - target.hp();
            target.heal(healAmount);

            CombatMain.COMBAT_MENU.update();
            root.returnToButtons();
        } catch (NullPointerException ignored) {
        }
    }

    private void logSliderUpdate() {
        Combatant target = (Combatant) targetComboBox.getSelectedItem();
        if (target == null) {
            return;
        }

        int healAmount = healthSlider.getValue() - target.hp();
        okButton.setText("Heal " + target.name() + " for " + healAmount + " hp");
    }

    private void logComboBoxChange() {
        Combatant selected = (Combatant) targetComboBox.getSelectedItem();

        if (selected == null || selected == DEFAULT_SELECTION) {
            return;
        }

        int hp = selected.hp();
        int hpMax = selected.maxHp();

        healthSlider.setEnabled(true);
        healthSlider.setMinimum(hp);
        healthSlider.setMaximum(hpMax);
        healthSlider.setValue(hp + 1);

        minLabel.setText(hp + " (Current)");
        maxLabel.setText(hpMax + " (Max)");

        okButton.setEnabled(true);

        logSliderUpdate();
    }

    private List<Combatant> getEligibleCombatants() {
        List<Combatant> allOnTeam = Locators.getTargetList(false);

        return allOnTeam.stream()
                .filter(combatant -> {
                    if (combatant == DEFAULT_SELECTION) {
                        return true;
                    }
                    return combatant.hp() < combatant.maxHp();
                })
                .toList();
    }

    private AncestorListener resetOnSelection() {
        return new AncestorListener() {
            @Override
            public void ancestorAdded(AncestorEvent event) {
                targetComboBox.setSelectedIndex(-1);
                minLabel.setText("--");
                maxLabel.setText("--");
                healthSlider.setValue(0);
                healthSlider.setEnabled(false);
                okButton.setText("Select a Combatant");
            }

            @Override
            public void ancestorRemoved(AncestorEvent event) {
            }

            @Override
            public void ancestorMoved(AncestorEvent event) {
            }
        };
    }

}