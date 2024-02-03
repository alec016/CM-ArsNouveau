package al.alec.custommachineryars.components.variant.item;

import al.alec.custommachineryars.Registration;
import al.alec.custommachineryars.components.SourceMachineComponent;
import al.alec.custommachineryars.util.SourceHelper;
import fr.frinn.custommachinery.CustomMachinery;
import fr.frinn.custommachinery.api.codec.NamedCodec;
import fr.frinn.custommachinery.api.component.IMachineComponentManager;
import fr.frinn.custommachinery.api.component.variant.IComponentVariant;
import fr.frinn.custommachinery.api.component.variant.ITickableComponentVariant;
import fr.frinn.custommachinery.common.component.ItemMachineComponent;
import fr.frinn.custommachinery.impl.component.variant.ItemComponentVariant;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SourceItemComponentVariant extends ItemComponentVariant implements ITickableComponentVariant<ItemMachineComponent> {
  public static final SourceItemComponentVariant INSTANCE = new SourceItemComponentVariant();
  public static final NamedCodec<SourceItemComponentVariant> CODEC = NamedCodec.unit(INSTANCE, "Source item component");
  public static final ResourceLocation ID = new ResourceLocation(CustomMachinery.MODID, "source");
  @Override
  public boolean canAccept(IMachineComponentManager manager, ItemStack stack) {
    CompoundTag nbt = stack.serializeNBT();
    return nbt.contains("source");
  }

  @Override
  public ResourceLocation getId() {
    return ID;
  }

  @Override
  public NamedCodec<SourceItemComponentVariant> getCodec() {
    return CODEC;
  }

  @Override
  public void tick(@NotNull ItemMachineComponent slot) {
    ItemStack stack = slot.getItemStack();
    CompoundTag nbt = stack.serializeNBT();
    if (stack.isEmpty() || !nbt.contains("source") || slot.getManager().getComponent(Registration.SOURCE_MACHINE_COMPONENT.get()).isEmpty())
      return;

    SourceMachineComponent buffer = slot.getManager().getComponent(Registration.SOURCE_MACHINE_COMPONENT.get()).get();

    if (slot.getMode().isInput())
      SourceHelper.INSTANCE.fillBufferFromStack(buffer, stack);
    else if (slot.getMode().isOutput())
      SourceHelper.INSTANCE.fillStackFromBuffer(stack, buffer);
  }
}
