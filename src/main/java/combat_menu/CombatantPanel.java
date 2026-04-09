package combat_menu;

import __main.manager.EncounterManager;
import character_info.combatant.Combatant;
import character_info.combatant.CombatantTransferable;
import format.ColorStyles;
import org.intellij.lang.annotations.MagicConstant;
import swing.RoundPanel;
import util.Locators;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CombatantPanel extends JPanel {

    public static final int TURN = 0, ATTACK = 1, HEAL = 2;

    private static final int BAR_WIDTH = 110, BAR_HEIGHT = 16, ACCENT_WIDTH = 3;

    private static final Color ROW_DEFAULT = new Color(0x1E, 0x21, 0x28);
    private static final Color ROW_TURN = new Color(0x1A, 0x22, 0x30);

    private final Combatant combatant;
    private final JLabel nameLabel;
    private final JPanel accentBar;
    private final RoundPanel barTrack;
    private final RoundPanel barFill;
    private final JLabel barLabel;

    private final Timer animationTimer;
    private int targetFillWidth = 0;

    public static CombatantPanel getPanelFor(Combatant combatant) {
        return new CombatantPanel(combatant);
    }

    private CombatantPanel(Combatant combatant) {
        this.combatant = combatant;
        setLayout(new BorderLayout(10, 0));
        setBorder(new EmptyBorder(5, 0, 5, 10));
        setBackground(ROW_DEFAULT);
        setOpaque(true);

        accentBar = new JPanel();
        accentBar.setPreferredSize(new Dimension(ACCENT_WIDTH, 0));
        accentBar.setBackground(ColorStyles.TRACK);
        add(accentBar, BorderLayout.WEST);

        nameLabel = new JLabel(combatant.name());
        nameLabel.setForeground(new Color(0xC8, 0xCC, 0xD8));
        nameLabel.setBorder(new EmptyBorder(0, 10, 0, 0));
        add(nameLabel, BorderLayout.CENTER);

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
        boolean unknown = combatant.isEnemy() && combatant.lifeStatus().isConscious();
        if (unknown) {
            targetFillWidth = BAR_WIDTH;
            barFill.setFill(ColorStyles.UNKNOWN);
            barLabel.setText("? / ?");
        } else {
            float ratio = combatant.maxHp() > 0 ? (float) combatant.hp() / combatant.maxHp() : 0;
            targetFillWidth = (int) (ratio * BAR_WIDTH);
            barFill.setFill(ColorStyles.getPercentColor(combatant.hp(), combatant.maxHp()));
            barLabel.setText(combatant.getHealthBarString());
        }

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

}