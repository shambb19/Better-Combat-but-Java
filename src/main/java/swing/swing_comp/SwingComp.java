package swing.swing_comp;

import __main.manager.EncounterManager;
import _global_list.Resource;
import combat_menu.listener.IntegerFieldListener;
import org.intellij.lang.annotations.MagicConstant;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class SwingComp<E extends JComponent> {

    public static final String
            TOP = "top",
            LEFT = "left",
            BOTTOM = "bottom",
            RIGHT = "right";

    public static final String
            BOLD = "bold",
            PLAIN = "plain",
            ITALIC = "italic",
            HEADER = "header",
            SUB_HEADER = "sub-header",
            TITLE = "title",
            CAPTION = "caption",
            MONO = "mono";

    protected final E component;

    protected SwingComp(E component) {
        this.component = component;
    }

    public E build() {
        return component;
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

    public SwingComp<E> enabledIf(Supplier<Boolean> condition) {
        component.setEnabled(condition.get());

        EncounterManager.addQueueListener(e -> SwingUtilities.invokeLater(() -> component.setEnabled(condition.get())));

        return this;
    }

    public SwingComp<E> enabledWhen(JCheckBox box, boolean selectionRequirement) {
        box.addActionListener(e -> component.setEnabled(box.isSelected() == selectionRequirement));
        return this;
    }

    public SwingComp<E> visibleWhen(JCheckBox box, boolean selectionRequirement) {
        box.addActionListener(e -> component.setVisible(box.isSelected() == selectionRequirement));
        return this;
    }

    public SwingComp<E> unselected() {
        if (!(component instanceof JComboBox<?>)) throw new ClassCastException();

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

    public SwingComp<E> round() {
        if (component instanceof JLabel)
            component.putClientProperty("FlatLaf.style", "background: $List.selectionBackground; " + "arc: 999");
        else
            component.putClientProperty("FlatLaf.style", "arc: " + 999);

        return this;
    }

    public SwingComp<E> withToolTip(String text) {
        component.setToolTipText(text);
        return this;
    }

    public SwingComp<E> onlyIntegers() {
        if (!(component instanceof JTextComponent comp)) throw new ClassCastException();

        comp.addKeyListener(new IntegerFieldListener());
        return this;
    }

    public SwingComp<E> withEmptyBorder(int size) {
        component.setBorder(new EmptyBorder(size, size, size, size));
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

    public SwingComp<E> withFont(
            @MagicConstant(stringValues = {
                    BOLD, PLAIN, ITALIC,
                    HEADER, SUB_HEADER, TITLE, CAPTION,
                    MONO
            }) String... fonts
    ) {
        Font base = component.getFont();

        final String KEY = "FlatLaf.style";

        for (String font : fonts) {
            switch (font) {
                case BOLD -> {
                    component.setFont(base.deriveFont(Font.BOLD));
                    component.putClientProperty(KEY, "font: bold");
                }
                case PLAIN -> {
                    component.setFont(base.deriveFont(Font.PLAIN));
                    component.putClientProperty(KEY, "font: normal");
                }
                case ITALIC -> {
                    component.setFont(base.deriveFont(Font.ITALIC));
                    component.putClientProperty(KEY, "font: italic");
                }

                case HEADER -> component.putClientProperty(KEY, "font: $h1.font");
                case SUB_HEADER -> component.putClientProperty(KEY, "font: $h2.font");
                case TITLE -> component.putClientProperty(KEY, "font: $h0.font");
                case CAPTION -> component.putClientProperty(KEY, "font: $small.font");

                case MONO -> component.putClientProperty(KEY, "font: $monospaced.font");
            }
        }

        component.revalidate();
        component.repaint();
        return this;
    }

    public SwingComp<E> bold(float size) {
        component.setFont(component.getFont().deriveFont(Font.BOLD, size));
        return this;
    }

    public SwingComp<E> withBackground(Color background) {
        component.setBackground(background);
        return this;
    }

    public SwingComp<E> withForeground(Color foreground) {
        component.setForeground(foreground);
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

    public SwingComp<E> centered() {
        component.setAlignmentX(Component.CENTER_ALIGNMENT);
        return this;
    }

    public SwingComp<E> onLeft() {
        component.setAlignmentX(Component.LEFT_ALIGNMENT);
        return this;
    }

    public SwingComp<E> withSize(int width, int height) {
        component.setPreferredSize(new Dimension(width, height));
        return this;
    }

    public SwingComp<JPanel> withCancelOption(Runnable onCancel) {
        if (!(component instanceof JButton)) throw new ClassCastException();
        round();

        return SwingPane.panel().collect(component, button("Cancel", onCancel).round()).withLayout(SwingPane.FLOW);
    }

    public SwingComp<E> applied(Consumer<E> action) {
        action.accept(component);
        return this;
    }

    public static <T extends JComponent> SwingComp<T> modifiable(T component) {
        return new SwingComp<>(component);
    }

    public static SwingComp<JButton> button(String name, Runnable actionListener) {
        JButton button = new JButton(name);

        if (actionListener == null)
            return SwingComp.modifiable(button);
        else
            return SwingComp.modifiable(button).withAction(actionListener);
    }

    public static SwingComp<JButton> button(Resource icon, Runnable actionListener) {
        Image image = new ImageIcon(icon.url()).getImage();
        Image resized = image.getScaledInstance(80, 80, Image.SCALE_SMOOTH);
        ImageIcon imageIcon = new ImageIcon(resized);

        JButton button = new JButton(imageIcon);
        return SwingComp.modifiable(button)
                .applied(b -> b.setFocusPainted(false))
                .withAction(actionListener);
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
        pane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        return new SwingComp<>(pane);
    }

    public static SwingComp<JTextField> field(String... contents) {
        StringBuilder text = new StringBuilder();
        for (String item : contents) {
            text.append(item).append(" ");
        }
        return new SwingComp<>(new JTextField(text.toString()));
    }

    public static void gapIn(int size, JPanel dest) {
        dest.add(Box.createVerticalStrut(size));
    }

    public static Component gap(int size) {
        return Box.createVerticalStrut(size);
    }

}