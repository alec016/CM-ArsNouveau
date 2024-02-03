package al.alec.custommachineryars.util;

import al.alec.custommachineryars.components.SourceMachineComponent;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class SourceHelper {
  public static final SourceHelper INSTANCE = new SourceHelper();

  private SourceHelper() {

  }

  public void fillBufferFromStack(SourceMachineComponent buffer, ItemStack stack) {
    CompoundTag nbt = stack.getOrCreateTag().getCompound("BlockEntityTag").copy();
    int itemCount = stack.getCount();
    int source = nbt.getInt("source") * itemCount;
    int received = buffer.receiveSource(source, true);
    if (received == source){
      nbt.putInt("source", 0);
      buffer.receiveSource(source);
    } else {
      buffer.receiveSource(received);
      nbt.putInt("source", (source - received) / itemCount);
    }
    CompoundTag newNbt = new CompoundTag();
    newNbt.put("BlockEntityTag", nbt);
    stack.setTag(newNbt);
  }
  public void fillStackFromBuffer(ItemStack stack, SourceMachineComponent buffer) {
    CompoundTag nbt = stack.getOrCreateTag().getCompound("BlockEntityTag").copy();
    int stackSource = nbt.getInt("source");
    int stackCapacity = stack.is(BlockRegistry.SOURCE_JAR.asItem()) ? 10000 : 0;
    if (stackSource == stackCapacity || stackCapacity == 0) return;
    int itemCount = stack.getCount();
    int possibleReceive = (stackCapacity - stackSource) * itemCount;
    int extract;
    if (buffer.extractSource(possibleReceive, true) == possibleReceive) {
      buffer.extractSource(possibleReceive);
      nbt.putInt("source", stackCapacity);
    } else if ((extract = buffer.extractSource(possibleReceive, true)) < possibleReceive) {
      buffer.extractSource(extract);
      nbt.putInt("source", stackSource + (extract / itemCount));
    }
    CompoundTag newNbt = new CompoundTag();
    newNbt.put("BlockEntityTag", nbt);
    stack.setTag(newNbt);
  }
}
