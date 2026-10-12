package swing.popup;

import _manager.ConcentrationManager;
import _manager.EffectManager;
import combat_object.combatant.Combatant;
import swing.custom.Popup;
import swing.builder.ButtonBuilder;
import swing.builder.ComponentBuilder;
import util.Message;

import javax.swing.*;
import java.awt.*;

import static swing.ColorStyles.*;
import static swing.builder.Components.*;
import static swing.builder.PanelBuilder.*;

public class SpellManagerPopup extends Popup {

    private final JPanel effectsList;

    {
        setTitle("Spell Manager");

        JPanel concentrationsList = getConcentrationPanel();
        effectsList = getEffectPanel();

        createPanel(ONE_COLUMN, concentrationsList, effectsList, glue())
                .build().getAsScrollerIn(this.getRootPane(), null);

        pack();
        setVisible(true);
    }

    private JPanel getConcentrationPanel() {
        JPanel panel = createPanel(ONE_COLUMN).vgap(5).build().get();

        createPanel(SINGLE_ROW,
                new JLabel(),
                ComponentBuilder.createLabel("Concentrating", Font.PLAIN, 13f, FOREGROUND),
                ComponentBuilder.createLabel("Target", Font.PLAIN, 13f, FOREGROUND),
                new JLabel()
                ).hgap(15).build().putIn(panel);

        for (ConcentrationManager.Concentration concentration : ConcentrationManager.getConcentrations()) {
            JPanel concentrationRow = createPanel(SINGLE_ROW).hgap(15).build().putIn(panel);

            for (Object o : new Object[]{"", concentration.by(), concentration.on()})
                ComponentBuilder.createLabel(o.toString())
                        .alignmentX(LEFT_ALIGNMENT)
                        .build().putIn(concentrationRow);

            ButtonBuilder.createButton("Break Concentration", CONCENTRATION, () -> {
                        ConcentrationManager.breakConcentration(concentration.by());
                        panel.remove(concentrationRow);
                        removeEffects(concentration.by());
                        panel.revalidate();
                        panel.repaint();
                    }).foreground(FG_MUTED)
                    .build().putIn(concentrationRow);

        }

        return panel;
    }

    private JPanel getEffectPanel() {
        JPanel panel = createPanel(ONE_COLUMN).vgap(5).build().get();

        JPanel tableHeaders = createPanel(SINGLE_ROW,
                ComponentBuilder.createLabel("Effect", Font.BOLD, 13f, FOREGROUND),
                ComponentBuilder.createLabel("Affected Combatant", Font.BOLD, 13f, FOREGROUND),
                ComponentBuilder.createLabel("Dealt By", Font.BOLD, 13f, FOREGROUND)
                ).hgap(15).build().putIn(panel);
        tableHeaders.setName("Go fuck yourself, NullPointerException");

        for (EffectManager.DealtEffect effect : EffectManager.getEffects()) {
            JPanel effectRow = createPanel(SINGLE_ROW).hgap(15).build().putIn(panel);
            effectRow.setName(effect.by().getName());

            addConstraintlessComponentsTo(effectRow,
                    ComponentBuilder.createLabel(effect.effect().getOfficialName()).alignmentX(LEFT_ALIGNMENT),
                    ComponentBuilder.createLabel(effect.on().toString()).alignmentX(LEFT_ALIGNMENT),
                    ComponentBuilder.createLabel(effect.by().toString()).foreground(FG_MUTED).alignmentX(LEFT_ALIGNMENT)
            );

            ButtonBuilder.createButton("End Effect", SPELL, () -> {
                EffectManager.removeEffect(effect.by(), effect.effect());
                panel.remove(effectRow);
                panel.revalidate();
                panel.repaint();
            }).build().validated();

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

        if (areNoAvailableQueries()) dispose();
    }

    private static boolean areNoAvailableQueries() {
        return EffectManager.getEffects().isEmpty() && ConcentrationManager.getConcentrations().isEmpty();
    }

    public static void run() {
        if (areNoAvailableQueries()) {
            Message.showAsInfoMessage("No effects or concentrations exist yet.");
            return;
        }
        new SpellManagerPopup();
    }
}
