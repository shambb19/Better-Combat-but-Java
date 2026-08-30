package combat_menu;

import _manager.EncounterManager;
import combat_object.combatant.Combatant;
import combat_object.combatant.PC;
import lombok.*;
import lombok.experimental.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static swing.ColorStyles.*;
import static swing.fluent.SwingComp.button;
import static swing.fluent.SwingComp.label;
import static swing.fluent.SwingPane.*;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CombatMapView {

    static int GRID_COLS = 24;   // 120 ft wide
    static int GRID_ROWS = 16;   //  80 ft tall
    static int CELL = 42;        // px per 5ft square
    static int TRAY_HEIGHT = 84;

    static int TOKEN_D = 34;
    static int TOKEN_R = TOKEN_D / 2;

    public static JMenuItem launch(Component parent, List<Combatant> combatants) {
        JMenuItem menuItem = new JMenuItem("Battle Map");
        menuItem.setEnabled(false);

        MapCanvas canvas = new MapCanvas(combatants);
        menuItem.addActionListener(e -> reopenViewer(parent, canvas));

        openPlacementDialog(parent, canvas, menuItem);

        return menuItem;
    }

    /**
     * Convenience overload that pulls the full combatant list straight from
     * EncounterManager. Verify these accessor names match your Encounter
     * model (they're inferred from EncounterSelectionPanel's
     * setFriendlies/setEnemies/getParty calls) - adjust if needed.
     */
    public static JMenuItem launch(Component parent) {
        List<Combatant> all = new ArrayList<>();
        all.addAll(EncounterManager.getParty());
        all.addAll(EncounterManager.getEncounter().getFriendlies());
        all.addAll(EncounterManager.getEncounter().getEnemies());
        return launch(parent, all);
    }

    // ------------------------------------------------------------------
    // Dialogs
    // ------------------------------------------------------------------

    private static void openPlacementDialog(Component parent, MapCanvas canvas, JMenuItem menuItem) {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(parent), "Place Your Combatants",
                Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(BG_DARK);

        JLabel instructions = new JLabel(
                "Drag every combatant from the tray onto the grid to set up the encounter.");
        instructions.setForeground(FG_MUTED);
        instructions.setBorder(new EmptyBorder(10, 14, 6, 14));
        dialog.add(instructions, BorderLayout.NORTH);

        JScrollPane scroller = new JScrollPane(canvas);
        scroller.setBorder(null);
        scroller.getViewport().setBackground(BG_DARK);
        dialog.add(scroller, BorderLayout.CENTER);

        JButton confirmButton = button("Confirm Layout", SUCCESS,
                () -> {
                    canvas.setEditable(false);
                    dialog.dispose();
                    menuItem.setEnabled(true);
                }).enabled(false)
                .withoutPaintedFocus().withEmptyBorder(8, 16, 8, 16)
                .component();

        dialog.add(footer(confirmButton), BorderLayout.SOUTH);

        canvas.setOnPlacementChanged(confirmButton::setEnabled);

        dialog.setSize(
                Math.min(1100, GRID_COLS * CELL + 80),
                Math.min(780, TRAY_HEIGHT + GRID_ROWS * CELL + 160));
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    private static void reopenViewer(Component parent, MapCanvas canvas) {
        // a component can only live in one container at a time
        Container currentParent = canvas.getParent();
        if (currentParent != null) currentParent.remove(canvas);

        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(parent), "Battle Map",
                Dialog.ModalityType.MODELESS);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(BG_DARK);

        JScrollPane scroller = new JScrollPane(canvas);
        scroller.setBorder(null);
        scroller.getViewport().setBackground(BG_DARK);
        dialog.add(scroller, BorderLayout.CENTER);

        JButton editButton = button("Edit Positions", BG_SURFACE, () -> canvas.setEditable(true))
                .withoutPaintedFocus().withEmptyBorder(8, 16, 8, 16).component();

        dialog.add(footer(editButton), BorderLayout.SOUTH);

        dialog.setSize(
                Math.min(1100, GRID_COLS * CELL + 80),
                Math.min(780, TRAY_HEIGHT + GRID_ROWS * CELL + 160));
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    private static JPanel footer(JButton actionButton) {
        JPanel wrap = newArrangedAs(FLOW_RIGHT, 12, 8).collect(actionButton).transparent().component();

        return newArrangedAs(BORDER)
                .borderCollect(
                        west(legend()), east(wrap)
                ).withBorder(new MatteBorder(1, 0, 0, 0, TRACK))
                .withBackground(BG_DARK).component();
    }

    private static JPanel legend() {
        return newArrangedAs(FLOW_LEFT, 14, 8)
                .collect(
                        legendChip("Party", PARTY),
                        legendChip("Friendly", FRIENDLY),
                        legendChip("Enemy", ENEMY)
                ).transparent().component();
    }

    private static JComponent legendChip(String text, Color color) {
        JComponent dot = new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.fillOval(0, 0, 10, 10);
                g2.dispose();
            }
        };
        dot.setPreferredSize(new Dimension(10, 10));

        JLabel label = label(text, Font.PLAIN, 11f, FG_MUTED).component();

        return newArrangedAs(FLOW_LEFT, 6, 0).collect(dot, label).transparent().component();
    }

    // ------------------------------------------------------------------
    // Canvas: draws the tray + grid, hosts the draggable tokens
    // ------------------------------------------------------------------
    @FieldDefaults(level = AccessLevel.PRIVATE)
    private static final class MapCanvas extends JPanel {

        final Map<Point, MapToken> occupied = new HashMap<>();
        boolean editable = true;
        Consumer<Boolean> onPlacementChanged = b -> {};

        MapCanvas(List<Combatant> combatants) {
            setLayout(null);
            setBackground(BG_DARK);

            int width = GRID_COLS * CELL;
            int height = TRAY_HEIGHT + GRID_ROWS * CELL;
            setPreferredSize(new Dimension(width, height));

            int trayX = 10, trayY = 8;
            for (Combatant c : combatants) {
                MapToken token = new MapToken(c, this);
                add(token);
                token.setBounds(trayX, trayY, TOKEN_D, TOKEN_D);
                trayX += TOKEN_D + 8;
                if (trayX + TOKEN_D > width - 10) {
                    trayX = 10;
                    trayY += TOKEN_D + 8;
                }
            }
        }

        void setOnPlacementChanged(Consumer<Boolean> callback) {
            this.onPlacementChanged = callback;
        }

        boolean notEditable() {
            return !editable;
        }

        void setEditable(boolean editable) {
            this.editable = editable;
            repaint();
        }

        /**
         * Called by a token once it's dropped. Snaps into the nearest free
         * cell if released over the grid; otherwise leaves it in the tray.
         */
        void handleDrop(MapToken token, int centerX, int centerY) {
            if (centerY < gridTop()) {
                occupied.entrySet().removeIf(entry -> entry.getValue() == token);
                notifyPlacementChanged();
                return;
            }

            int col = clamp(Math.round((centerX - gridLeft() - CELL / 2f) / (float) CELL), GRID_COLS - 1);
            int row = clamp(Math.round((centerY - gridTop() - CELL / 2f) / (float) CELL), GRID_ROWS - 1);

            Point cell = nearestFreeCell(col, row, token);

            occupied.entrySet().removeIf(entry -> entry.getValue() == token);
            occupied.put(cell, token);

            int x = gridLeft() + cell.x * CELL + CELL / 2 - TOKEN_R;
            int y = gridTop() + cell.y * CELL + CELL / 2 - TOKEN_R;
            token.setLocation(x, y);

            notifyPlacementChanged();
        }

        int gridTop() {return TRAY_HEIGHT;}

        private void notifyPlacementChanged() {
            boolean allPlaced = occupied.size() == getComponentCount();
            onPlacementChanged.accept(allPlaced);
        }

        private static int clamp(int v, int max) {
            return Math.max(0, Math.min(max, v));
        }

        int gridLeft() {return 0;}

        private Point nearestFreeCell(int col, int row, MapToken token) {
            Point direct = new Point(col, row);
            MapToken occupant = occupied.get(direct);
            if (occupant == null || occupant == token) return direct;

            int maxRadius = Math.max(GRID_COLS, GRID_ROWS);
            for (int radius = 1; radius < maxRadius; radius++) {
                for (int dx = -radius; dx <= radius; dx++) {
                    for (int dy = -radius; dy <= radius; dy++) {
                        if (Math.max(Math.abs(dx), Math.abs(dy)) != radius) continue;
                        int c = clamp(col + dx, GRID_COLS - 1);
                        int r = clamp(row + dy, GRID_ROWS - 1);
                        Point candidate = new Point(c, r);
                        MapToken existing = occupied.get(candidate);
                        if (existing == null || existing == token) return candidate;
                    }
                }
            }
            return direct; // grid completely full - fall back rather than lose the token
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(FG_HINT);
            g2.setFont(getFont().deriveFont(Font.PLAIN, 11f));
            g2.drawString("TRAY — unplaced combatants", 12, TRAY_HEIGHT - 10);

            int gridW = GRID_COLS * CELL;
            int gridH = GRID_ROWS * CELL;

            g2.setColor(BG_SURFACE);
            g2.fillRect(gridLeft(), gridTop(), gridW, gridH);

            g2.setColor(TRACK);
            for (int c = 0; c <= GRID_COLS; c++) {
                int x = gridLeft() + c * CELL;
                g2.drawLine(x, gridTop(), x, gridTop() + gridH);
            }
            for (int r = 0; r <= GRID_ROWS; r++) {
                int y = gridTop() + r * CELL;
                g2.drawLine(gridLeft(), y, gridLeft() + gridW, y);
            }

            g2.dispose();
        }
    }

    // ------------------------------------------------------------------
    // Token: a draggable colored dot labeled with the combatant's abbreviation
    // ------------------------------------------------------------------
    @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
    private static final class MapToken extends JComponent {

        Combatant combatant;
        Color color;
        @NonFinal Point dragOffset;

        MapToken(Combatant combatant, MapCanvas canvas) {
            this.combatant = combatant;
            this.color = factionColor(combatant);
            setSize(TOKEN_D, TOKEN_D);
            setToolTipText(combatant.toString());

            MouseAdapter dragHandler = new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (canvas.notEditable()) return;
                    dragOffset = e.getPoint();
                    getParent().setComponentZOrder(MapToken.this, 0);
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    if (canvas.notEditable() || dragOffset == null) return;
                    dragOffset = null;
                    canvas.handleDrop(MapToken.this, getX() + TOKEN_R, getY() + TOKEN_R);
                }

                @Override
                public void mouseDragged(MouseEvent e) {
                    if (canvas.notEditable() || dragOffset == null) return;
                    Point parentPoint = SwingUtilities.convertPoint(MapToken.this, e.getPoint(), canvas);
                    int x = parentPoint.x - dragOffset.x;
                    int y = parentPoint.y - dragOffset.y;
                    x = Math.max(0, Math.min(canvas.getWidth() - TOKEN_D, x));
                    y = Math.max(0, Math.min(canvas.getHeight() - TOKEN_D, y));
                    setLocation(x, y);
                    canvas.repaint();
                }
            };
            addMouseListener(dragHandler);
            addMouseMotionListener(dragHandler);
        }

        private static Color factionColor(Combatant c) {
            if (c instanceof PC) return PARTY;
            return c.isEnemy() ? ENEMY : FRIENDLY;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(color);
            g2.fillOval(0, 0, TOKEN_D, TOKEN_D);
            g2.setColor(BACKGROUND);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawOval(0, 0, TOKEN_D - 1, TOKEN_D - 1);

            String abbr = combatant.getAbbreviation();
            g2.setFont(getFont().deriveFont(Font.BOLD, 10f));
            FontMetrics fm = g2.getFontMetrics();
            int tw = fm.stringWidth(abbr);

            g2.setColor(textColorFor(color));
            g2.drawString(abbr, (TOKEN_D - tw) / 2f, (TOKEN_D + fm.getAscent() - fm.getDescent()) / 2f - 1);

            g2.dispose();
        }

        @Override
        public boolean contains(int x, int y) {
            int c = TOKEN_D / 2;
            int r = TOKEN_D / 2;
            return (x - c) * (x - c) + (y - c) * (y - c) <= r * r;
        }

        private static Color textColorFor(Color bg) {
            double luminance = (0.299 * bg.getRed() + 0.587 * bg.getGreen() + 0.114 * bg.getBlue()) / 255.0;
            return luminance > 0.6 ? Color.BLACK : Color.WHITE;
        }
    }
}