package swing.fluent;

import _global_list.Resource;
import boilerplate.FilteredVals;
import lombok.*;
import lombok.experimental.*;
import org.intellij.lang.annotations.MagicConstant;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Optional;
import java.util.function.Consumer;

import static swing.ColorStyles.*;

@Getter
@Data
@SuppressWarnings("UnusedReturnValue")
public class SwingComp<E extends JComponent> {

    public static final int TOP = 0, LEFT = 1, BOTTOM = 2, RIGHT = 3;

    @Accessors(fluent = true) protected final E component;

    protected SwingComp(E component) {
        this.component = component;
        component.setForeground(FOREGROUND);
        if (component.isOpaque()) {
            component.setBackground(BACKGROUND);
        }
        component.setAlignmentX(Component.CENTER_ALIGNMENT);
        Optional.ofNullable(component.getFont()).ifPresent(f -> component.setFont(f.deriveFont(Font.PLAIN, 12f)));
    }

    public SwingComp<E> withDerivedFont(@FilteredVals.Fonts int type, float size) {
        component.setFont(component.getFont().deriveFont(type, size));
        return this;
    }

    /**
     * <blockquote><pre>
     *     {@code
     *     // preset with the following:
     *     setFont(getFont().deriveFont(Font.PLAIN, 12f));
     *     setForeground(ColorStyles.TEXT_PRIMARY);
     *     }
     * </pre></blockquote>
     */
    public static SwingComp<JLabel> label(Object text) {
        return new SwingComp<>(new JLabel(String.valueOf(text)));
    }

    public static SwingComp<JLabel> label(Object text, float size) {
        return label(text, Font.PLAIN, size);
    }

    public static SwingComp<JLabel> label(Object text, @FilteredVals.Fonts int style, float size) {
        return label(text).withDerivedFont(style, size);
    }

    public static SwingComp<JLabel> label(
            Object text, @FilteredVals.Fonts int style, float size, @FilteredVals.Color Color fg
    ) {
        return label(text).withDerivedFont(style, size).withForeground(fg);
    }

    public SwingComp<E> withForeground(@FilteredVals.Color Color foreground) {
        component.setForeground(foreground);
        return this;
    }

    public static SwingComp<JLabel> label(Object text, @FilteredVals.Color Color fg) {
        return label(text).withForeground(fg);
    }

    /**
     * <blockquote><pre>
     *     {@code
     *     // preset with the following:
     *     setFont(getFont().deriveFont(Font.PLAIN, 12f));
     *     setFocusPainted(false);
     *     setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
     *     setOpaque(true);
     *     setBorder(new CompoundBorder(new LineBorder(param$bg, 1), 7, 10, 7, 10);
     *     setBackground(param$bg)
     *     setBackground(ColorStyles.BACKGROUND);
     *     setForeground(ColorStyles.TEXT_PRIMARY);
     *     // a mouse listener to add a 1 thickness LineBorder with color ColorStyles.TEXT_MUTED
     *     // on mouse entry that is removed on mouse exit
     *     }
     * </pre></blockquote>
     */
    public static SwingComp<JButton> button(Object textOrIcon, @FilteredVals.Color Color bg, Runnable actionListener) {
        JButton button = switch (textOrIcon) {
            case String s -> new JButton(s);
            case Resource icon -> {
                Image image = new ImageIcon(icon.getUrl()).getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
                Image resized = image.getScaledInstance(80, 80, Image.SCALE_SMOOTH);
                yield new JButton(new ImageIcon(resized));
            }
            default -> throw new ClassCastException("SwingComp.button: String or Resource expected");
        };

        return SwingComp.fluent(button)
                .withBackground(bg)
                .applied(b -> Optional.ofNullable(actionListener).ifPresent(a -> b.addActionListener(e -> a.run())));
    }

    public static SwingComp<JButton> button(
            Object textOrIcon, @FilteredVals.Color Color bg, Consumer<JButton> actionListener
    ) {
        return button(textOrIcon, bg, (Runnable) null).withAction(actionListener);
    }

