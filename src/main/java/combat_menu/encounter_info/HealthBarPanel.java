package combat_menu.encounter_info;

import __main.manager.EncounterManager;
import combat_menu.action_panel.form.ActionFormPanel;
import combat_object.combatant.Combatant;
import format.ColorStyles;
import format.swing_comp.SwingPane;
import lombok.experimental.*;
import org.intellij.lang.annotations.MagicConstant;
import swing_custom.RoundPanel;
import util.Locators;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Optional;

import static format.swing_comp.SwingComp.label;
import static format.swing_comp.SwingPane.BORDER;
import static format.swing_comp.SwingPane.panelIn;

@FieldDefaults(makeFinal = true)
public class HealthBarPanel extends JPanel {

    public static final int ATTACK = 1, HEAL = 2;

    static int BAR_WIDTH = 110, BAR_HEIGHT = 16, ACCENT_WIDTH = 3;

    static Color ROW_TURN = new Color(0x1A, 0x22, 0x30);

    Combatant combatant;
    JLabel nameLabel;
    JPanel accentBar;
    RoundPanel barTrack;
    RoundPanel barFill;
    @NonFinal StatTooltipWindow activeTooltip = null;
    @NonFinal MouseListener actionSelectionListener;

    Timer animationTimer;
    @NonFinal int targetFillWidth = 0;

    public static HealthBarPanel getPanelFor(Combatant combatant) {
        return new HealthBarPanel(combatant);
    }

    private HealthBarPanel(Combatant combatant) {
        this.combatant = combatant;

        SwingPane.fluent(this).arrangedAs(BORDER, 10, 0)
                .withEmptyBorder(5, 0, 5, 10)
                .withBackground(ColorStyles.BACKGROUND);

        accentBar = panelIn(this, BorderLayout.WEST)
                .withPreferredSize(ACCENT_WIDTH, 0)
                .withBackground(ColorStyles.TRACK)
                .component();

        nameLabel = label(combatant, new Color(0xC8, 0xcc, 0xd8))
                .withEmptyBorder(0, 10, 0, 0)
                .in(this, BorderLayout.CENTER);

        barTrack = new RoundPanel(BAR_HEIGHT / 2, ColorStyles.TRACK);
        barTrack.setLayout(null);
        barTrack.setPreferredSize(new Dimension(BAR_WIDTH, BAR_HEIGHT));

        barFill = new RoundPanel(BAR_HEIGHT / 2, ColorStyles.HEALTHY);
        barFill.setBounds(0, 0, 0, BAR_HEIGHT);
        barTrack.add(barFill);

        add(barTrack, BorderLayout.EAST);

        animationTimer = new Timer(16, e -> tickAnimation());
        installStatTooltip();
        update();
    }

    private void tickAnimation() {
        int current = barFill.getWidth();
        if (current == targetFillWidth) {
            animationTimer.stop();
            return;
        }
        int step = Math.max(1, Math.abs(targetFillWidth - current) / 6);
        int next = current < targetFillWidth ? current + step : current - step;
        barFill.setBounds(0, 0, next, BAR_HEIGHT);
        barTrack.repaint();
    }

    public void update() {
        barFill.setFill(combatant.getHealthBarColor());

        boolean unknown = combatant.isEnemy() && combatant.getLifeStatus().isConscious();
        if (unknown)
            targetFillWidth = BAR_WIDTH;
        else
            targetFillWidth = (int) (combatant.getHpRatio() * BAR_WIDTH);

        barFill.fireSlideAdjust(targetFillWidth, BAR_HEIGHT, barTrack);
    }

    public void endActionState() {
        if (combatant == EncounterManager.getCurrentCombatant()) {
            accentBar.setBackground(ColorStyles.HEALTHY);
            nameLabel.setForeground(new Color(0xc8, 0xcc, 0xd8));
            setBackground(ROW_TURN);
        } else {
            accentBar.setBackground(ColorStyles.TRACK);
            nameLabel.setForeground(new Color(0xc8, 0xcc, 0xd8));
            setBackground(ColorStyles.BACKGROUND);
        }
    }

    public void setActionMode(
            @MagicConstant(intValues = {HealthBarPanel.ATTACK, HealthBarPanel.HEAL}) int mode, ActionFormPanel dest
    ) {
        boolean isValidTarget = Locators.getTargetList(mode == ATTACK).contains(combatant);

        if (isValidTarget) {
            accentBar.setBackground(ColorStyles.EQUATOR);
            nameLabel.setForeground(ColorStyles.EQUATOR);
            setBackground(ColorStyles.BACKGROUND);
        } else {
            endActionState();
        }
        setActionSelectionState(isValidTarget, dest);
    }

    private void installStatTooltip() {
        barTrack.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent e) {
                Optional.ofNullable(activeTooltip).ifPresent(Window::dispose);

                Window owner = SwingUtilities.getWindowAncestor(HealthBarPanel.this);
                activeTooltip = new StatTooltipWindow(owner, combatant);

                Point barOnScreen = barTrack.getLocationOnScreen();
                int tx = barOnScreen.x - activeTooltip.getWidth() - 8;
                int ty = barOnScreen.y + (barTrack.getHeight() - activeTooltip.getHeight()) / 2;

                if (tx < 0) tx = barOnScreen.x + barTrack.getWidth() + 8;

                activeTooltip.setLocation(tx, ty);
                activeTooltip.setVisible(true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (activeTooltip != null) {
                    activeTooltip.dispose();
                    activeTooltip = null;
                }
            }
        });
    }

    private void setActionSelectionState(boolean isSelectable, ActionFormPanel dest) {
        actionSelectionListener = new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                dest.submitTarget(combatant);
            }
        };

        if (isSelectable)
            addMouseListener(actionSelectionListener);
        else
            removeMouseListener(actionSelectionListener);
    }

}