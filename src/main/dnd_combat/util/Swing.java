package util;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class Swing {

    public static JPanel asFlowLayout(
            JComponent component1,
            JComponent component2,
            JComponent component3
    ) {
        return asFlowLayout(List.of(component1, component2, component3));
    }

    public static JPanel asFlowLayout(List<JComponent> components) {
        JPanel panel = new JPanel(new FlowLayout());
        components.forEach(panel::add);
        return panel;
    }

}
