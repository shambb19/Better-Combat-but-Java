package combat_menu.popup;

import __main.manager.EffectManager;
import format.ColorStyles;
import swing.swing_comp.SwingPane;
import util.Message;

import javax.swing.*;
import java.awt.*;

import static swing.swing_comp.SwingComp.button;
import static swing.swing_comp.SwingComp.scrollPane;
import static swing.swing_comp.SwingPane.*;

public class EffectManagerPopup extends JDialog {

    {
        setTitle("Effect Manager");
        SwingPane.fluent(this).arrangedAs(BORDER, 0, 10).withLabeledBorder("All Active Effects");

        JPanel effectsList = newArrangedAs(ONE_COLUMN, 0, 5).component();

        panelIn(effectsList).arrangedAs(SINGLE_ROW, 15, 0).collect(
                label("Effect").withDerivedFont(Font.BOLD, 13f),
                label("Affected Combatant").withDerivedFont(Font.BOLD, 13f),
                label("Dealt By").withDerivedFont(Font.BOLD, 13f),
                label(null)
        );

        for (EffectManager.DealtEffect effect : EffectManager.getEffectsAsList()) {
            JPanel effectPanel = panelIn(effectsList).arrangedAs(SINGLE_ROW, 15, 0).component();

            label(effect.effect().getOfficialName()).onLeft().in(effectPanel);
            label(effect.affected()).onLeft().in(effectPanel);
            label(effect.by(), ColorStyles.TEXT_MUTED).onLeft().in(effectPanel);

            button("End Effect", ColorStyles.SELECTION,
                    () -> {
                        EffectManager.removeEffectOn(effect.by(), effect.effect());
                        effectsList.remove(effectPanel);
                        effectsList.revalidate();
                        effectsList.repaint();
                    }).muted().in(effectPanel);
        }

        effectsList.add(Box.createVerticalGlue());
        scrollPane(effectsList)
                .in(this, BorderLayout.CENTER);

        setIconImage(__main.Main.getAppIcon().getImage());
        setModal(false);
        setAlwaysOnTop(true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        pack();
        setVisible(true);
    }

    public static void run() {
        if (EffectManager.getEffectsAsList().isEmpty()) {
            Message.template("No effects have been dealt yet.");
            return;
        }
        new EffectManagerPopup().setVisible(true);
    }
}
