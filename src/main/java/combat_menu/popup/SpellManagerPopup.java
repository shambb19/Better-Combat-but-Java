package combat_menu.popup;

import __main.manager.ConcentrationManager;
import __main.manager.EffectManager;
import combat_object.combatant.Combatant;
import util.Message;

import javax.swing.*;
import java.awt.*;

import static format.ColorStyles.CONCENTRATION;
import static format.ColorStyles.SPELL;
import static format.swing_comp.SwingComp.*;
import static format.swing_comp.SwingPane.fluent;
import static format.swing_comp.SwingPane.*;

public class SpellManagerPopup extends JDialog {

    private final JPanel effectsList;

    {
        setTitle("Spell Manager");
        fluent(this).withLabeledBorder("All Active Effects");

        JPanel concentrationsList = getConcentrationPanel();
        effectsList = getEffectPanel();

        JPanel scrollContents = newArrangedAs(ONE_COLUMN)
                .collect(
                        concentrationsList, effectsList, Box.createVerticalGlue()
                ).component();

        scrollPane(scrollContents).in(this);

        setIconImage(__main.Main.getAppIcon().getImage());
        setModal(false);
        setAlwaysOnTop(true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        pack();
        setVisible(true);
    }

    private JPanel getConcentrationPanel() {
        JPanel panel = newArrangedAs(ONE_COLUMN, 0, 5).component();

        panelIn(panel).arrangedAs(SINGLE_ROW, 15, 0).collect(
                new JLabel(),
                label("Concentrating", Font.PLAIN, 13f),
                label("Target", Font.PLAIN, 13f),
                new JLabel()
        );

        for (ConcentrationManager.Concentration concentration : ConcentrationManager.getConcentrationsAsList()) {
            JPanel concentrationRow = panelIn(panel).arrangedAs(SINGLE_ROW, 15, 0).component();

            label("").onLeft().in(concentrationRow);
            label(concentration.by()).onLeft().in(concentrationRow);
            label(concentration.on()).onLeft().in(concentrationRow);

            button("Break Concentration", CONCENTRATION,
                    () -> {
                        ConcentrationManager.breakConcentration(concentration.by());
                        panel.remove(concentrationRow);
                        removeEffects(concentration.by());
                        panel.revalidate();
                        panel.repaint();
                    }).muted().in(concentrationRow);
        }

        return panel;
    }

    private JPanel getEffectPanel() {
        JPanel panel = newArrangedAs(ONE_COLUMN, 0, 5).component();

        JPanel tableHeaders = panelIn(panel).arrangedAs(SINGLE_ROW, 15, 0).collect(
                label("Effect", Font.BOLD, 13f),
                label("Affected Combatant", Font.BOLD, 13f),
                label("Dealt By", Font.BOLD, 13f),
                new JLabel()
        ).component();
        tableHeaders.setName("Go fuck yourself, NullPointerException");

        for (EffectManager.DealtEffect effect : EffectManager.getEffectsAsList()) {
            JPanel effectRow = panelIn(panel).arrangedAs(SINGLE_ROW, 15, 0).component();
            effectRow.setName(effect.by().getName());

            label(effect.effect().getOfficialName()).onLeft().in(effectRow);
            label(effect.on()).onLeft().in(effectRow);
            label(effect.by()).muted().onLeft().in(effectRow);

            button("End Effect", SPELL,
                    () -> {
                        EffectManager.removeEffectOn(effect.by(), effect.effect());
                        panel.remove(effectRow);
                        panel.revalidate();
                        panel.repaint();
                    }).muted().in(effectRow);
        }

        return panel;
    }

    private void removeEffects(Combatant combatant) {
        for (Component c : effectsList.getComponents()) {
            if (c.getName().equals(combatant.getName())) {
                effectsList.remove(c);
                effectsList.revalidate();
                effectsList.repaint();
                return;
            }
        }

        if (areNoAvailableQueries()) {
            dispose();
        }
    }

    private static boolean areNoAvailableQueries() {
        return EffectManager.getEffectsAsList().isEmpty() && ConcentrationManager.getConcentrationsAsList().isEmpty();
    }

    public static void run() {
        if (areNoAvailableQueries()) {
            Message.showAsInfoMessage("No effects or concentrations exist yet.");
            return;
        }
        new SpellManagerPopup().setVisible(true);
    }
}
