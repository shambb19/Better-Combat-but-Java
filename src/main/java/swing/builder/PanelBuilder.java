package swing.builder;

import boilerplate.FilteredVals;
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

    public static <P extends JPanel> PanelBuilderBuilder<P, ?, ?> modifyingPanel(P target) {
        return PanelBuilder.<P>buildPanel().component(target);
    }

    public static PanelBuilderBuilder<JPanel, ?, ?> modifyingPanel(RootPaneContainer target) {
        return PanelBuilder.<JPanel>buildPanel().component((JPanel) target.getContentPane());
    }

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

    public static PanelBuilderBuilder<JPanel, ?, ?> createPanel(
            @FilteredVals.Layouts int layoutType, Object... contents
    ) {
        var b = PanelBuilder.<JPanel>buildPanel().component(new JPanel()).layoutType(layoutType);
        for (Object o : contents) b.hostedComponent(getComponent(o), null);
        return b;
    }

    public static void modifyBasicPanel(
            JPanel root,
            @FilteredVals.Layouts int layoutType, int hgap, int vgap,
            Border border
    ) {
        PanelBuilder.<JPanel>buildPanel().component(root).layoutType(layoutType)
                        .hgap(hgap).vgap(vgap).border(border).build().validated();
    }

    public static void modifyBasicPanel(JPanel root, @FilteredVals.Layouts int layoutType, Border border) {
        modifyBasicPanel(root, layoutType, 0, 0, border);
    }

    public static GridBagConstraints createConstraints(
            @MagicConstant(valuesFromClass = GridBagConstraints.class) int anchor,
            int top, int left, int bottom, int right
    ) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = anchor;
        gbc.insets = new Insets(top, left, bottom, right);
        return gbc;
    }

    private static Component getComponent(Object comp) {
        String simpleName = comp.getClass().getSimpleName();
        return switch (comp) {
            case String s -> ComponentBuilder.createLabel(s).build().get();
            case Component c -> c;
            case ComponentBuilder<?> cb -> cb.get();
            case ComponentBuilderBuilder<?, ?, ?> cbb -> cbb.build().get();
            default -> throw new ClassCastException("PanelBuilder.getComponent: unexpected class " + simpleName);
        };
    }

}