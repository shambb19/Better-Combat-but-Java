package boilerplate;

import org.intellij.lang.annotations.MagicConstant;

import java.awt.*;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE) @Target(ElementType.PARAMETER) public @interface FilteredVals {

    /**
     * Limits the associated parameter to the layout int constants in the PanelBuilder class
     */
    @MagicConstant(valuesFromClass = swing.builder.PanelBuilder.class) @interface Layouts {}

    /**
     * Limits the associated parameter to the Font constant Font.PLAIN, Font.BOLD, and Font.ITALIC
     */
    @MagicConstant(intValues = {Font.PLAIN, Font.BOLD, Font.ITALIC}) @interface Fonts {}

    /**
     * Limits the associated parameter to one of the colors in the ColorStyles class
     */
    @MagicConstant(valuesFromClass = swing.ColorStyles.class) @interface Color {}
}