package es.degrassi.custommachineryars.util;

import com.hollingsworth.arsnouveau.api.source.ISourceCap;
import com.hollingsworth.arsnouveau.api.source.ISourceTile;
import es.degrassi.custommachineryars.components.ISourceCapExtension;
import net.minecraft.core.BlockPos;

public interface IWandableMachineTile {
  BlockPos cma$getToPos();
  BlockPos cma$getFromPos();
  void cma$setFromPos(BlockPos pos);
  void cma$setToPos(BlockPos pos);
  int cma$transferSource(ISourceCapExtension from, ISourceCapExtension to);
  int cma$transferSource(ISourceCap from, ISourceCapExtension to);
  int cma$transferSource(ISourceCapExtension from, ISourceCap to);
  int cma$transferSource(ISourceTile from, ISourceCapExtension to);
  int cma$transferSource(ISourceCapExtension from, ISourceTile to);
  int cma$transferSource(ISourceTile from, ISourceTile to);
  int cma$getTransferRate(ISourceCapExtension from, ISourceCapExtension to);
  int cma$getTransferRate(ISourceTile from, ISourceCapExtension to);
  int cma$getTransferRate(ISourceCapExtension from, ISourceTile to);
  int cma$getTransferRate(ISourceTile from, ISourceTile to);
}
