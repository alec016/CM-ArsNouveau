package es.degrassi.custommachineryars.util;

import com.hollingsworth.arsnouveau.common.items.data.BlockFillContents;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import es.degrassi.custommachineryars.components.SourceMachineComponent;
import net.minecraft.world.item.ItemStack;

public class SourceHelper {
  public static final SourceHelper INSTANCE = new SourceHelper();

  private SourceHelper() {}

  public void fillBufferFromStack(SourceMachineComponent buffer, ItemStack stack) {

    BlockFillContents tag = stack.getComponents().get(DataComponentRegistry.BLOCK_FILL_CONTENTS.get());
    int itemCount = stack.getCount();
    assert tag != null;
    int source = tag.amount() * itemCount;
    int received = buffer.receiveSource(source, true);
    BlockFillContents newContent;
    if (received == source){
      newContent = new BlockFillContents(0);
//      tag.putInt("source", 0);
      buffer.receiveSource(source);
    } else {
      buffer.receiveSource(received);
      newContent = new BlockFillContents((source - received) / itemCount);
//      nbt.putInt("source", (source - received) / itemCount);
    }
    stack.set(DataComponentRegistry.BLOCK_FILL_CONTENTS.get(), newContent);
  }

  public void fillStackFromBuffer(ItemStack stack, SourceMachineComponent buffer) {

    BlockFillContents nbt = stack.getComponents().get(DataComponentRegistry.BLOCK_FILL_CONTENTS.get());
//    CompoundTag nbt = stack.getComponents().getOrDefault(DataComponents.BLOCK_ENTITY_DATA, CustomData.of(new CompoundTag())).copyTag();
    int stackSource = nbt.amount();
    int stackCapacity = stack.is(BlockRegistry.SOURCE_JAR.asItem()) ? 10000 : 0;
    if (stackSource == stackCapacity || stackCapacity == 0) return;
    int itemCount = stack.getCount();
    int possibleReceive = (stackCapacity - stackSource) * itemCount;
    int extract;
    BlockFillContents newContent;
    if (buffer.extractSource(possibleReceive, true) == possibleReceive) {
      buffer.extractSource(possibleReceive);
      newContent = new BlockFillContents(stackCapacity);
//      nbt.putInt("source", stackCapacity);
    } else if ((extract = buffer.extractSource(possibleReceive, true)) < possibleReceive) {
      buffer.extractSource(extract);
      newContent = new BlockFillContents(stackSource + (extract / itemCount));
//      nbt.putInt("source", stackSource + (extract / itemCount));
    } else {
      newContent = nbt;
    }
    stack.set(DataComponentRegistry.BLOCK_FILL_CONTENTS.get(), newContent);
  }
}
