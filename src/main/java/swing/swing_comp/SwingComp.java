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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

@Getter
@Data
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("UnusedReturnValue")
public class SwingComp<E extends JComponent> {

    public static final String
            TOP = "top",
            LEFT = "left",
            BOTTOM = "bottom",
            RIGHT = "right";

    public static final String
            STANDARD = "standard",
            X_LARGE = "xl",
            LARGE = "l",
            MEDIUM = "m",
            SMALL = "s",
            X_SMALL = "xs";

    @Accessors(fluent = true) protected final E component;

    public static <T extends JComponent> SwingComp<T> modifiable(T component) {
        return new SwingComp<>(component);
    }

    public SwingComp<E> in(JPanel panel) {
        panel.add(component);
        panel.revalidate();
        panel.repaint();
        return this;
    }

    public SwingComp<E> in(RootPaneContainer container) {
        return in((JPanel) container.getContentPane());
    }

    public SwingComp<E> in(
            JPanel panel,
            @MagicConstant(valuesFromClass = BorderLayout.class) String location
    ) {
        panel.add(component, location);
        panel.revalidate();
        panel.repaint();
        return this;
    }

    public SwingComp<E> in(
            RootPaneContainer container,
            @MagicConstant(valuesFromClass = BorderLayout.class) String location
    ) {
        container.getContentPane().add(component, location);
        return this;
    }

    public SwingComp<E> disabled() {
        component.setEnabled(false);
        return this;
    }

    public SwingComp<E> enabled() {
        component.setEnabled(true);
        return this;
    }

    public SwingComp<E> withoutSelection() {
        if (!(component instanceof JComboBox<?>))
            throw new ClassCastException("SwingComp.withoutSelection: JComboBox expected");

        ((JComboBox<?>) component).setSelectedIndex(-1);
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

    public SwingComp<E> withToolTip(String text) {
        component.setToolTipText(text);
        return this;
    }

    public SwingComp<E> withPaddedMatteBorderOnSide(
            Color accentColor,
            @MagicConstant(stringValues = {TOP, LEFT, BOTTOM, RIGHT}) String matteLocation,
            int top, int left, int bottom, int right
    ) {
        int matteTop = 0, matteLeft = 0, matteBottom = 0, matteRight = 0;
        switch (matteLocation) {
            case TOP -> matteTop = 1;
            case LEFT -> matteLeft = 1;
            case BOTTOM -> matteBottom = 1;
            case RIGHT -> matteRight = 1;
        }
        MatteBorder matteBorder = new MatteBorder(matteTop, matteLeft, matteBottom, matteRight, accentColor);
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

    public SwingComp<E> withHighlight(
            Color accentColor,
            @MagicConstant(stringValues = {TOP, LEFT, BOTTOM, RIGHT}) String accentLocation,
            boolean isSmall
    ) {
        int top = 0, left = 0, bottom = 0, right = 0;
        switch (accentLocation) {
            case TOP -> top = 5;
            case LEFT -> left = 5;
            case BOTTOM -> bottom = 5;
            case RIGHT -> right = 5;
        }

        int blankY = (isSmall) ? 5 : 10;
        int blankX = (isSmall) ? 7 : 15;

        component.setBorder(new CompoundBorder(
                new MatteBorder(top, left, bottom, right, accentColor),
                new EmptyBorder(blankY, blankX, blankY, blankX)
        ));

        return this;
    }

    public SwingComp<E> withLabeledBorder(String label) {
        component.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                label,
                TitledBorder.LEFT,
                TitledBorder.TOP
        ));

        return this;
    }

    public SwingComp<E> withDerivedFont(
            @MagicConstant(intValues = {Font.PLAIN, Font.BOLD, Font.ITALIC}) int type,
            float size
    ) {
        component.setFont(component.getFont().deriveFont(type, size));
        return this;
    }

    public SwingComp<E> asStandardTextSize() {
        return withDerivedFont(Font.PLAIN, 12f);
    }

    public SwingComp<E> asHeader() {
        component.putClientProperty("FlatLaf.style", "font: $h1.font");
        return this;
    }

    public SwingComp<E> withBackgroundAndForeground(Color bg, Color fg) {
        return withBackground(bg).withForeground(fg);
    }

