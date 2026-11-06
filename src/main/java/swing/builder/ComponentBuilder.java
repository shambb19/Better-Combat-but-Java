package swing.builder;

import boilerplate.FilteredVals;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.experimental.SuperBuilder;
import org.intellij.lang.annotations.MagicConstant;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import static swing.ColorStyles.*;

@FieldDefaults(makeFinal = true, level = AccessLevel.PROTECTED)
@SuperBuilder(builderMethodName = "buildComponent") public class ComponentBuilder<S extends JComponent> {
    protected S component;

    Color background;
    @Builder.Default Color foreground = FOREGROUND;
    @Builder.Default FontBuilder fontBuilder = FontBuilder.of(Font.PLAIN, 12f);

    String toolTipText;

    Border border;

    @Builder.Default @MagicConstant(valuesFromClass = Component.class) float alignmentX = Component.CENTER_ALIGNMENT;
    @Builder.Default @MagicConstant(valuesFromClass = Component.class) float alignmentY = Component.CENTER_ALIGNMENT;

    Dimension minimumSize, maximumSize, preferredSize;

    @Builder.Default int fixedHeight = -1;
    Dimension fixedSize;

    @Builder.Default boolean visible = true;
    @Builder.Default boolean enabled = true;
    @Builder.Default boolean opaque = false;

    @Builder.Default Consumer<S> onMouseEnter = b -> {};
    @Builder.Default Consumer<S> onMouseExit = b -> {};

    @Singular Map<String, Object> clientProperties;

    static {
        UIManager.put("Component.arrowType", "chevron");
    }

    /**
     * Returns the component from this ComponentBuilder after all changes
     * added in the Builder have been applied. This method, or one of the
     * other ComponentBuilder validator methods (putIn, validated), are
     * required for the ComponentBuilder class to function
     * @return the validated JComponent subclass from this ComponentBuilder
     */
    public S get() {
        validated();
        return component;
    }

    /**
     * Returns the component from this ComponentBuilder after all changes
     * added in the Builder have been applied and after adding it to the
     * given Container object. This method, or one of the other
     * ComponentBuilder validator methods (putIn, validated, get), are
     * required for the ComponentBuilder class to function
     * @param dest the Container to which this ComponentBuilder's component
     *             is to be added
     * @return the validated JComponent subclass from this ComponentBuilder
     */
    public S putIn(Container dest) {
        return putIn(dest, null);
    }

    /**
     * Returns the component from this ComponentBuilder after all changes
     * added in the Builder have been applied and after adding it to the
     * given Container object. This method, or one of the other
     * ComponentBuilder validator methods (putIn, validated, get), are
     * required for the ComponentBuilder class to function
     * @param dest the Container to which this ComponentBuilder's component
     *             is to be added
     * @param constraints the constraints with which this ComponentBuilder's
     *                    component is to be added
     * @return the validated JComponent subclass from this ComponentBuilder
     */
    public S putIn(Container dest, Object constraints) {
        validated();
        dest.add(component, constraints);
        return component;
    }

    /**
     * Returns a JScrollPane hosting this ComponentBuilder's component. The
     * JScrollPane is added to the given Container with the given constraints
     * before it is returned. The JScrollPane is without modification except
     * for an EmptyBorder
     * @param dest the Container to which the JScrollPane is to be added
     * @param constraints the constraints with which the JScrollPane is to be
     *                    added
     * @return the new JScrollPane that hosts this ComponentBuilder's component
     */
    public JScrollPane getAsScrollerIn(Container dest, Object constraints) {
        validated();
        JScrollPane scrollPane = new JScrollPane(component);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        dest.add(scrollPane, constraints);
        return scrollPane;
    }

