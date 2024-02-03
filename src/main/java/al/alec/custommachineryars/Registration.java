package al.alec.custommachineryars;


import al.alec.custommachineryars.components.SourceMachineComponent;
import al.alec.custommachineryars.components.variant.item.SourceItemComponentVariant;
import al.alec.custommachineryars.guielement.SourceGuiElement;
import al.alec.custommachineryars.network.data.BlockPosData;
import al.alec.custommachineryars.network.syncable.BlockPosSyncable;
import al.alec.custommachineryars.requirement.SourceRequirement;
import al.alec.custommachineryars.requirement.SourceRequirementPerTick;
import dev.architectury.registry.registries.Registries;
import dev.architectury.registry.registries.RegistrySupplier;
import fr.frinn.custommachinery.CustomMachinery;
import fr.frinn.custommachinery.api.ICustomMachineryAPI;
import fr.frinn.custommachinery.api.component.MachineComponentType;
import fr.frinn.custommachinery.api.component.variant.RegisterComponentVariantEvent;
import fr.frinn.custommachinery.api.guielement.GuiElementType;
import fr.frinn.custommachinery.api.network.DataType;
import fr.frinn.custommachinery.api.requirement.RequirementType;
import fr.frinn.custommachinery.common.network.data.BooleanData;
import fr.frinn.custommachinery.common.network.syncable.BooleanSyncable;
import net.minecraft.core.BlockPos;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public abstract class Registration {
  public static final Registries REGISTRIES = Registries.get(CustomMachineryArsNouveau.MODID);
  public static final DeferredRegister<GuiElementType<?>> GUI_ELEMENTS = DeferredRegister.create(GuiElementType.REGISTRY_KEY, ICustomMachineryAPI.INSTANCE.modid());
  public static final DeferredRegister<MachineComponentType<?>> MACHINE_COMPONENTS = DeferredRegister.create(MachineComponentType.REGISTRY_KEY, ICustomMachineryAPI.INSTANCE.modid());
  public static final DeferredRegister<RequirementType<?>> REQUIREMENTS = DeferredRegister.create(RequirementType.REGISTRY_KEY, ICustomMachineryAPI.INSTANCE.modid());
  public static final DeferredRegister<DataType<?, ?>> DATAS = DeferredRegister.create(DataType.REGISTRY_KEY, ICustomMachineryAPI.INSTANCE.modid());

  public static final RegistryObject<GuiElementType<SourceGuiElement>> SOURCE_GUI_ELEMENT = GUI_ELEMENTS.register("source", () -> GuiElementType.create(SourceGuiElement.CODEC));
  public static final RegistryObject<MachineComponentType<SourceMachineComponent>> SOURCE_MACHINE_COMPONENT = MACHINE_COMPONENTS.register("source", () -> MachineComponentType.create(SourceMachineComponent.Template.CODEC));
  public static final RegistryObject<RequirementType<SourceRequirement>> SOURCE_REQUIREMENT = REQUIREMENTS.register("source", () -> RequirementType.world(SourceRequirement.CODEC));
  public static final RegistryObject<RequirementType<SourceRequirementPerTick>> SOURCE_REQUIREMENT_PER_TICK = REQUIREMENTS.register("source_per_tick", () -> RequirementType.world(SourceRequirementPerTick.CODEC));
  public static final RegistryObject<DataType<BlockPosData, BlockPos>> BLOCKPOS_DATA = DATAS.register("blockpos", () -> DataType.create(BlockPos.class, BlockPosSyncable::create, BlockPosData::new));


  public static void registerComponentVariants(RegisterComponentVariantEvent event) {
    event.register(fr.frinn.custommachinery.common.init.Registration.ITEM_MACHINE_COMPONENT.get(), SourceItemComponentVariant.ID, SourceItemComponentVariant.CODEC);
  }
}
