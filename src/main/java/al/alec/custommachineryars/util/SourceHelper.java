package al.alec.custommachineryars.util;

import al.alec.custommachineryars.components.SourceMachineComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class SourceHelper {
  public static final SourceHelper INSTANCE = new SourceHelper();

  private SourceHelper() {

  }

  public void fillBufferFromStack(SourceMachineComponent buffer, ItemStack stack) {
    CompoundTag nbt = stack.serializeNBT().copy();
    int source = nbt.getInt("source");
    int received = buffer.receiveSource(source, true);
    if (received == source){
      nbt.putInt("source", 0);
      buffer.receiveSource(source);
    } else {
      buffer.receiveSource(received);
      nbt.putInt("source", source - received);
    }
    stack.deserializeNBT(nbt);
  }
  public void fillStackFromBuffer(ItemStack stack, SourceMachineComponent buffer) {
    CompoundTag nbt = stack.serializeNBT().copy();
    int stackSource = nbt.getInt("source");
    int stackCapacity = nbt.getInt("max_source");
    if (stackSource == stackCapacity) return;
    int possibleReceive = stackCapacity - stackSource;
    int extract = 0;
    if (buffer.extractSource(possibleReceive, true) == possibleReceive) {
      nbt.putInt("source", stackCapacity);
      buffer.extractSource(possibleReceive);
    } else if ((extract = buffer.extractSource(possibleReceive, true)) < possibleReceive) {
      buffer.extractSource(extract);
      nbt.putInt("source", stackSource + extract);
    }
    stack.deserializeNBT(nbt);
  }
}
