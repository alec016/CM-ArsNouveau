package es.degrassi.custommachineryars;

import es.degrassi.custommachineryars.components.SourceMachineComponent;
import es.degrassi.custommachineryars.components.item.SourceItemMachineComponent;
import es.degrassi.custommachineryars.guielement.SourceGuiElement;
import es.degrassi.custommachineryars.requirement.SourceRequirement;
import es.degrassi.custommachineryars.requirement.SourceRequirementPerTick;
import fr.frinn.custommachinery.CustomMachinery;
import fr.frinn.custommachinery.api.component.MachineComponentType;
import fr.frinn.custommachinery.api.guielement.GuiElementType;
import fr.frinn.custommachinery.api.guielement.IGuiElement;
import fr.frinn.custommachinery.api.network.DataType;
import fr.frinn.custommachinery.api.requirement.RequirementType;
import fr.frinn.custommachinery.common.component.handler.ItemComponentHandler;
import fr.frinn.custommachinery.common.component.item.ItemMachineComponent;
import java.util.function.Supplier;
import net.neoforged.neoforge.registries.DeferredRegister;

@SuppressWarnings("unused")
public abstract class Registration {
  public static final DeferredRegister<GuiElementType<? extends IGuiElement>> GUI_ELEMENTS = DeferredRegister.create(GuiElementType.REGISTRY_KEY, CustomMachinery.MODID);
  public static final DeferredRegister<MachineComponentType<?>> MACHINE_COMPONENTS = DeferredRegister.create(MachineComponentType.REGISTRY_KEY, CustomMachinery.MODID);
  public static final DeferredRegister<RequirementType<?>> REQUIREMENTS = DeferredRegister.create(RequirementType.REGISTRY_KEY, CustomMachinery.MODID);
  public static final DeferredRegister<DataType<?, ?>> DATAS = DeferredRegister.create(DataType.REGISTRY_KEY, CustomMachinery.MODID);

  public static final Supplier<GuiElementType<SourceGuiElement>> SOURCE_GUI_ELEMENT = GUI_ELEMENTS.register("source", () -> GuiElementType.create(SourceGuiElement.CODEC));
  public static final Supplier<MachineComponentType<SourceMachineComponent>> SOURCE_MACHINE_COMPONENT = MACHINE_COMPONENTS.register("source", () -> MachineComponentType.create(SourceMachineComponent.Template.CODEC));
  public static final Supplier<RequirementType<SourceRequirement>> SOURCE_REQUIREMENT = REQUIREMENTS.register("source", () -> RequirementType.world(SourceRequirement.CODEC));
  public static final Supplier<RequirementType<SourceRequirementPerTick>> SOURCE_REQUIREMENT_PER_TICK = REQUIREMENTS.register("source_per_tick", () -> RequirementType.world(SourceRequirementPerTick.CODEC));

  public static final Supplier<MachineComponentType<ItemMachineComponent>> ITEM_SOURCE_MACHINE_COMPONENT = MACHINE_COMPONENTS.register("item_source", () -> MachineComponentType.create(SourceItemMachineComponent.Template.CODEC).setNotSingle(ItemComponentHandler::new));

}
