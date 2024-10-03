package es.degrassi.custommachineryars.client.screen.creation.gui.builder;

import es.degrassi.custommachineryars.Registration;
import es.degrassi.custommachineryars.guielement.SourceGuiElement;
import fr.frinn.custommachinery.api.guielement.GuiElementType;
import fr.frinn.custommachinery.client.screen.BaseScreen;
import fr.frinn.custommachinery.client.screen.creation.MachineEditScreen;
import fr.frinn.custommachinery.client.screen.creation.gui.GuiElementBuilderPopup;
import fr.frinn.custommachinery.client.screen.creation.gui.IGuiElementBuilder;
import fr.frinn.custommachinery.client.screen.creation.gui.MutableProperties;
import fr.frinn.custommachinery.client.screen.popup.PopupScreen;
import fr.frinn.custommachinery.impl.guielement.AbstractGuiElement;
import java.util.function.Consumer;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public class SourceGuiElementBuilder implements IGuiElementBuilder<SourceGuiElement> {
  @Override
  public GuiElementType<SourceGuiElement> type() {
    return Registration.SOURCE_GUI_ELEMENT.get();
  }

  @Override
  public SourceGuiElement make(AbstractGuiElement.Properties properties, @Nullable SourceGuiElement from) {
    if (from != null)
      return new SourceGuiElement(properties, from.highlight());
    else
      return new SourceGuiElement(properties, true);
  }

  @Override
  public PopupScreen makeConfigPopup(MachineEditScreen parent, MutableProperties properties, @Nullable SourceGuiElement from, Consumer<SourceGuiElement> onFinish) {
    return new SourceGuiElementBuilderPopup(parent, properties, from, onFinish);
  }

  public static class SourceGuiElementBuilderPopup extends GuiElementBuilderPopup<SourceGuiElement> {
    private Checkbox highlight;

    public SourceGuiElementBuilderPopup(BaseScreen parent, MutableProperties properties, @Nullable SourceGuiElement from, Consumer<SourceGuiElement> onFinish) {
      super(parent, properties, from, onFinish);
    }

    @Override
    public SourceGuiElement makeElement() {
      return new SourceGuiElement(this.properties.build(), this.highlight.selected());
    }

    @Override
    public void addWidgets(GridLayout.RowHelper row) {
      this.addPriority(row);
      row.addChild(new StringWidget(Component.translatable("custommachinery.gui.creation.gui.highlight"), this.font));
      this.highlight = row.addChild(Checkbox.builder(Component.translatable("custommachinery.gui.creation.gui.highlight"), this.font).selected(this.baseElement == null || this.baseElement.highlight()).build());
    }
  }
}
