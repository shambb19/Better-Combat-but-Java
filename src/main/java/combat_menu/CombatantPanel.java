package combat_menu;

import __main.manager.EncounterManager;
import combat_object.combatant.Combatant;
import combat_object.combatant.CombatantTransferable;
import combat_object.combatant.StatTooltipWindow;
import format.ColorStyles;
import lombok.experimental.*;
import org.intellij.lang.annotations.MagicConstant;
import swing.RoundPanel;
import util.Locators;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static swing.swing_comp.SwingComp.label;
import static swing.swing_comp.SwingPane.*;

@FieldDefaults(makeFinal = true)
public class CombatantPanel extends JPanel {

    public static final int TURN = 0, ATTACK = 1, HEAL = 2;

    static int BAR_WIDTH = 110, BAR_HEIGHT = 16, ACCENT_WIDTH = 3;

    static Color ROW_DEFAULT = new Color(0x1E, 0x21, 0x28);
    static Color ROW_TURN = new Color(0x1A, 0x22, 0x30);

    Combatant combatant;
    JLabel nameLabel;
    JPanel accentBar;
    RoundPanel barTrack;
    RoundPanel barFill;
    JLabel barLabel;
    @NonFinal StatTooltipWindow activeTooltip = null;

    Timer animationTimer;
    @NonFinal int targetFillWidth = 0;

    public static CombatantPanel getPanelFor(Combatant combatant) {
        return new CombatantPanel(combatant);
    }

    private CombatantPanel(Combatant combatant) {
        this.combatant = combatant;

        modifiable(this).withLayout(BORDER)
                .withGaps(10, 0)
                .withEmptyBorder(5, 0, 5, 10)
                .withBackground(ROW_DEFAULT)
                .opaque();

        accentBar = panelIn(this, BorderLayout.WEST)
                .withPreferredSize(ACCENT_WIDTH, 0)
                .withBackground(ColorStyles.TRACK)
                .component();

        nameLabel = label(combatant.getName())
                .withForeground(new Color(0xC8, 0xcc, 0xd8))
                .withEmptyBorder(0, 10, 0, 0)
                .in(this, BorderLayout.CENTER)
                .component();

        barTrack = new RoundPanel(BAR_HEIGHT / 2, ColorStyles.TRACK);
        barTrack.setLayout(null);
        barTrack.setPreferredSize(new Dimension(BAR_WIDTH, BAR_HEIGHT));

        barFill = new RoundPanel(BAR_HEIGHT / 2, ColorStyles.HEALTHY);
        barFill.setBounds(0, 0, 0, BAR_HEIGHT);

        barLabel = new JLabel("", SwingConstants.CENTER);
        barLabel.setBounds(0, 0, BAR_WIDTH, BAR_HEIGHT);
        barLabel.setFont(new Font("SansSerif", Font.BOLD, 10));
        barLabel.setForeground(Color.WHITE);

        barTrack.add(barLabel);
        barTrack.add(barFill);

        add(barTrack, BorderLayout.EAST);

        animationTimer = new Timer(16, e -> tickAnimation());
        installDragSource();
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
        barLabel.setText(combatant.getHealthBarString());

        boolean unknown = combatant.isEnemy() && combatant.getLifeStatus().isConscious();
        if (unknown)
            targetFillWidth = BAR_WIDTH;
        else
            targetFillWidth = (int) (combatant.getHpRatio() * BAR_WIDTH);

        barFill.fireSlideAdjust(targetFillWidth, BAR_HEIGHT, barTrack);
    }

    public void setActionMode(
            @MagicConstant(intValues = {CombatantPanel.TURN, CombatantPanel.ATTACK, CombatantPanel.HEAL}) int mode
    ) {
        Combatant currentCombatant = EncounterManager.getCurrentCombatant();

        boolean isTurn = combatant == currentCombatant;

        boolean isValidDragTarget;
        if (mode == TURN)
            isValidDragTarget = false;
        else
            isValidDragTarget = Locators.getTargetList(mode == ATTACK).contains(combatant);

        if (isTurn) {
            accentBar.setBackground(ColorStyles.HEALTHY);
            nameLabel.setForeground(new Color(0xC8, 0xCC, 0xD8));
            setBackground(ROW_TURN);
        } else if (isValidDragTarget) {
            accentBar.setBackground(ColorStyles.EQUATOR);
            nameLabel.setForeground(ColorStyles.EQUATOR);
            setBackground(ROW_DEFAULT);
        } else {
            accentBar.setBackground(ColorStyles.TRACK);
            nameLabel.setForeground(new Color(0xC8, 0xCC, 0xD8));
            setBackground(ROW_DEFAULT);
        }
    }

    private void installDragSource() {
        setTransferHandler(new TransferHandler() {
            @Override
            public int getSourceActions(JComponent c) {
                return COPY;
            }

            @Override
            protected Transferable createTransferable(JComponent c) {
                return new CombatantTransferable(combatant);
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                getTransferHandler().exportAsDrag(CombatantPanel.this, e, TransferHandler.COPY);
            }
        });
    }

    private void installStatTooltip() {
        barTrack.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent e) {
                if (activeTooltip != null) activeTooltip.dispose();

                Window owner = SwingUtilities.getWindowAncestor(CombatantPanel.this);
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

}