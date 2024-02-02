package al.alec.custommachineryars.mixin;

import com.hollingsworth.arsnouveau.api.source.AbstractSourceMachine;
import com.hollingsworth.arsnouveau.common.block.tile.RelayTile;
import fr.frinn.custommachinery.common.init.CustomMachineTile;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = RelayTile.class, remap = false)
public abstract class RelayTileMixin extends AbstractSourceMachine {

  public RelayTileMixin(BlockEntityType<?> manaTile, BlockPos pos, BlockState state) {
    super(manaTile, pos, state);
  }

  @Inject(method = "onFinishedConnectionFirst", at = @At(value = "RETURN", ordinal = 0), cancellable = true)
  public void onFinishedConnectionFirst(@Nullable BlockPos storedPos, @Nullable LivingEntity storedEntity, Player playerEntity, CallbackInfo ci) {
    if (!(level.getBlockEntity(storedPos) instanceof CustomMachineTile))
      ci.cancel();
  }

  @Inject(method = "onFinishedConnectionLast", at = @At(value = "RETURN", ordinal = 0), cancellable = true)
  private void cma$onFinishedConnectionLast(@Nullable BlockPos storedPos, @Nullable LivingEntity storedEntity, Player playerEntity, CallbackInfo ci) {
    if (!(level.getBlockEntity(storedPos) instanceof CustomMachineTile))
      ci.cancel();
  }
}