    public static SwingComp<JScrollPane> scrollPane(Component contents) {
        JScrollPane pane = new JScrollPane(contents);
        pane.getVerticalScrollBar().setUnitIncrement(16);
        pane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        pane.getViewport().setBackground(BACKGROUND);
        return new SwingComp<>(pane).withBorder(BorderFactory.createEmptyBorder());
    }

    public SwingComp<E> withBorder(Border border) {
        component.setBorder(border);
        return this;
    }

    public static SwingComp<JTextArea> textArea(String text) {
        JTextArea textArea = new JTextArea(text);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        textArea.setFocusable(false);

        return new SwingComp<>(textArea).withBackground(null);
    }

    public SwingComp<E> withBackground(@FilteredVals.Color Color background) {
        component.setBackground(background);
        if (component instanceof JScrollPane s) s.getViewport().setBackground(background);
        return this;
    }

    public static Component spacer(int x, int y) {
        return Box.createRigidArea(new Dimension(x, y));
    }

    public static Component glue() {
        return Box.createVerticalGlue();
    }

    public SwingPane toPane() {
        if (!(component instanceof JPanel p))
            throw new ClassCastException("SwingComp.toPane: JPanel expected");
        return SwingPane.fluent(p);
    }

    public E in(RootPaneContainer container) {
        return in((JPanel) container.getContentPane());
    }

    public E in(JPanel panel) {
        return in(panel, null);
    }

    public E in(JPanel panel, @FilteredVals.Border String location) {
        panel.add(component, location);
        panel.revalidate();
        panel.repaint();
        return component;
    }

    public E in(RootPaneContainer container, @FilteredVals.Border String location) {
        container.getContentPane().add(component, location);
        return component;
    }

    public SwingComp<E> enabled(boolean b) {
        component.setEnabled(b);
        return this;
    }

    public SwingComp<E> transparent() {
        component.setOpaque(false);
        return this;
    }

    public SwingComp<E> withPaddedMatteBorderOnSide(
            @FilteredVals.Color Color accentColor,
            @MagicConstant(intValues = {TOP, LEFT, BOTTOM, RIGHT}) int matteLocation,
            int top, int left, int bottom, int right
    ) {
        int[] matteSides = new int[4];
        matteSides[matteLocation] += 6;

        MatteBorder matteBorder = new MatteBorder(matteSides[0], matteSides[1], matteSides[2], matteSides[3], accentColor);
        return withPaddedBorder(matteBorder, top, left, bottom, right);
    }

    public SwingComp<E> withPaddedBorder(Border mainBorder, int top, int left, int bottom, int right) {
        component.setBorder(BorderFactory.createCompoundBorder(
                mainBorder, new EmptyBorder(top, left, bottom, right)
        ));
        return this;
    }

    public SwingComp<E> withEmptyBorder(int sideLength) {
        return withEmptyBorder(sideLength, sideLength, sideLength, sideLength);
    }

    public SwingComp<E> withEmptyBorder(int top, int left, int bottom, int right) {
        component.setBorder(new EmptyBorder(top, left, bottom, right));
        return this;
    }

    /**
     * @param topLength the size of the top and bottom empty borders
     * @return the SwingComp object with top and bottom empty borders
     * equal to topLength and left and right empty borders equal to 2*topLength
     */
    public SwingComp<E> withSidePaddedEmptyBorder(int topLength) {
        return withEmptyBorder(topLength, topLength * 2, topLength, topLength * 2);
    }

    public SwingComp<E> withLabeledBorder(String label) {
        return withBorder(new TitledBorder(BorderFactory.createEtchedBorder(), label, TitledBorder.LEFT, TitledBorder.TOP));
    }

    public SwingComp<E> withText(@FilteredVals.Fonts int type, float size, @FilteredVals.Color Color fg) {
        component.setFont(component.getFont().deriveFont(type, size));
        return withForeground(fg);
    }

    public SwingComp<E> withBackgroundAndForeground(@FilteredVals.Color Color bg, @FilteredVals.Color Color fg) {
        return withBackground(bg).withForeground(fg);
    }

