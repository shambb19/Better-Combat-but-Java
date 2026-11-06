package swing.builder;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import swing.ColorStyles;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@SuperBuilder(builderMethodName = "buildButton")
public class ButtonBuilder<S extends AbstractButton> extends ComponentBuilder<S> {

     @Builder.Default String text = "";
     @Builder.Default Consumer<S> clickAction = b -> {};

     @Builder.Default boolean paintedFocus = false;

     static {
          UIManager.put("Button.hoverBackground", ColorStyles.SELECTION);
          UIManager.put("Button.hoverForeground", ColorStyles.BG_SURFACE);
     }

     /**
      * Applies all changes added in the builder to this ButtonBuilder's
      * component. This method, or one of the other inherited ComponentBuilder
      * validator methods (putIn, validated, get), are required for the
      * ButtonBuilder class to function
      */
     @Override public void validated() {
          super.validated();

          if (component.getText() == null || component.getText().isEmpty()) component.setText(text);

          SwingUtilities.invokeLater(() -> component.addActionListener(e -> clickAction.accept(component)));

          component.setFocusPainted(paintedFocus);

          component.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

          if (border == null) {
               component.setBorder(BorderFactory.createEmptyBorder(7, 10, 7, 10));
          }
     }

     /**
      * Returns a fluent ButtonBuilderBuilder object with the component set equal
      * to the given AbstractButton. This method should only be used when a specific
      * method does not exist for the AbstractButton being created. For example, this
      * method should not be used to create a JButton or JCheckBox
      * @param button the button to be set as the hosted component of
      *               the new ButtonBuilderBuilder object
      * @return the new ButtonBuilderBuilder object
      * @param <B> the subclass of AbstractButton that is the type of the button
      *           to be added to the new ButtonBuilderBuilder object
      */
     public static <B extends AbstractButton> ButtonBuilderBuilder<B, ?, ?> modifyingButton(B button) {
          return ButtonBuilder.<B>buildButton().component(button);
     }

     /**
      * Returns a fluent ButtonBuilderBuilder object with the component set to a new
      * JCheckBox created from the given label text and click action
      * @param text the text to be displayed next to the check
      * @param action the Consumer run when the check is clicked, taking the hosted
      *               JCheckBox as its parameter
      * @return the new ButtonBuilderBuilder object
      */
     public static ButtonBuilderBuilder<JCheckBox, ?, ?> createCheckBox(
             String text, Consumer<JCheckBox> action
     ) {
          return ButtonBuilder.<JCheckBox>buildButton().component(new JCheckBox(text)).clickAction(action);
     }

     /**
      * Returns a fluent ButtonBuilderBuilder object with the component set to a new
      * JButton created from the given text, background color, and click action
      * @param text the text to be displayed on the button. Note that all JButtons are
      *             automatically set with hoverBackground=SELECTION and
      *             hoverForeground = BG_SURFACE
      * @param background the background Color of the button
      * @param action the Runnable run when the button is clicked
      * @return the new ButtonBuilderBuilder object
      */
     public static ButtonBuilderBuilder<JButton, ?, ?> createButton(
             String text, Color background, Runnable action
     ) {
          return createButton(text, background, b -> action.run());
     }

     /**
      * Returns a fluent ButtonBuilderBuilder object with the component set to a new
      * JButton created from the given text, background color, and click action. Note
      * that all JButtons are automatically set with hoverBackground=SELECTION and
      * hoverForeground = BG_SURFACE
      * @param text the text to be displayed on the button
      * @param background the background Color of the button
      * @param action the Consumer run when the button is clicked, taking the hosted
      *               JButton as its parameter
      * @return the new ButtonBuilderBuilder object
      */
     public static ButtonBuilderBuilder<JButton, ?, ?> createButton(
             String text, Color background, Consumer<JButton> action
     ) {
          return ButtonBuilder.<JButton>buildButton().component(new JButton(text))
                  .background(background).clickAction(action);
     }

}
