package al.alec.custommachineryars.network.syncable;

import al.alec.custommachineryars.network.data.BlockPosData;
import fr.frinn.custommachinery.common.network.data.BooleanData;
import fr.frinn.custommachinery.common.network.syncable.BooleanSyncable;
import fr.frinn.custommachinery.impl.network.AbstractSyncable;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;

public abstract class BlockPosSyncable extends AbstractSyncable<BlockPosData, BlockPos> {

  @Override
  public BlockPosData getData(short id) {
    return new BlockPosData(id, get());
  }

  public static BlockPosSyncable create(Supplier<BlockPos> supplier, Consumer<BlockPos> consumer) {
    return new BlockPosSyncable() {
      @Override
      public BlockPos get() {
        return supplier.get();
      }

      @Override
      public void set(BlockPos value) {
        consumer.accept(value);
      }
    };
  }
}
