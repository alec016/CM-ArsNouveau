package al.alec.custommachineryars.util;

import com.hollingsworth.arsnouveau.api.source.ISourceTile;
import net.minecraft.core.BlockPos;

public interface IWandableMachineTile {
  BlockPos cma$getToPos();
  BlockPos cma$getFromPos();
  void cma$setFromPos(BlockPos pos);
  void cma$setToPos(BlockPos pos);
  int cma$transferSource(ISourceTile from, ISourceTile to);
  int cma$getTransferRate(ISourceTile from, ISourceTile to);
}
