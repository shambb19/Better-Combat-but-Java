package format.swing_comp;

import boilerplate.SourceVal;
import lombok.*;

import javax.swing.*;
import java.awt.*;

public class SwingPane extends SwingComp<JPanel> {

    public static final int
            BORDER = 0,
            VERTICAL_BOX = 1, HORIZONTAL_BOX = 2,
            FLOW = 3, FLOW_RIGHT = 4, FLOW_LEFT = 5,
            ONE_COLUMN = 6, TWO_COLUMN = 7,
            SINGLE_ROW = 8;

    private SwingPane(JPanel panel) {
        super(panel);
    }

    public static SwingPane panelIn(JPanel panel) {
        return panelIn(panel, null);
    }

    public static SwingPane panelIn(JPanel panel, @SourceVal String location) {
        JPanel pane = new JPanel();
        panel.add(pane, location);
        return new SwingPane(pane);
    }

    public static SwingPane panelIn(RootPaneContainer container) {
        return panelIn(container, null);
    }

    public static SwingPane panelIn(RootPaneContainer container, @SourceVal String location) {
        return panelIn((JPanel) container.getContentPane(), location);
    }

    public static SwingPane newArrangedAs(@SourceVal.Pane int layout) {
        return newArrangedAs(layout, 0, 0);
    }

    public static SwingPane newArrangedAs(@SourceVal.Pane int layout, int hgap, int vgap) {
        return new SwingPane(new JPanel()).arrangedAs(layout, hgap, vgap);
    }

    public SwingPane arrangedAs(@SourceVal.Pane int layout) {
        return arrangedAs(layout, 0, 0);
    }

    public SwingPane arrangedAs(@SourceVal.Pane int layout, int hgap, int vgap) {
        LayoutManager manager = switch (layout) {
            case BORDER -> new BorderLayout(hgap, vgap);
            case VERTICAL_BOX -> new BoxLayout(component, BoxLayout.Y_AXIS);
            case HORIZONTAL_BOX -> new BoxLayout(component, BoxLayout.X_AXIS);
            case FLOW -> new FlowLayout(FlowLayout.CENTER, hgap, vgap);
            case FLOW_RIGHT -> new FlowLayout(FlowLayout.RIGHT, hgap, vgap);
            case FLOW_LEFT -> new FlowLayout(FlowLayout.LEFT, hgap, vgap);
            case ONE_COLUMN -> new GridLayout(0, 1, hgap, vgap);
            case TWO_COLUMN -> new GridLayout(0, 2, hgap, vgap);
            case SINGLE_ROW -> new GridLayout(1, 0, hgap, vgap);
            default -> throw new IllegalArgumentException("withLayout in SwingPane : unexpected layout value");
        };
        component.setLayout(manager);
        component.revalidate();
        component.repaint();

        return this;
    }

    public SwingPane collect(Object... components) {
        for (Object obj : components) {
            if (obj instanceof Object[] array)
                for (Object arrObj : array) component.add(getComponent(arrObj));
            else if (obj instanceof Iterable<?> iterable)
                iterable.forEach(o -> component.add(getComponent(o)));
            else
                component.add(getComponent(obj));
        }

        component.revalidate();
        component.repaint();
        return this;
    }

    @SneakyThrows
    public SwingPane borderCollect(BorderComponent... components) {
        for (BorderComponent comp : components) {
            component.add(comp.component, comp.location);
        }
        component.revalidate();
        component.repaint();
        return this;
    }

    public static BorderComponent north(Object component) {
        return new BorderComponent(BorderLayout.NORTH, SwingPane.getComponent(component));
    }

    public static BorderComponent east(Object component) {
        return new BorderComponent(BorderLayout.EAST, SwingPane.getComponent(component));
    }

    public static BorderComponent south(Object component) {
        return new BorderComponent(BorderLayout.SOUTH, SwingPane.getComponent(component));
    }

    public static BorderComponent west(Object component) {
        return new BorderComponent(BorderLayout.WEST, SwingPane.getComponent(component));
    }

    public static BorderComponent center(Object component) {
        return new BorderComponent(BorderLayout.CENTER, SwingPane.getComponent(component));
    }

    public static SwingPane fluent(JPanel panel) {
        return new SwingPane(panel);
    }

    public static SwingPane fluent(RootPaneContainer container) {
        return new SwingPane((JPanel) container.getContentPane());
    }

    private static Component getComponent(Object comp) {
        return switch (comp) {
            case String s -> new JLabel(s);
            case Component c -> c;
            case SwingComp<?> sc -> sc.component();
            default -> {
                String cause;
                if (comp instanceof BorderComponent)
                    cause = "use SwingPane.borderCollect for BorderLayout assignment";
                else
                    cause = "unsupported obj " + comp.getClass();
                throw new ClassCastException("SwingPane.getComponent: " + cause);
            }
        };
    }

    @Value public static class BorderComponent {
        @SourceVal.Border String location;
        Component component;
    }

}