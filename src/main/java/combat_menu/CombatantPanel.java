package combat_menu;

import character_info.combatant.Combatant;
import format.swing_comp.SwingComp;
import format.swing_comp.SwingPane;

import javax.swing.*;

public class CombatantPanel extends JPanel {

    private final Combatant thisCombatant;

    private final JLabel nameLabel;
    private final JProgressBar healthBar;

    public static CombatantPanel getPanelFor(Combatant combatant) {
        return new CombatantPanel(combatant);
    }

    private CombatantPanel(Combatant thisCombatant) {
        this.thisCombatant = thisCombatant;

        SwingPane.modifiable(this).withLayout(SwingPane.FLOW_RIGHT).withEmptyBorder(8);

        nameLabel = SwingComp.label(thisCombatant.name())
                .round()
                .disabled()
                .in(this)
                .build();

        healthBar = SwingComp.progressBar(0, thisCombatant.maxHp(), thisCombatant.hp(), SwingConstants.HORIZONTAL)
                .round()
                .applied(b -> b.setStringPainted(true))
                .in(this)
                .build();

        update();
        thisCombatant.setHealthBar(healthBar);
    }

    public void update() {
        int barValue = thisCombatant.hp();
        if (thisCombatant.isEnemy() && thisCombatant.lifeStatus().isConscious())
            barValue = thisCombatant.maxHp();

        healthBar.setValue(barValue);
        healthBar.setString(thisCombatant.getHealthBarString());
        healthBar.setForeground(thisCombatant.getHealthBarColor());
        healthBar.repaint();
    }

    public void setIsTurn(boolean isTurn) {
        SwingComp<JLabel> labelModifiable = SwingComp.modifiable(nameLabel);

        if (isTurn)
            labelModifiable
                    .withForeground(thisCombatant.getHealthBarColor())
                    .withFont(SwingComp.BOLD)
                    .withBackground(UIManager.getColor("List.selectionBackground"));
        else
            labelModifiable
                    .withForeground(UIManager.getColor("Label.foreground"))
                    .withFont(SwingComp.PLAIN)
                    .withBackground(null);
    }

    public Combatant getThisCombatant() {
        return thisCombatant;
    }

}
