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

     public static <B extends AbstractButton> ButtonBuilderBuilder<B, ?, ?> modifyingButton(B button) {
          return ButtonBuilder.<B>buildButton().component(button);
     }

     public static ButtonBuilderBuilder<JCheckBox, ?, ?> createCheckBox(
             String text, Consumer<JCheckBox> action
     ) {
          return ButtonBuilder.<JCheckBox>buildButton().component(new JCheckBox(text)).clickAction(action);
     }

     public static ButtonBuilderBuilder<JButton, ?, ?> createButton(
             String text, Color background, Runnable action
     ) {
          return createButton(text, background, b -> action.run());
     }

     public static ButtonBuilderBuilder<JButton, ?, ?> createButton(
             String text, Color background, Consumer<JButton> action
     ) {
          return ButtonBuilder.<JButton>buildButton().component(new JButton(text))
                  .background(background).clickAction(action);
     }

}
