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

    public S get() {
        validated();
        return component;
    }

    public S putIn(Container dest) {
        return putIn(dest, null);
    }

    public S putIn(Container dest, Object constraints) {
        validated();
        dest.add(component, constraints);
        return component;
    }

    public JScrollPane getAsScrollerIn(Container dest, Object constraints) {
        validated();
        JScrollPane scrollPane = new JScrollPane(component);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        dest.add(scrollPane, constraints);
        return scrollPane;
    }

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

    protected <P> void setIfNonNull(P property, Consumer<P> consumer) {
        Optional.ofNullable(property).ifPresent(consumer);
    }

    public static <C extends JComponent> ComponentBuilderBuilder<C, ?, ?> modifying(C comp) {
        return ComponentBuilder.<C>buildComponent().component(comp);
    }

    public static JLabel getBasicLabel(
            Object contents, @FilteredVals.Fonts int style, float size, Color foreground
    ) {
        return createLabel(contents, style, size, foreground).build().get();
    }

    public static ComponentBuilderBuilder<JLabel, ?, ?> createLabel(Object contents) {
        return createLabel(contents, Font.PLAIN, 12f, FOREGROUND);
    }

    public static ComponentBuilderBuilder<JLabel, ?, ?> createLabel(
            Object contents, @FilteredVals.Fonts int style, float size, Color foreground
    ) {
        var builder = ComponentBuilder.<JLabel>buildComponent();

        if (contents instanceof ImageIcon i) builder.component(new JLabel(i));
        else builder.component(new JLabel(contents.toString()));

        return builder.fontBuilder(FontBuilder.of(style, size, foreground));
    }

    public static <C> ComponentBuilderBuilder<JComboBox<C>, ?, ?> createComboBox(
            C[] contents, Consumer<JComboBox<C>> onChange, @Nullable C initialSelection
    ) {
        JComboBox<C> box = new JComboBox<>(contents);
        box.addActionListener(e -> onChange.accept(box));

        Optional.ofNullable(initialSelection).ifPresentOrElse(box::setSelectedItem, () -> box.setSelectedIndex(-1));

        return ComponentBuilder.<JComboBox<C>>buildComponent().component(box);
    }

    @Value @AllArgsConstructor(staticName = "of") @RequiredArgsConstructor(staticName = "of")
    public static class FontBuilder {
        @FilteredVals.Fonts int style;
        float size;
        @NonFinal Color fg = null;

        public static void setFontOn(JComponent component, FontBuilder to) {
            component.setFont(component.getFont().deriveFont(to.style, to.size));
            Optional.ofNullable(to.fg).ifPresent(component::setForeground);
        }
    }

}
