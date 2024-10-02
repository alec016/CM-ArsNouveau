package es.degrassi.custommachineryars.client.screen.creation.component.builder;

import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import es.degrassi.custommachineryars.Registration;
import es.degrassi.custommachineryars.components.SourceMachineComponent;
import es.degrassi.custommachineryars.components.SourceMachineComponent.Template;
import fr.frinn.custommachinery.api.component.ComponentIOMode;
import fr.frinn.custommachinery.api.component.MachineComponentType;
import fr.frinn.custommachinery.client.screen.BaseScreen;
import fr.frinn.custommachinery.client.screen.creation.MachineEditScreen;
import fr.frinn.custommachinery.client.screen.creation.component.ComponentBuilderPopup;
import fr.frinn.custommachinery.client.screen.creation.component.IMachineComponentBuilder;
import fr.frinn.custommachinery.client.screen.popup.PopupScreen;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public class SourceComponentBuilder implements IMachineComponentBuilder<SourceMachineComponent, Template> {
  @Override
  public MachineComponentType<SourceMachineComponent> type() {
    return Registration.SOURCE_MACHINE_COMPONENT.get();
  }

  @Override
  public PopupScreen makePopup(MachineEditScreen machineEditScreen, @Nullable Template template, Consumer<Template> consumer) {
    return new SourceComponentBuilderPopup(machineEditScreen, template, consumer);
  }

  @Override
  public void render(GuiGraphics graphics, int x, int y, int width, int height, Template template) {
    graphics.renderFakeItem(BlockRegistry.SOURCE_JAR.asItem().getDefaultInstance(), x, y + height / 2 - 8);
    graphics.drawString(Minecraft.getInstance().font, "type: " + template.getType().getId().getPath(), x + 25, y + 5, 0, false);
  }

  public static class SourceComponentBuilderPopup extends ComponentBuilderPopup<Template> {
    private EditBox capacity;
    private EditBox maxInput;
    private EditBox maxOutput;
    private CycleButton<ComponentIOMode> mode;

    public SourceComponentBuilderPopup(BaseScreen parent, Template template, Consumer<Template> onFinish) {
      super(parent, template, onFinish, Component.translatable("custommachineryars.gui.creation.components.source.title"));
    }

    @Override
    public Template makeTemplate() {
      return new Template(mode.getValue(), (int) parseLong(capacity.getValue()), (int) parseLong(maxInput.getValue()), (int) parseLong(maxOutput.getValue()));
    }

    @Override
    protected void init() {
      super.init();

      //Capacity
      this.capacity = this.propertyList.add(Component.translatable("custommachinery.gui.creation.components.capacity"), new EditBox(this.font, 0, 0, 160, 20, Component.translatable("custommachinery.gui.creation.components.capacity")));
      this.capacity.setFilter(this::checkLong);
      this.baseTemplate().ifPresentOrElse(template -> this.capacity.setValue("" + template.capacity()), () -> this.capacity.setValue("10000"));

      //mode
      this.mode = this.propertyList.add(Component.translatable("custommachinery.gui.creation.components.mode"), CycleButton.builder(ComponentIOMode::toComponent).displayOnlyValue().withValues(ComponentIOMode.values()).withInitialValue(ComponentIOMode.BOTH).create(0, 0, 180, 20, Component.translatable("custommachinery.gui.creation.components.mode")));
      this.baseTemplate().ifPresent(template -> this.mode.setValue(template.mode()));

      //Max input
      this.maxInput = this.propertyList.add(Component.translatable("custommachinery.gui.creation.components.maxInput"), new EditBox(this.font, 0, 0, 180, 20, Component.translatable("custommachinery.gui.creation.components.maxInput")));
      this.maxInput.setFilter(this::checkLong);
      this.baseTemplate().ifPresentOrElse(template -> this.maxInput.setValue("" + template.maxInput()), () -> this.maxInput.setValue("10000"));

      //Max output
      this.maxOutput = this.propertyList.add(Component.translatable("custommachinery.gui.creation.components.maxOutput"), new EditBox(this.font, 0, 0, 180, 20, Component.translatable("custommachinery.gui.creation.components.maxOutput")));
      this.maxOutput.setFilter(this::checkLong);
      this.baseTemplate().ifPresentOrElse(template -> this.maxOutput.setValue("" + template.maxOutput()), () -> this.maxOutput.setValue("10000"));
    }
  }
}
