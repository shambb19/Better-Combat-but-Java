package swing.swing_comp;

import _global_list.Resource;
import format.ColorStyles;
import lombok.*;
import lombok.experimental.*;
import org.intellij.lang.annotations.MagicConstant;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Optional;
import java.util.function.Consumer;

@Getter
@Data
@SuppressWarnings("UnusedReturnValue")
public class SwingComp<E extends JComponent> {

    public static final int TOP = 0, LEFT = 1, BOTTOM = 2, RIGHT = 3;

    @Accessors(fluent = true) protected final E component;

    protected SwingComp(E component) {
        this.component = component;
        component.setForeground(ColorStyles.TEXT_PRIMARY);
        component.setBackground(ColorStyles.BACKGROUND);
        component.setAlignmentX(Component.CENTER_ALIGNMENT);
        Optional.ofNullable(component.getFont()).ifPresent(f -> component.setFont(f.deriveFont(Font.PLAIN, 12f)));
    }

    public static <T extends JComponent> SwingComp<T> fluent(T component) {
        SwingComp<T> comp = new SwingComp<>(component);

        return switch (component) {
            case JTextComponent ignored -> comp.withForegroundAndCaretColor(ColorStyles.TEXT_PRIMARY);
            case AbstractButton ignored -> comp.withoutPaintedFocus().opaque()
                    .withPaddedBorder(new LineBorder(ColorStyles.BACKGROUND, 1), 7, 10, 7, 10)
                    .applied(b -> {
                        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                        addAbstractButtonMouseListenerTo((AbstractButton) b);
                    });
            default -> comp;
        };
    }

    public E in(JPanel panel) {
        return in(panel, null);
    }

    public E in(RootPaneContainer container) {
        return in((JPanel) container.getContentPane());
    }

    public E in(
            JPanel panel,
            @MagicConstant(valuesFromClass = BorderLayout.class) String location
    ) {
        panel.add(component, location);
        panel.revalidate();
        panel.repaint();
        return component;
    }

    public E in(
            RootPaneContainer container,
            @MagicConstant(valuesFromClass = BorderLayout.class) String location
    ) {
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

    public SwingComp<E> opaque() {
        component.setOpaque(true);
        return this;
    }

    public SwingComp<E> withPaddedMatteBorderOnSide(
            Color accentColor,
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

    public SwingComp<E> withEmptyBorder(int top, int left, int bottom, int right) {
        component.setBorder(new EmptyBorder(top, left, bottom, right));
        return this;
    }

    public SwingComp<E> withBorder(Border border) {
        component.setBorder(border);
        return this;
    }

    public SwingComp<E> withLabeledBorder(String label) {
        component.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), label, TitledBorder.LEFT, TitledBorder.TOP
        ));
        return this;
    }

    public SwingComp<E> withText(
            @MagicConstant(intValues = {Font.PLAIN, Font.BOLD, Font.ITALIC}) int type, float size, Color fg
    ) {
        component.setFont(component.getFont().deriveFont(type, size));
        return withForeground(fg);
    }

    public SwingComp<E> withDerivedFont(
            @MagicConstant(intValues = {Font.PLAIN, Font.BOLD, Font.ITALIC}) int type, float size
    ) {
        component.setFont(component.getFont().deriveFont(type, size));
        return this;
    }

    public SwingComp<E> withBackgroundAndForeground(Color bg, Color fg) {
        return withBackground(bg).withForeground(fg);
    }

    public SwingComp<E> withBackground(Color background) {
        component.setBackground(background);
        return this;
    }

    public SwingComp<E> muted() {
        component.setForeground(ColorStyles.TEXT_MUTED);
        return this;
    }

    public SwingComp<E> withForeground(Color foreground) {
        component.setForeground(foreground);
        return this;
    }

    public SwingComp<E> withForegroundAndCaretColor(Color color) {
        if (!(component instanceof JTextComponent t))
            throw new ClassCastException("SwingComp.withForegroundAndCaretColor: JTextComponent required");
        t.setForeground(color);
        t.setCaretColor(color);
        return this;
    }

    public SwingComp<E> withoutPaintedFocus() {
        if (!(component instanceof AbstractButton b))
            throw new ClassCastException("SwingComp.withFocusPainted: AbstractButton required");
        b.setFocusPainted(false);
        return this;
    }

    public SwingComp<E> withAction(Consumer<E> action) {
        if (!(component instanceof AbstractButton button))
            throw new ClassCastException("SwingComp.withAction$Consumer: AbstractButton expected");
        button.addActionListener(e -> action.accept(component));
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

    public SwingComp<E> withPreferredSize(int width, int height) {
        component.setPreferredSize(new Dimension(width, height));
        component.revalidate();
        return this;
    }

    public SwingComp<JPanel> withCancelOption(Runnable onCancel) {
        if (!(component instanceof JButton)) throw new ClassCastException();

        return SwingPane.newArrangedAs(SwingPane.FLOW).collect(component, button("Cancel", ColorStyles.CRITICAL, onCancel));
    }

    public SwingComp<E> applied(Consumer<E> action) {
        action.accept(component);
        return this;
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
    public static SwingComp<JButton> button(Object textOrIcon, Color bg, Runnable actionListener) {
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

    public static SwingComp<JLabel> label(
            Object text, @MagicConstant(intValues = {Font.PLAIN, Font.BOLD, Font.ITALIC}) int style, float size, Color fg
    ) {
        return label(text).withDerivedFont(style, size).withForeground(fg);
    }

    public static SwingComp<JLabel> label(Object text, Color fg) {
        return label(text).withForeground(fg);
    }

    public static <T> SwingComp<JComboBox<T>> comboBox(T[] contents) {
        return new SwingComp<>(new JComboBox<>(contents));
    }

    public static SwingComp<JScrollPane> scrollPane(Component contents) {
        JScrollPane pane = new JScrollPane(contents);
        pane.getVerticalScrollBar().setUnitIncrement(16);
        pane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        return new SwingComp<>(pane).withBorder(BorderFactory.createEmptyBorder());
    }

    public static SwingComp<JTextArea> textArea(String text) {
        JTextArea textArea = new JTextArea(text);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        textArea.setFocusable(false);

        return new SwingComp<>(textArea).withBackground(null);
    }

    public static Component spacer(int x, int y) {
        return Box.createRigidArea(new Dimension(x, y));
    }

    public static void addAbstractButtonMouseListenerTo(AbstractButton b) {
        b.addMouseListener(new MouseAdapter() {
            Border standardBorder;

            @Override public void mouseEntered(MouseEvent e) {
                standardBorder = b.getBorder();

                if (b.getClientProperty("hoverActive") != null
                        || standardBorder instanceof CompoundBorder) return;
                b.putClientProperty("hoverActive", true);

                Color lineColor = b.getBackground().equals(ColorStyles.SUCCESS)
                        ? ColorStyles.TEXT_MUTED
                        : ColorStyles.SELECTION;

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
                b.putClientProperty("hoverActive", null);
                b.setBorder(standardBorder);
            }
        });

    }

}