    public SwingComp<E> muted() {
        component.setForeground(FG_MUTED);
        return this;
    }

    public SwingComp<E> withAction(Consumer<E> action) {
        ActionListener l = e -> action.accept(component);

        if (component instanceof AbstractButton b) b.addActionListener(l);
        else if (component instanceof JComboBox<?> c) c.addActionListener(l);
        else throw new ClassCastException("SwingComp.withAction: AbstractButton or JComboBox expected");

        return this;
    }

    public SwingComp<E> withMouseMoveListener(Consumer<E> onEnter, Consumer<E> onExit) {
        component.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                onEnter.accept(component);
            }

            @Override public void mouseExited(MouseEvent e) {
                onExit.accept(component);
            }
        });
        return this;
    }

    public SwingComp<E> onLeft() {
        component.setAlignmentX(Component.LEFT_ALIGNMENT);
        return this;
    }

    public SwingComp<E> withMaximumSize(int width, int height) {
        component.setMaximumSize(new Dimension(width, height));
        component.revalidate();
        return this;
    }

    public SwingComp<E> withMinimumSize(int width, int height) {
        component.setMinimumSize(new Dimension(width, height));
        component.revalidate();
        return this;
    }

    public SwingComp<E> withPreferredSize(int width, int height) {
        component.setPreferredSize(new Dimension(width, height));
        component.revalidate();
        return this;
    }

    public SwingComp<E> withFixedSize(int width, int height) {
        return withPreferredSize(width, height).withMaximumSize(width, height).withMinimumSize(width, height);
    }

    public SwingComp<E> applied(Consumer<E> action) {
        action.accept(component);
        return this;
    }

    public SwingComp<JScrollPane> toScroller() {
        return scrollPane(component);
    }

    public static <T extends JComponent> SwingComp<T> fluent(T component) {
        SwingComp<T> comp = new SwingComp<>(component);

        return switch (component) {
            case JTextComponent ignored -> comp.withForegroundAndCaretColor(FOREGROUND);
            case AbstractButton ignored -> comp.withoutPaintedFocus().opaque()
                    .withPaddedBorder(new LineBorder(BACKGROUND, 1), 7, 10, 7, 10)
                    .applied(b -> {
                        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                        addAbstractButtonMouseListenerTo((AbstractButton) b);
                    });
            default -> comp;
        };
    }

    public SwingComp<E> withForegroundAndCaretColor(@FilteredVals.Color Color color) {
        if (!(component instanceof JTextComponent t))
            throw new ClassCastException("SwingComp.withForegroundAndCaretColor: JTextComponent required");
        t.setForeground(color);
        t.setCaretColor(color);
        return this;
    }

    public SwingComp<E> opaque() {
        component.setOpaque(true);
        return this;
    }

    public SwingComp<E> withoutPaintedFocus() {
        if (!(component instanceof AbstractButton b))
            throw new ClassCastException("SwingComp.withFocusPainted: AbstractButton required");
        b.setFocusPainted(false);
        return this;
    }

    public static void addAbstractButtonMouseListenerTo(AbstractButton b) {
        b.addMouseListener(new MouseAdapter() {
            Border standardBorder;

            @Override public void mouseEntered(MouseEvent e) {
                if (!b.isEnabled()) return;

                standardBorder = b.getBorder();

                if (b.getClientProperty("hoverActive") != null) return;
                b.putClientProperty("hoverActive", true);

                Color lineColor = b.getBackground().equals(SUCCESS)
                        ? FG_MUTED
                        : SELECTION;

                Insets insets = standardBorder.getBorderInsets(b);
                Border innerBorder = BorderFactory.createEmptyBorder(
                        Math.max(0, insets.top - 1),
                        Math.max(0, insets.left - 1),
                        Math.max(0, insets.bottom - 1),
                        Math.max(0, insets.right - 1)
                );

                b.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(lineColor, 1),
                        innerBorder
                ));
            }

            @Override public void mouseExited(MouseEvent e) {
                if (!b.isEnabled()) return;
                b.putClientProperty("hoverActive", null);
                b.setBorder(standardBorder);
            }
        });

    }

}