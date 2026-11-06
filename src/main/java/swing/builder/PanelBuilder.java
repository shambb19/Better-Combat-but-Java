package swing.builder;

import boilerplate.FilteredVals;
import exception.UnexpectedClassException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Singular;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.intellij.lang.annotations.MagicConstant;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.Map;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@SuperBuilder(builderMethodName = "buildPanel") public class PanelBuilder<S extends JPanel> extends ComponentBuilder<S> {

    public static Border SPACER_BORDER = BorderFactory.createEmptyBorder(20, 20, 20, 20);

    public static final int
            BORDER = 0,
            VERTICAL_BOX = 1, HORIZONTAL_BOX = 2,
            FLOW = 3, FLOW_RIGHT = 4, FLOW_LEFT = 5,
            ONE_COLUMN = 6, TWO_COLUMN = 7,
            SINGLE_ROW = 8,
            GRID_BAG = 9,
            NONE = 10;

    @Builder.Default @MagicConstant(valuesFromClass = PanelBuilder.class) int layoutType = BORDER;
    @Singular Map<Object, Object> hostedComponents;

    int hgap;
    int vgap;

    /**
     * Applies all changes added in the builder to this PanelBuilder's
     * component. This method, or one of the other ComponentBuilder validator
     * methods (putIn, get), are required for the ComponentBuilder class
     * to function
     */
    @Override public void validated() {
        super.validated();

        LayoutManager manager = switch (layoutType) {
            case VERTICAL_BOX -> new BoxLayout(component, BoxLayout.Y_AXIS);
            case HORIZONTAL_BOX -> new BoxLayout(component, BoxLayout.X_AXIS);
            case FLOW -> new FlowLayout(FlowLayout.CENTER, hgap, vgap);
            case FLOW_RIGHT -> new FlowLayout(FlowLayout.RIGHT, hgap, vgap);
            case FLOW_LEFT -> new FlowLayout(FlowLayout.LEFT, hgap, vgap);
            case ONE_COLUMN -> new GridLayout(0, 1, hgap, vgap);
            case TWO_COLUMN -> new GridLayout(0, 2, hgap, vgap);
            case SINGLE_ROW -> new GridLayout(1, 0, hgap, vgap);
            case GRID_BAG -> new GridBagLayout();
            case NONE -> null;
            default -> new BorderLayout(hgap, vgap);
        };
        component.setLayout(manager);

        for (var entry : hostedComponents.entrySet()) {
            Object o = entry.getKey();
            Object constraints = entry.getValue();
            Component comp = getComponent(o);

            if (constraints == null) component.add(comp);
            else component.add(comp, constraints);
        }

        component.revalidate();
        component.repaint();
    }

    /**
     * Returns a fluent PanelBuilderBuilder object with the component set equal
     * to the given JPanel.
     * @param target the JPanel to be set as the hosted component of the new
     *               PanelBuilderBuilder object
     * @return the new PanelBuilderBuilder object
     * @param <P> the subclass of JPanel that is the type of the component
     *            to be added to the new PanelBuilderBuilder object
     */
    public static <P extends JPanel> PanelBuilderBuilder<P, ?, ?> modifyingPanel(P target) {
        return PanelBuilder.<P>buildPanel().component(target);
    }

    /**
     * Returns a fluent PanelBuilderBuilder object with the component set equal
     * to the given RootPaneContainer's content pane.
     * @param target the RootPaneContainer, the content pane of which will be set
     *               as the hosted component of the new PanelBuilderBuilder object
     * @return the new PanelBuilderBuilder object
     */
    public static PanelBuilderBuilder<JPanel, ?, ?> modifyingPanel(RootPaneContainer target) {
        return PanelBuilder.<JPanel>buildPanel().component((JPanel) target.getContentPane());
    }

    /**
     * Adds all objects (after being converted to Component subclasses) to the given JPanel.
     * No parameter will be used when adding; this method should NOT be used for any JPanel
     * with layout BorderLayout or GridBagLayout.
     * <p></p>
     * Objects are converted in
     * PanelBuilder.getComponent, which returns the following:
     * Strings as basic JLabels with the String as its text;
     * Components as themselves;
     * ComponentBuilders and ComponentBuilderBuilders as validated versions of their hosted
     * components.
     * All other classes passed to contents will throw a ClassCastException
     * @param panel the JPanel to which the contents will be added
     * @param contents the Objects which, if a valid class (from String, Component,
     *                 ComponentBuilder, ComponentBuilderBuilder) or subclass, will be
     *                 converted and added to the JPanel parameter
     */
    public static void addConstraintlessComponentsTo(JPanel panel, Object... contents) {
        for (Object o : contents) {
            if (o instanceof Object[] arr) {
                for (Object p : arr) panel.add(getComponent(p));
            } else if (o instanceof Iterable<?> list) {
                list.forEach(p -> panel.add(getComponent(p)));
            } else {
                panel.add(getComponent(o));
            }
        }
        panel.revalidate();
        panel.repaint();
    }

    /**
     * Returns a fluent PanelBuilderBuilder object with the component set to a new
     * JPanel with the given layout and contents. All content adding is done without
     * constraints; if layout is BorderLayout or GridBagLayout, component adding
     * should be done with the .hostedComponent fluent call, which allows constraints
     * @param layoutType the int constant layout type to be applied to the new hosted
     *                   JPanel
     * @param contents the Objects which, if a valid class (from String, Component,
     *                 ComponentBuilder, ComponentBuilderBuilder) or subclass, will be
     *                 converted and added to new hosted JPanel
     * @return the new PanelBuilderBuilder object
     */
    public static PanelBuilderBuilder<JPanel, ?, ?> createPanel(
            @FilteredVals.Layouts int layoutType, Object... contents
    ) {
        var b = PanelBuilder.<JPanel>buildPanel().component(new JPanel()).layoutType(layoutType);
        for (Object o : contents) b.hostedComponent(getComponent(o), null);
        return b;
    }

    /**
     * Modifies the given JPanel with the given parameters
     * @param root the JPanel to be modified
     * @param layoutType the int constant layout type to be applied to the JPanel
     * @param hgap the horizontal gap value to be applied to the layout type
     * @param vgap the vertical gap value to be applied to the layout type
     * @param border the Border to be added to the JPanel
     */
    public static void modifyBasicPanel(
            JPanel root,
            @FilteredVals.Layouts int layoutType, int hgap, int vgap,
            Border border
    ) {
        PanelBuilder.<JPanel>buildPanel().component(root).layoutType(layoutType)
                        .hgap(hgap).vgap(vgap).border(border).build().validated();
    }

    /**
     * Modifies the given JPanel with the given parameters. The layout type
     * given will be applied with hgap=0 and vgap=0. To set it otherwise, use
     * the override of this method that takes hgap and vgap values
     * @param root the JPanel to be modified
     * @param layoutType the int constant layout type to be applied to the JPanel
     * @param border the Border to be added to the JPanel
     */
    public static void modifyBasicPanel(JPanel root, @FilteredVals.Layouts int layoutType, Border border) {
        modifyBasicPanel(root, layoutType, 0, 0, border);
    }

    /**
     * Returns a GridBagConstraints object with the given anchor location
     * and insets. This method is a boilerplate-reducer that is functionally
     * identical to instantiating a GridBagConstraints object and manually
     * editing the anchor and insets
     * @param anchor the int constant anchor location to be applied to the
     *               GridBagConstraints
     * @param top the top inset value
     * @param left the left inset value
     * @param bottom the bottom inset value
     * @param right the right inset value
     * @return the new GridBagConstraints object
     */
    public static GridBagConstraints createConstraints(
            @MagicConstant(valuesFromClass = GridBagConstraints.class) int anchor,
            int top, int left, int bottom, int right
    ) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = anchor;
        gbc.insets = new Insets(top, left, bottom, right);
        return gbc;
    }

    /**
     * Converts the given Object to a Component if it is from a valid class
     * (String, Component, ComponentBuilder, ComponentBuilderBuilder) or subclass.
     * <p>Object is String -> returns a basic JLabel displaying the String as text</p>
     * <p>Object is Component -> returns itself</p>
     * <p>Object is ComponentBuilder or ComponentBuilderBuilder -> returns the validated
     * version of its hosted component</p>
     * <p>Object is none of these classes -> throws ClassCastException</p>
     * @param comp the Object to be converted
     * @return the converted Component if comp is a valid class, else throws
     * ClassCastException
     */
    private static Component getComponent(Object comp) {
        Class<?>[] validClasses = {String.class, Component.class, ComponentBuilder.class, ComponentBuilderBuilder.class};
        return switch (comp) {
            case String s -> ComponentBuilder.createLabel(s).build().get();
            case Component c -> c;
            case ComponentBuilder<?> cb -> cb.get();
            case ComponentBuilderBuilder<?, ?, ?> cbb -> cbb.build().get();
            default -> throw new UnexpectedClassException(comp, validClasses);
        };
    }

}