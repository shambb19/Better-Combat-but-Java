package swing.swing_comp;

import org.intellij.lang.annotations.MagicConstant;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class SwingPane extends SwingComp<JPanel> {

    public static final int BORDER = 0;
    public static final int VERTICAL_BOX = 1;
    public static final int HORIZONTAL_BOX = 2;
    public static final int FLOW = 3;
    public static final int FLOW_RIGHT = 4;
    public static final int FLOW_LEFT = 5;
    public static final int ONE_COLUMN = 6;
    public static final int TWO_COLUMN = 7;
    public static final int SINGLE_ROW = 8;

    private SwingPane(JPanel panel) {
        super(panel);
    }

    public static SwingPane panelIn(JPanel panel) {
        JPanel pane = new JPanel();
        panel.add(pane);
        return new SwingPane(pane);
    }

    public static SwingPane panelIn(
            JPanel panel,
            @MagicConstant(valuesFromClass = BorderLayout.class) String location
    ) {
        JPanel pane = new JPanel();
        panel.add(pane, location);
        return new SwingPane(pane);
    }

    public static SwingPane panelIn(RootPaneContainer container) {
        JPanel pane = new JPanel();
        container.getContentPane().add(pane);
        return new SwingPane(pane);
    }

    public static SwingPane panelIn(
            RootPaneContainer container,
            @MagicConstant(valuesFromClass = BorderLayout.class) String location
    ) {
        return panelIn((JPanel) container.getContentPane(), location);
    }

    public static SwingPane panel() {
        return new SwingPane(new JPanel());
    }

    public static SwingPane flowPair(Object comp1, Object comp2, boolean isOnLeft) {
        LayoutManager layout = isOnLeft ? new FlowLayout(FlowLayout.LEFT) : new FlowLayout();
        return modifiable(new JPanel(layout)).collect(comp1, comp2);
    }

    public SwingPane collect(Object... components) {
        Arrays.stream(components).forEach(comp -> component.add(getComponent(comp)));
        component.revalidate();
        component.repaint();
        return this;
    }

    public SwingPane collectIf(boolean condition, Object... components) {
        if (!condition)
            return this;
        else
            return collect(components);
    }

    public SwingPane with(
            Object comp,
            @MagicConstant(valuesFromClass = BorderLayout.class) String location
    ) {
        component.add(getComponent(comp), location);
        return this;
    }

    public SwingPane withLayout(@MagicConstant(valuesFromClass = SwingPane.class) int layout) {
        LayoutManager manager = switch (layout) {
            case BORDER -> new BorderLayout();
            case VERTICAL_BOX -> new BoxLayout(component, BoxLayout.Y_AXIS);
            case HORIZONTAL_BOX -> new BoxLayout(component, BoxLayout.X_AXIS);
            case FLOW -> new FlowLayout();
            case FLOW_RIGHT -> new FlowLayout(FlowLayout.RIGHT);
            case FLOW_LEFT -> new FlowLayout(FlowLayout.LEFT);
            case ONE_COLUMN -> new GridLayout(0, 1);
            case TWO_COLUMN -> new GridLayout(0, 2);
            case SINGLE_ROW -> new GridLayout(1, 0);
            default -> throw new IllegalArgumentException("withLayout in SwingPane : unexpected layout value");
        };
        component.setLayout(manager);
        component.revalidate();
        component.repaint();
        return this;
    }

    public SwingPane contentsReplacedWith(Object... components) {
        component.removeAll();
        return collect(components);
    }

    public SwingPane withGaps(int hgap, int vgap) {
        switch (component.getLayout()) {
            case BorderLayout b:
                b.setHgap(hgap);
                b.setVgap(vgap);
                break;
            case GridLayout g:
                g.setHgap(hgap);
                g.setVgap(vgap);
                break;
            default:
                throw new ClassCastException("withGaps in SwingPane: GridLayout or BorderLayout expected");
        }
        return this;
    }

    public static SwingPane modifiable(JPanel panel) {
        return new SwingPane(panel);
    }

    public static SwingPane modifiable(RootPaneContainer container) {
        return new SwingPane((JPanel) container.getContentPane());
    }

    private static Component getComponent(Object comp) {
        return switch (comp) {
            case String s -> new JLabel(s);
            case Component c -> c;
            case SwingComp<?> sc -> sc.build();
            default -> throw new ClassCastException();
        };
    }

}