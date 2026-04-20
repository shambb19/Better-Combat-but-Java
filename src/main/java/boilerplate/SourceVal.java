package boilerplate;

import format.swing_comp.SwingPane;
import org.intellij.lang.annotations.MagicConstant;

import java.awt.*;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE, ElementType.METHOD})
public @interface SourceVal {

    @MagicConstant(intValues = {Font.PLAIN, Font.BOLD, Font.ITALIC}) @interface Fonts {
    }

    @MagicConstant(valuesFromClass = BorderLayout.class) @interface Border {
    }

    @MagicConstant(valuesFromClass = SwingPane.class) @interface Pane {
    }

}