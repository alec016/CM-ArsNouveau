package es.degrassi.custommachineryars.guielement;

import es.degrassi.custommachineryars.CustommachineryArs;
import es.degrassi.custommachineryars.Registration;
import es.degrassi.custommachineryars.components.SourceMachineComponent;
import fr.frinn.custommachinery.api.codec.NamedCodec;
import fr.frinn.custommachinery.api.component.MachineComponentType;
import fr.frinn.custommachinery.api.guielement.GuiElementType;
import fr.frinn.custommachinery.api.guielement.IComponentGuiElement;
import fr.frinn.custommachinery.api.guielement.IGuiElement;
import fr.frinn.custommachinery.impl.guielement.AbstractGuiElement;
import fr.frinn.custommachinery.impl.guielement.AbstractTexturedGuiElement;
import net.minecraft.resources.ResourceLocation;

public class SourceGuiElement extends AbstractTexturedGuiElement implements IComponentGuiElement<SourceMachineComponent> {
  private static final ResourceLocation BASE_SOURCE_STORAGE_EMPTY_TEXTURE = new ResourceLocation(CustommachineryArs.MODID, "textures/gui/base_source_storage_empty.png");

  public static final NamedCodec<SourceGuiElement> CODEC = NamedCodec.record(manaGuiElement ->
      manaGuiElement.group(
        makePropertiesCodec().forGetter(AbstractGuiElement::getProperties),
        NamedCodec.BOOL.optionalFieldOf("highlight", true).forGetter(SourceGuiElement::highlight)
      ).apply(manaGuiElement, SourceGuiElement::new),
    "Source gui element"
  );
  private final boolean highlight;

  public SourceGuiElement(Properties properties, boolean highlight) {
    super(properties, BASE_SOURCE_STORAGE_EMPTY_TEXTURE);
    this.highlight = highlight;
  }

  public ResourceLocation getEmptyTexture() {
    return BASE_SOURCE_STORAGE_EMPTY_TEXTURE;
  }

  public boolean highlight() {
    return this.highlight;
  }

  @Override
  public MachineComponentType<SourceMachineComponent> getComponentType() {
    return Registration.SOURCE_MACHINE_COMPONENT.get();
  }

  @Override
  public String getComponentId() {
    return "";
  }

  @Override
  public GuiElementType<? extends IGuiElement> getType() {
    return Registration.SOURCE_GUI_ELEMENT.get();
  }
}
