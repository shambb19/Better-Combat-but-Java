package combat_menu.encounter_info;

import combat_menu.action_panel.form.ActionFormPanel;
import combat_object.combatant.Combatant;
import lombok.*;
import lombok.experimental.*;
import org.intellij.lang.annotations.MagicConstant;
import swing.popup.StatTooltipPopup;
import swing.builder.ComponentBuilder;
import util.Locators;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;
import java.util.Optional;

import static swing.ColorStyles.*;
import static swing.builder.PanelBuilder.*;

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
    @NonFinal StatTooltipPopup activeTooltip = null;

    Timer animationTimer;
    @NonFinal int targetFillWidth = 0;

    public HealthBarPanel(Combatant combatant) {
        this.combatant = combatant;

        modifyingPanel(this).layoutType(BORDER).hgap(10)
                .border(BorderFactory.createEmptyBorder(5, 0, 5, 10))
                .fixedHeight(40)
                .background(BACKGROUND)
                .alignmentX(LEFT_ALIGNMENT).build().validated();

        accentBar = createPanel(FLOW)
                .preferredSize(new Dimension(ACCENT_WIDTH, 0))
                .background(TRACK)
                .build().putIn(this, BorderLayout.WEST);

        nameLabel = ComponentBuilder.createLabel(combatant)
                .border(BorderFactory.createEmptyBorder(0, 10, 0, 0))
                .build().putIn(this, BorderLayout.CENTER);

        barTrack = modifyingPanel(new RoundPanel(BAR_HEIGHT / 2, TRACK))
                .layoutType(NONE)
                .preferredSize(new Dimension(BAR_WIDTH, BAR_HEIGHT))
                .onMouseEnter(p -> { // stat tooltip
                    if (combatant.isDead()) return;

                    Optional.ofNullable(activeTooltip).ifPresent(Window::dispose);

                    Window owner = SwingUtilities.getWindowAncestor(HealthBarPanel.this);
                    activeTooltip = new StatTooltipPopup(owner, combatant);

                    Point barOnScreen = p.getLocationOnScreen();
                    int tx = barOnScreen.x - activeTooltip.getWidth() - 8;
                    int ty = barOnScreen.y + (p.getHeight() - activeTooltip.getHeight()) / 2;

                    if (tx < 0) tx = barOnScreen.x + p.getWidth() + 8;

                    activeTooltip.setLocation(tx, ty);
                    activeTooltip.setVisible(true);
                }).onMouseExit(p -> {
                    if (activeTooltip != null) {
                        activeTooltip.dispose();
                        activeTooltip = null;
                    }
                }).build().get();

        barFill = new RoundPanel(BAR_HEIGHT / 2, HEALTHY);
        barFill.setBounds(0, 0, 0, BAR_HEIGHT);
        barTrack.add(barFill);

        add(barTrack, BorderLayout.EAST);

        animationTimer = new Timer(16, e -> doTickAnimation());
        update();
    }

    private void doTickAnimation() {
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

        boolean unknown = combatant.isEnemy() && combatant.isConscious();
        if (unknown)
            targetFillWidth = BAR_WIDTH;
        else
            targetFillWidth = (int) (combatant.getHpRatio() * BAR_WIDTH);

        barFill.fireSlideAdjust(targetFillWidth, BAR_HEIGHT, barTrack);
    }

    public void endActionState() {
        // new Color[]{background, accent bar background, name label foreground}
        Color[] currentCombatant = new Color[]{ROW_TURN, HEALTHY, FOREGROUND};
        Color[] otherCombatants = new Color[]{BACKGROUND, TRACK, FOREGROUND};

        Color[] colors = combatant.isCurrentCombatant() ? currentCombatant : otherCombatants;
        setBackground(colors[0]);
        accentBar.setBackground(colors[1]);
        nameLabel.setForeground(colors[2]);
        if (combatant.isUnconscious()) nameLabel.setForeground(FG_MUTED);
    }

    public void setActionMode(
            @MagicConstant(intValues = {HealthBarPanel.ATTACK, HealthBarPanel.HEAL}) int mode, ActionFormPanel dest
    ) {
        boolean isValidTarget = Locators.getTargetList(mode == ATTACK).contains(combatant);

        if (isValidTarget) {
            accentBar.setBackground(HEADER);
            nameLabel.setForeground(HEADER);
            setBackground(BACKGROUND);
        } else {
            endActionState();
        }

        MouseListener actionSelectionListener = new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                dest.submitTarget(combatant);
            }
        };

        Component[] components = Arrays.copyOf(getComponents(), getComponents().length + 1);
        components[components.length - 1] = this;
        for (Component c : components) {
            if (isValidTarget) c.addMouseListener(actionSelectionListener);
            else c.removeMouseListener(actionSelectionListener);
        }
    }

    @RequiredArgsConstructor static class RoundPanel extends JPanel {
        private final int radius;
        @NonNull private Color background;
        private Timer timer;

        {
            setOpaque(false);
        }

        public void setFill(Color c) {
            background = c;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(background);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius * 2, radius * 2);
            g2.dispose();
        }

        public void fireSlideAdjust(int targetWidth, int height, JComponent track) {
            if (timer != null && timer.isRunning()) {
                timer.stop();
            }

            timer = new Timer(16, e -> {
                int current = getWidth();

                if (current == targetWidth) {
                    ((Timer) e.getSource()).stop();
                    return;
                }

                int diff = Math.abs(targetWidth - current);
                int step = Math.max(1, diff / 6);
                int next = current < targetWidth ? current + step : current - step;

                setBounds(0, 0, next, height);

                Optional.ofNullable(track).ifPresent(Component::repaint);
            });

            timer.start();
        }
    }
}