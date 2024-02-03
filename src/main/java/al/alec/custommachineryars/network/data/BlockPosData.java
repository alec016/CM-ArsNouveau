package al.alec.custommachineryars.network.data;

import al.alec.custommachineryars.Registration;
import fr.frinn.custommachinery.impl.network.Data;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

public class BlockPosData extends Data<BlockPos> {

  public BlockPosData(short id, BlockPos value) {
    super(Registration.BLOCKPOS_DATA.get(), id, value);
  }

  public BlockPosData(short id, FriendlyByteBuf buffer) {
    this(id, buffer.readBlockPos());
  }

  @Override
  public void writeData(FriendlyByteBuf buffer) {
    super.writeData(buffer);
    buffer.writeBlockPos(getValue());
  }
}
