package combat_menu.map;

import _manager.EncounterManager;
import combat_object.combatant.Combatant;
import encounter.Encounter;
import lombok.*;
import lombok.experimental.*;
import util.Filterable;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import static swing.ColorStyles.*;
import static swing.fluent.SwingComp.*;
import static swing.fluent.SwingPane.*;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CombatMapView {

    static int GRID_COLS = 24;   // 120 ft wide
    static int GRID_ROWS = 16;   //  80 ft tall
    static int CELL = 42;        // px per 5ft square
    static int GRID_LEFT = 0;
    static int GRID_TOP = 84; // also: tray bottom
    static int GRID_BOTTOM = 49;

    static int TOKEN_D = 34;
    static int TOKEN_R = TOKEN_D / 2;

    Component parent;
    @Getter MapCanvas canvas;
    @Getter JMenuItem mapMenuItem;

    public CombatMapView(Component parent) {
        this.parent = parent;

        Encounter e = EncounterManager.getEncounter();
        List<Combatant> all = Filterable.ofLists(e.getFriendlies(), e.getEnemies()).toList();

        canvas = new MapCanvas(all);

        mapMenuItem = new JMenuItem("Battle Map");
        mapMenuItem.setEnabled(false);
        mapMenuItem.addActionListener(f -> reopenViewer());

        openPlacementDialog();
    }

    private void openPlacementDialog() {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(parent), "Setup Combatant Map",
                Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(BG_DARK);

        label("Drag every combatant onto the grid.", FG_MUTED)
                .withEmptyBorder(10, 14, 6, 14)
                .in(dialog, BorderLayout.NORTH);

        scrollPane(canvas).withBorder(null).withBackground(BG_DARK).in(dialog, BorderLayout.CENTER);

        JButton confirmButton = button("Confirm Layout", SUCCESS,
                () -> {
                    canvas.setEditable(false);
                    dialog.dispose();
                    mapMenuItem.setEnabled(true);
                }).enabled(false)
                .withoutPaintedFocus().withSidePaddedEmptyBorder(8)
                .component();
        dialog.add(footer(confirmButton), BorderLayout.SOUTH);

        canvas.setOnPlacementChanged(confirmButton::setEnabled);

        sizeDialogToContent(dialog);
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    private void reopenViewer() {
        Container currentParent = canvas.getParent();
        if (currentParent != null) currentParent.remove(canvas);

        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(parent), "Battle Map",
                Dialog.ModalityType.MODELESS);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(BG_DARK);

        fluent(new JScrollPane(canvas)).withBorder(null).withBackground(BG_DARK).in(dialog, BorderLayout.CENTER);

        JButton editButton = positionEditButton(canvas, dialog);
        dialog.add(footer(editButton), BorderLayout.SOUTH);

        sizeDialogToContent(dialog);
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    private static void sizeDialogToContent(JDialog dialog) {
        dialog.pack();

        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int maxW = Math.min(screen.width - 100, dialog.getWidth());
        int maxH = Math.min(screen.height - 100, dialog.getHeight());
        dialog.setSize(maxW, maxH);
    }

    private static JButton positionEditButton(MapCanvas canvas, JDialog dialog) {
        final String EDIT_START = "Edit Positions";
        final String EDIT_END = "Finish and Close";
        final Color EDIT_START_COLOR = BG_SURFACE;

        return button(EDIT_START, EDIT_START_COLOR, b -> {
            boolean editing = b.getBackground().equals(EDIT_START_COLOR);
            canvas.setEditable(editing);
            b.setText(editing ? EDIT_END : EDIT_START);
            b.setBackground(editing ? SUCCESS : EDIT_START_COLOR);
            if (!editing) dialog.setVisible(false);
        }).withoutPaintedFocus().withSidePaddedEmptyBorder(8).component();
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

    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static final class MapCanvas extends JPanel {

        final ArrayList<MapToken> tokens = new ArrayList<>();
        final Map<Point, MapToken> occupied = new HashMap<>();
        boolean editable = true;
        Consumer<Boolean> onPlacementChanged = b -> {};

        public MapCanvas(List<Combatant> combatants) {
            setLayout(null);
            setBackground(BG_DARK);

            int width = GRID_COLS * CELL;
            int height = GRID_TOP + GRID_ROWS * CELL;
            setPreferredSize(new Dimension(width, height));

            int trayX = 10, trayY = 8;
            for (Combatant c : combatants) {
                MapToken token = new MapToken(c, this);
                tokens.add(token);
                add(token);
                token.setBounds(trayX, trayY, TOKEN_D, TOKEN_D);
                token.markHome();
                trayX += TOKEN_D + 8;
                if (trayX + TOKEN_D > width - 10) {
                    trayX = 10;
                    trayY += TOKEN_D + 8;
                }
            }
        }

        public void logCombatantDead(Combatant c, boolean dead) {
            MapToken token = Filterable.of(tokens).firstOrElseThrow(t -> t.getCombatant().equals(c));
            token.setDead(dead);
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

        void handleDrop(MapToken token, int centerX, int centerY) {
            if (centerY < GRID_TOP) {
                token.returnHome();
                notifyPlacementChanged();
                return;
            }
            //TODO same for grid bottom

            int col = clamp(Math.round((centerX - GRID_LEFT - CELL / 2f) / (float) CELL), GRID_COLS - 1);
            int row = clamp(Math.round((centerY - GRID_TOP - CELL / 2f) / (float) CELL), GRID_ROWS - 1);

            Point cell = nearestFreeCell(col, row, token);

            occupied.entrySet().removeIf(entry -> entry.getValue() == token);
            occupied.put(cell, token);

            int x = GRID_LEFT + cell.x * CELL + CELL / 2 - TOKEN_R;
            int y = GRID_TOP + cell.y * CELL + CELL / 2 - TOKEN_R;
            token.setLocation(x, y);
            token.markHome();

            notifyPlacementChanged();
        }

        private void notifyPlacementChanged() {
            boolean allPlaced = occupied.size() == getComponentCount();
            onPlacementChanged.accept(allPlaced);
        }

        private static int clamp(int v, int max) {
            return Math.clamp(max, 0, v);
        }

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
            return direct;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(FG_HINT);
            g2.setFont(getFont().deriveFont(Font.PLAIN, 11f));
            g2.drawString("Unplaced Combatants", 12, GRID_TOP - 10);

            int gridW = GRID_COLS * CELL;
            int gridH = GRID_ROWS * CELL;

            g2.setColor(BG_SURFACE);
            g2.fillRect(GRID_LEFT, GRID_TOP, gridW, gridH);

            g2.setColor(TRACK);
            for (int c = 0; c <= GRID_COLS; c++) {
                int x = GRID_LEFT + c * CELL;
                g2.drawLine(x, GRID_TOP, x, GRID_TOP + gridH);
            }
            for (int r = 0; r <= GRID_ROWS; r++) {
                int y = GRID_TOP + r * CELL;
                g2.drawLine(GRID_LEFT, y, GRID_LEFT + gridW, y);
            }

            g2.dispose();
        }
    }

    @FieldDefaults(level = AccessLevel.PRIVATE)
    private static final class MapToken extends JComponent {

        @Getter final Combatant combatant;
        final Color aliveColor;
        Color color;
        Point dragOffset;
        Point homePosition;

        MapToken(Combatant combatant, MapCanvas canvas) {
            this.combatant = combatant;
            aliveColor = combatant.getCombatantColor();
            color = aliveColor;
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
                    x = Math.clamp(x, 0, canvas.getWidth() - TOKEN_D);
                    y = Math.clamp(y, 0, canvas.getHeight() - TOKEN_D);
                    setLocation(x, y);
                    canvas.repaint();
                }
            };
            addMouseListener(dragHandler);
            addMouseMotionListener(dragHandler);
        }

        public void setDead(boolean dead) {
            this.color = dead ? BG_LOCKED : aliveColor;
            repaint();

            String toolTipText = combatant.toString();
            if (dead) toolTipText = "(Defeated) " + toolTipText;
            setToolTipText(toolTipText);
        }

        void markHome() {
            homePosition = getLocation();
        }

        void returnHome() {
            Optional.of(homePosition).ifPresent(this::setLocation);
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
            Function<Integer, Double> s = v -> Math.pow(v, 2);
            return s.apply(x - c) + s.apply(y - c) <= s.apply(r);
        }

        private static Color textColorFor(Color bg) {
            double luminance = (0.299 * bg.getRed() + 0.587 * bg.getGreen() + 0.114 * bg.getBlue()) / 255.0;
            return luminance > 0.6 ? Color.BLACK : Color.WHITE;
        }
    }
}