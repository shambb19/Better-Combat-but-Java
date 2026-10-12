package boilerplate;

import org.intellij.lang.annotations.MagicConstant;

import java.awt.*;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE) @Target(ElementType.PARAMETER) public @interface FilteredVals {
    @MagicConstant(valuesFromClass = swing.builder.PanelBuilder.class) @interface Layouts {}

    @MagicConstant(intValues = {Font.PLAIN, Font.BOLD, Font.ITALIC}) @interface Fonts {}

    @MagicConstant(valuesFromClass = swing.ColorStyles.class) @interface Color {}
}