    public SwingComp<E> withBackground(Color background) {
        component.setBackground(background);
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

    public SwingComp<E> withAction(Runnable action) {
        switch (component) {
            case AbstractButton button -> button.addActionListener(e -> action.run());
            case JTextComponent textComp -> textComp.addKeyListener(new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent e) {
                    action.run();
                }
            });
            case JSpinner spinner -> spinner.addChangeListener(e -> action.run());
            case JList<?> list -> list.addListSelectionListener(e -> action.run());
            default -> component.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    action.run();
                }
            });
        }
        return this;
    }

    public SwingComp<E> withAction(Consumer<E> action) {
        return withAction(() -> action.accept(component));
    }

    public SwingComp<E> withHandCursor() {
        component.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return this;
    }

    public SwingComp<E> centered() {
        component.setAlignmentX(Component.CENTER_ALIGNMENT);
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

        return SwingPane.panel().collect(component, button("Cancel", onCancel)).withLayout(SwingPane.FLOW);
    }

    public SwingComp<E> applied(Consumer<E> action) {
        action.accept(component);
        return this;
    }

    /**
     * <blockquote><pre>
     *     {@code
     *     // preset with the following:
     *     setFocusPainted(false);
     *     setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
     *     setOpaque(true);
     *     setBorder(new EmptyBorder(4, 4, 4, 4);
     *     // a mouse listener to add a 1 thickness LineBorder with color ColorStyles.TEXT_MUTED
     *     // on mouse entry that is removed on mouse exit
     *     }
     * </pre></blockquote>
     */
    public static SwingComp<JButton> button(Object textOrIcon, Runnable actionListener) {
        JButton button;
        if (textOrIcon instanceof String s)
            button = new JButton(s);
        else if (textOrIcon instanceof Resource icon) {
            Image image = new ImageIcon(icon.getUrl()).getImage();
            Image resized = image.getScaledInstance(80, 80, Image.SCALE_SMOOTH);
            ImageIcon imageIcon = new ImageIcon(resized);

            button = new JButton(imageIcon);
        } else
            throw new ClassCastException("SwingComp.button: String or Resource expected");

        button.addMouseListener(new MouseAdapter() {
            Border standardBorder;

            @Override public void mouseEntered(MouseEvent e) {
                standardBorder = button.getBorder();

                if (button.getClientProperty("hoverActive") != null
                        || standardBorder instanceof CompoundBorder) return;
                button.putClientProperty("hoverActive", true);

                Color lineColor = button.getBackground().equals(ColorStyles.SUCCESS)
                        ? ColorStyles.TEXT_MUTED
                        : ColorStyles.SELECTION;

                Insets insets = standardBorder.getBorderInsets(button);
                Border innerBorder = BorderFactory.createEmptyBorder(
                        Math.max(0, insets.top - 1),
                        Math.max(0, insets.left - 1),
                        Math.max(0, insets.bottom - 1),
                        Math.max(0, insets.right - 1)
                );

                button.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(lineColor, 1),
                        innerBorder
                ));
            }

            @Override public void mouseExited(MouseEvent e) {
                button.putClientProperty("hoverActive", null);
                button.setBorder(standardBorder);
            }
        });

        SwingComp<JButton> comp = SwingComp.modifiable(button)
                .withBackground(ColorStyles.BACKGROUND)
                .withEmptyBorder(6, 14, 6, 14)
                .withoutPaintedFocus().withHandCursor().opaque();

        if (actionListener == null) return comp;
        return comp.withAction(actionListener);
    }

    public static SwingComp<JLabel> label(String... text) {
        StringBuilder labelText = new StringBuilder();
        for (String str : text) {
            labelText.append(str);
        }
        return new SwingComp<>(new JLabel(labelText.toString()));
    }

    @SafeVarargs
    public static <T> SwingComp<JComboBox<T>> comboBox(List<T> contents, T... additionalContents) {
        JComboBox<T> box = new JComboBox<>();

        contents.forEach(box::addItem);
        Arrays.stream(additionalContents).forEach(box::addItem);

        return new SwingComp<>(box);
    }

    public static <T> SwingComp<JList<T>> list(DefaultListModel<T> listModel) {
        JList<T> list = new JList<>(listModel);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        return new SwingComp<>(list);
    }

    public static SwingComp<JCheckBox> checkBox(String label) {
        return new SwingComp<>(new JCheckBox(label));
    }

    public static SwingComp<JScrollPane> scrollPane(Component contents) {
        JScrollPane pane = new JScrollPane(contents);
        pane.getVerticalScrollBar().setUnitIncrement(16);
        pane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        return new SwingComp<>(pane);
    }

    public static SwingComp<JTextField> field(String... contents) {
        StringBuilder text = new StringBuilder();
        for (String item : contents)
            text.append(item).append(" ");

        return new SwingComp<>(new JTextField(text.toString()));
    }

    public static SwingComp<JTextArea> textArea(String text) {
        JTextArea textArea = new JTextArea(text);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        textArea.setFocusable(false);
        textArea.setBackground(null);

        return new SwingComp<>(textArea);
    }

    public static void gapIn(int size, JPanel dest) {
        dest.add(Box.createVerticalStrut(size));
    }

}