package al.alec.custommachineryars;


import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.Registries;
import fr.frinn.custommachinery.api.ICustomMachineryAPI;
import fr.frinn.custommachinery.api.component.MachineComponentType;
import fr.frinn.custommachinery.api.component.variant.RegisterComponentVariantEvent;
import fr.frinn.custommachinery.api.guielement.GuiElementType;
import fr.frinn.custommachinery.api.requirement.RequirementType;

public abstract class Registration {
  public static final Registries REGISTRIES = Registries.get(CustomMachineryArsNouveau.MODID);
  public static final DeferredRegister<GuiElementType<?>> GUI_ELEMENTS = DeferredRegister.create(ICustomMachineryAPI.INSTANCE.modid(), GuiElementType.REGISTRY_KEY);
  public static final DeferredRegister<MachineComponentType<?>> MACHINE_COMPONENTS = DeferredRegister.create(ICustomMachineryAPI.INSTANCE.modid(), MachineComponentType.REGISTRY_KEY);
  public static final DeferredRegister<RequirementType<?>> REQUIREMENTS = DeferredRegister.create(ICustomMachineryAPI.INSTANCE.modid(), RequirementType.REGISTRY_KEY);

  public static final RegistrySupplier<GuiElementType<SourceGuiElement>> SOURCE_GUI_ELEMENT = GUI_ELEMENTS.register("source", () -> GuiElementType.create(SourceGuiElement.CODEC));
  public static final RegistrySupplier<MachineComponentType<SourceMachineComponent>> SOURCE_MACHINE_COMPONENT = MACHINE_COMPONENTS.register("source", () -> MachineComponentType.create(SourceMachineComponent.Template.CODEC));
  public static final RegistrySupplier<RequirementType<SourceRequirement>> SOURCE_REQUIREMENT = REQUIREMENTS.register("source", () -> RequirementType.world(SourceRequirement.CODEC));
  public static final RegistrySupplier<RequirementType<SourceRequirementPerTick>> SOURCE_REQUIREMENT_PER_TICK = REQUIREMENTS.register("source_per_tick", () -> RequirementType.world(SourceRequirementPerTick.CODEC));


  public static void registerComponentVariants(RegisterComponentVariantEvent event) {
    event.register(fr.frinn.custommachinery.common.init.Registration.ITEM_MACHINE_COMPONENT.get(), SourceItemComponentVariant.ID, SourceItemComponentVariant.CODEC);
  }
}
