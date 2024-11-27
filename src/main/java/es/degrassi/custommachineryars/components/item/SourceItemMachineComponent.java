package es.degrassi.custommachineryars.components.item;

import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import es.degrassi.custommachineryars.Registration;
import es.degrassi.custommachineryars.util.SourceHelper;
import fr.frinn.custommachinery.api.codec.NamedCodec;
import fr.frinn.custommachinery.api.component.ComponentIOMode;
import fr.frinn.custommachinery.api.component.IMachineComponentManager;
import fr.frinn.custommachinery.api.component.ITickableComponent;
import fr.frinn.custommachinery.api.component.MachineComponentType;
import fr.frinn.custommachinery.api.utils.Filter;
import fr.frinn.custommachinery.common.component.item.ItemMachineComponent;
import fr.frinn.custommachinery.impl.component.config.IOSideConfig;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class SourceItemMachineComponent extends ItemMachineComponent implements ITickableComponent {
  public SourceItemMachineComponent(IMachineComponentManager manager, ComponentIOMode mode, String id, int capacity,
                                    int maxInput, int maxOutput, Filter<Item> filter,
                                    IOSideConfig.Template configTemplate, boolean locked) {
    super(manager, mode, id, capacity, maxInput, maxOutput, filter, configTemplate, locked);
  }

  @Override
  public boolean isItemValid(int slot, ItemStack stack) {
    return getManager().getComponent(Registration.SOURCE_MACHINE_COMPONENT.get()).isPresent()
      && stack.has(DataComponentRegistry.BLOCK_FILL_CONTENTS.get());
  }

  @Override
  public MachineComponentType<ItemMachineComponent> getType() {
    return Registration.ITEM_SOURCE_MACHINE_COMPONENT.get();
  }

  @Override
  public void serverTick() {
    ItemStack stack = getItemStack();
    if (!isItemValid(0, stack)) return;

    if (!stack.getComponents().has(DataComponentRegistry.BLOCK_FILL_CONTENTS.get())) return;

    getManager().getComponent(Registration.SOURCE_MACHINE_COMPONENT.get()).ifPresent(buffer -> {
      if (getMode().isInput())
        SourceHelper.INSTANCE.fillBufferFromStack(buffer, stack);
      else if (getMode().isOutput())
        SourceHelper.INSTANCE.fillStackFromBuffer(stack, buffer);
    });
  }

  public static class Template extends ItemMachineComponent.Template {
    public static final NamedCodec<Template> CODEC = defaultCodec(Template::new, "Source item machine component");

    public Template(String id, ComponentIOMode mode, int capacity, Optional<Integer> maxInput, Optional<Integer> maxOutput, Filter<Item> filter, Optional<IOSideConfig.Template> config, boolean locked) {
      super(id, mode, capacity, maxInput, maxOutput, filter, config, locked);
    }

    @Override
    public MachineComponentType<ItemMachineComponent> getType() {
      return Registration.ITEM_SOURCE_MACHINE_COMPONENT.get();
    }

    @Override
    public ItemMachineComponent build(IMachineComponentManager manager) {
      return new SourceItemMachineComponent(manager, this.mode, this.id, this.capacity, this.maxInput, this.maxOutput, this.filter, this.config, this.locked);
    }
  }
}