    /**
     * Applies all changes added in the builder to this ComponentBuilder's
     * component. This method, or one of the other ComponentBuilder validator
     * methods (putIn, get), are required for the ComponentBuilder class
     * to function
     */
    public void validated() {
        JComponent toModify;
        if (component instanceof JScrollPane sp) toModify = sp.getViewport();
        else toModify = component;

        setIfNonNull(background, toModify::setBackground);
        setIfNonNull(foreground, toModify::setForeground);

        try {
            toModify.setFont(toModify.getFont().deriveFont(fontBuilder.style, fontBuilder.size));
        } catch (NullPointerException ignored) {}
        setIfNonNull(fontBuilder.fg, toModify::setForeground);

        setIfNonNull(border, toModify::setBorder);

        setIfNonNull(toolTipText, toModify::setToolTipText);

        toModify.setAlignmentX(alignmentX);
        toModify.setAlignmentY(alignmentY);

        if (fixedHeight > 0) {
            toModify.setMinimumSize(new Dimension(0, fixedHeight));
            toModify.setMaximumSize(new Dimension(Integer.MAX_VALUE, fixedHeight));
        }
        if (fixedSize != null) {
            toModify.setMinimumSize(fixedSize);
            toModify.setMaximumSize(fixedSize);
            toModify.setPreferredSize(fixedSize);
        } else {
            setIfNonNull(minimumSize, toModify::setMinimumSize);
            setIfNonNull(maximumSize, toModify::setMaximumSize);
            setIfNonNull(preferredSize, toModify::setPreferredSize);
        }

        component.setVisible(visible);
        component.setEnabled(enabled);
        toModify.setOpaque(background != null);

        component.addMouseListener(new MouseAdapter() {
            @Override public void mouseExited(MouseEvent e) {
                onMouseExit.accept(component);
            }

            @Override public void mouseEntered(MouseEvent e) {
                onMouseEnter.accept(component);
            }
        });

        for (var entry : clientProperties.entrySet()) {
            toModify.putClientProperty(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Sets the given property using the given consumer only if the property
     * is not null. The consumer is a method reference that, as a precondition,
     * matches the description of the property being changed. For example,
     * if property=background, the consumer must be component::setBackground.
     * @param property the value of the property to be changed
     * @param consumer the consumer used to change that property, run only if
     *                 property != null
     * @param <P> the type of the property being changed
     */
    protected <P> void setIfNonNull(P property, Consumer<P> consumer) {
        Optional.ofNullable(property).ifPresent(consumer);
    }

    /**
     * Returns a fluent ComponentBuilderBuilder object with the component set equal
     * to the given component. This method should only be used when a specific method
     * does not exist for the component being created. For example, this method
     * should not be used to create a JLabel.
     * @param comp the component to be set as the hosted component of the new
     *             ComponentBuilderBuilder object
     * @return the new ComponentBuilderBuilder object
     * @param <C> the subclass of JComponent that is the type of the component
     *           to be added to the new ComponentBuilderBuilder object
     */
    public static <C extends JComponent> ComponentBuilderBuilder<C, ?, ?> modifying(C comp) {
        return ComponentBuilder.<C>buildComponent().component(comp);
    }

    /**
     * Returns a JLabel with the given contents, font, and foreground.
     * @param contents the contents to be displayed in the JLabel. If
     *                 contents instanceof Icon, the icon will be displayed,
     *                 else the toString of the object will be shown
     * @param style the Font class int constant of the new JLabel object's font
     * @param size the float size of the new JLabel object's font
     * @param foreground the foreground of the new JLabel
     * @return the new JLabel object
     */
    public static JLabel getBasicLabel(
            Object contents, @FilteredVals.Fonts int style, float size, Color foreground
    ) {
        return createLabel(contents, style, size, foreground).build().get();
    }

    /**
     * Returns a JLabel with the given contents
     * @param contents the contents to be displayed in the JLabel. If
     *                 contents instanceof Icon, the icon will be displayed,
     *                 else the toString of the object will be shown
     * @return the new JLabel object
     */
    public static ComponentBuilderBuilder<JLabel, ?, ?> createLabel(Object contents) {
        return createLabel(contents, Font.PLAIN, 12f, FOREGROUND);
    }

    /**
     * Returns a fluent ComponentBuilderBuilder object with the component set
     * equal to a new JLabel created with the given contents, font, and foreground.
     * @param contents the contents to be displayed in the JLabel. If contents
     *                 instanceof Icon, the icon will be displayed, else the
     *                 toString of the object will be shown
     * @param style the Font class int constant of the new JLabel object's font
     * @param size the float size of the new JLabel object's font
     * @param foreground the foreground of the new JLabel object's
     * @return the new ComponentBuilderBuilder object hosting the new JLabel object
     */
    public static ComponentBuilderBuilder<JLabel, ?, ?> createLabel(
            Object contents, @FilteredVals.Fonts int style, float size, Color foreground
    ) {
        var builder = ComponentBuilder.<JLabel>buildComponent();

        if (contents instanceof ImageIcon i) builder.component(new JLabel(i));
        else builder.component(new JLabel(contents.toString()));

        return builder.fontBuilder(FontBuilder.of(style, size, foreground));
    }

    /**
     * Returns a fluent ComponentBuilderBuilder object with the component set
     * equal to a new JComboBox created with the given contents, actionListener,
     * initialSelection, and type.
     * @param contents the contents of the new JComboBox object
     * @param onChange the Consumer run when the JComboBox selection is changed,
     *                 taking the hosted JComboBox as its parameter
     * @param initialSelection the object in the JComboBox to be selected initially.
     *                         If null, the JComboBox is left initially unselected.
     * @return the new ComponentBuilderBuilder object hosting the new JComboBox object
     * @param <C> the type of the contents of the JComboBox
     */
    public static <C> ComponentBuilderBuilder<JComboBox<C>, ?, ?> createComboBox(
            C[] contents, Consumer<JComboBox<C>> onChange, @Nullable C initialSelection
    ) {
        JComboBox<C> box = new JComboBox<>(contents);
        box.addActionListener(e -> onChange.accept(box));

        Optional.ofNullable(initialSelection).ifPresentOrElse(box::setSelectedItem, () -> box.setSelectedIndex(-1));

        return ComponentBuilder.<JComboBox<C>>buildComponent().component(box);
    }

    /**
     * A date holder temporarily holding the information required to set the font
     * of a component. The style and size parameters replace the parameters of
     * Font.deriveFont, and the foreground parameter sets the component's foreground
     * normally. fg is an optional value; when null, nothing is changed in the target
     * component.
     */
    @Value @AllArgsConstructor(staticName = "of") @RequiredArgsConstructor(staticName = "of")
    public static class FontBuilder {
        @FilteredVals.Fonts int style;
        float size;
        @NonFinal Color fg = null;

        /**
         * Sets the font and foreground of the given component to the given FontBuilder's
         * instance variables. Style and are the parameters of Font.deriveFont. Foreground
         * sets the component's foreground if not null, else does nothing.
         * @param component the JComponent to be modified
         * @param to the FontBuilder object whose instance variables will edit the JComponent
         */
        public static void setFontOn(JComponent component, FontBuilder to) {
            component.setFont(component.getFont().deriveFont(to.style, to.size));
            Optional.ofNullable(to.fg).ifPresent(component::setForeground);
        }
    }

}
