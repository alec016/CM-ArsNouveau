package es.degrassi.custommachineryars.mixin;

import com.hollingsworth.arsnouveau.api.item.IWandable;
import com.hollingsworth.arsnouveau.api.source.AbstractSourceMachine;
import com.hollingsworth.arsnouveau.api.source.ISourceTile;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.api.util.NBTUtil;
import com.hollingsworth.arsnouveau.client.particle.ColorPos;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.tile.RelayTile;
import com.hollingsworth.arsnouveau.common.items.DominionWand;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.nuggets.client.overlay.IWorldTooltipProvider;
import es.degrassi.custommachineryars.Registration;
import es.degrassi.custommachineryars.components.ISourceCapExtension;
import es.degrassi.custommachineryars.util.IWandableMachineTile;
import fr.frinn.custommachinery.api.component.IMachineComponentManager;
import fr.frinn.custommachinery.api.machine.MachineTile;
import fr.frinn.custommachinery.common.init.CustomMachineTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("deprecation, unused")
@Mixin({CustomMachineTile.class})
public abstract class CustomMachineTileMixin extends MachineTile implements IWandable, IWorldTooltipProvider, IWandableMachineTile {

  @Shadow
  public abstract IMachineComponentManager getComponentManager();

  @Unique
  private BlockPos cma$toPos;
  @Unique
  private BlockPos cma$fromPos;

  @Unique
  private static final String cma$TO = "to_";
  @Unique
  private static final String cma$FROM = "from";

  public CustomMachineTileMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
    super(type, pos, state);
  }

  @Inject(method = "saveAdditional", at = @At("TAIL"))
  private void cma$saveAdditional(CompoundTag nbt, HolderLookup.Provider registries, CallbackInfo ci) {
    if (cma$toPos != null) {
      NBTUtil.storeBlockPos(nbt, cma$TO, cma$toPos.immutable());
    } else {
      NBTUtil.removeBlockPos(nbt, cma$TO);
    }

    if (cma$fromPos != null) {
      NBTUtil.storeBlockPos(nbt, cma$FROM, cma$fromPos.immutable());
    } else {
      NBTUtil.removeBlockPos(nbt, cma$FROM);
    }
  }

  @Inject(method = "loadAdditional", at = @At("TAIL"))
  private void cma$loadAdditional(CompoundTag nbt, HolderLookup.Provider registries, CallbackInfo ci) {
    this.cma$toPos = null;
    this.cma$fromPos = null;

    if (NBTUtil.hasBlockPos(nbt, cma$TO)) {
      this.cma$toPos = NBTUtil.getBlockPos(nbt, cma$TO);
    }
    if (NBTUtil.hasBlockPos(nbt, cma$FROM)) {
      this.cma$fromPos = NBTUtil.getBlockPos(nbt, cma$FROM);
    }
  }

  @Inject(method = "getUpdateTag", at = @At("RETURN"), cancellable = true)
  private void cma$getUpdateTag(CallbackInfoReturnable<CompoundTag> cir) {
    CompoundTag nbt = cir.getReturnValue();
    NBTUtil.storeBlockPos(nbt, cma$TO, cma$toPos);
    NBTUtil.storeBlockPos(nbt, cma$FROM, cma$fromPos);
    cir.setReturnValue(nbt);
  }

  @Override
  public BlockPos cma$getToPos() {
    return cma$toPos;
  }

  @Override
  public BlockPos cma$getFromPos() {
    return cma$fromPos;
  }

  @Override
  public void cma$setFromPos(BlockPos pos) {
    this.cma$fromPos = pos;
    Objects.requireNonNull(getLevel()).sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
  }

  @Override
  public void cma$setToPos(BlockPos pos) {
    this.cma$toPos = pos;
    Objects.requireNonNull(getLevel()).sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
  }

  @Override
  public void onFinishedConnectionFirst(@Nullable BlockPos storedPos, @Nullable LivingEntity storedEntity, Player playerEntity) {
    if (
        level == null
            || storedPos == null
            || level.isClientSide
            || storedPos.equals(getBlockPos())
            || (!(level.getBlockEntity(storedPos) instanceof AbstractSourceMachine)
            && !(level.getBlockEntity(storedPos) instanceof CustomMachineTile))
    ) {
      return;
    }

    getComponentManager().getComponent(Registration.SOURCE_MACHINE_COMPONENT.get()).ifPresent(component -> {
      // Let relays take from us, no action needed.
      if (this.cma$setSendTo(storedPos.immutable())) {
        PortUtil.sendMessage(playerEntity, Component.translatable("custommachineryars.connections.send", DominionWand.getPosString(storedPos)));
        ParticleUtil.beam(storedPos, worldPosition, level);
      } else {
        PortUtil.sendMessage(playerEntity, Component.translatable("custommachineryars.connections.fail"));
      }
    });
  }

  @Override
  public void onFinishedConnectionLast(@Nullable BlockPos storedPos, @Nullable LivingEntity storedEntity, Player playerEntity) {
    if (
        level == null
            || storedPos == null
            || storedPos.equals(getBlockPos())
            || level.getBlockEntity(storedPos) instanceof RelayTile
            || (!(level.getBlockEntity(storedPos) instanceof AbstractSourceMachine)
            && !(level.getBlockEntity(storedPos) instanceof CustomMachineTile))
    ) {
      return;
    }

    getComponentManager().getComponent(Registration.SOURCE_MACHINE_COMPONENT.get()).ifPresent(component -> {
      if (this.cma$setTakeFrom(storedPos.immutable())) {
        PortUtil.sendMessage(playerEntity, Component.translatable("custommachineryars.connections.take", DominionWand.getPosString(storedPos)));
      } else {
        PortUtil.sendMessage(playerEntity, Component.translatable("custommachineryars.connections.fail"));
      }
    });
  }

  @Override
  public Result onClearConnections(Player playerEntity) {
    this.cma$clearPos();
    PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.connections.cleared"));
    return Result.CLEAR;
  }

  @Override
  public List<ColorPos> getWandHighlight(List<ColorPos> list) {
    if (cma$toPos != null) {
      list.add(ColorPos.centered(cma$toPos, ParticleColor.TO_HIGHLIGHT));
    }
    if (cma$fromPos != null) {
      list.add(ColorPos.centered(cma$fromPos, ParticleColor.FROM_HIGHLIGHT));
    }
    return list;
  }

  @Unique
  public int cma$getMaxDistance() {
    return 30;
  }

  @Unique
  public boolean cma$setTakeFrom(BlockPos pos) {
    if (
        BlockUtil.distanceFrom(pos, this.worldPosition) > cma$getMaxDistance()
            || pos.equals(getBlockPos())
    ) {
      return false;
    }
    cma$setFromPos(pos);
    return true;
  }

  @Unique
  public boolean cma$setSendTo(BlockPos pos) {
    if (
        BlockUtil.distanceFrom(pos, this.worldPosition) > cma$getMaxDistance()
            || pos.equals(getBlockPos())
            || (!(Objects.requireNonNull(level).getBlockEntity(pos) instanceof AbstractSourceMachine)
            && !(level.getBlockEntity(pos) instanceof CustomMachineTile))
    ) {
      return false;
    }
    cma$setToPos(pos);
    return true;
  }

  @Unique
  public void cma$clearPos() {
    cma$setToPos(null);
    cma$setFromPos(null);
  }

  @Override
  public int cma$transferSource(ISourceCapExtension from, ISourceCapExtension to) {
    int transferRate = cma$getTransferRate(from, to);
    if (transferRate <= 0) return 0;
    from.removeSource(transferRate);
    to.addSource(transferRate);
    this.getComponentManager().markDirty();
    return transferRate;
  }

  @Override
  public int cma$transferSource(ISourceTile from, ISourceCapExtension to) {
    int transferRate = cma$getTransferRate(from, to);
    if (transferRate <= 0) return 0;
    from.removeSource(transferRate);
    to.addSource(transferRate);
    this.getComponentManager().markDirty();
    return transferRate;
  }

  @Override
  public int cma$transferSource(ISourceCapExtension from, ISourceTile to) {
    int transferRate = cma$getTransferRate(from, to);
    if (transferRate <= 0) return 0;
    from.removeSource(transferRate);
    to.addSource(transferRate);
    this.getComponentManager().markDirty();
    return transferRate;
  }

  @Override
  public int cma$transferSource(ISourceTile from, ISourceTile to) {
    int transferRate = cma$getTransferRate(from, to);
    if (transferRate <= 0) return 0;
    from.removeSource(transferRate);
    to.addSource(transferRate);
    this.getComponentManager().markDirty();
    return transferRate;
  }

  /**
   * Gets the maximum amount of source that can be transferred from one tile to another.
   */
  @Override
  public int cma$getTransferRate(ISourceCapExtension from, ISourceCapExtension to) {
    return Math.min(Math.min(from.getTransferRate(), from.getSource()), to.getMaxSource() - to.getSource());
  }

  @Override
  public int cma$getTransferRate(ISourceTile from, ISourceCapExtension to) {
    return Math.min(Math.min(from.getTransferRate(), from.getSource()), to.getMaxSource() - to.getSource());
  }

  @Override
  public int cma$getTransferRate(ISourceCapExtension from, ISourceTile to) {
    return Math.min(Math.min(from.getTransferRate(), from.getSource()), to.getMaxSource() - to.getSource());
  }

  @Override
  public int cma$getTransferRate(ISourceTile from, ISourceTile to) {
    return Math.min(Math.min(from.getTransferRate(), from.getSource()), to.getMaxSource() - to.getSource());
  }

  @Override
  public void getTooltip(List<Component> tooltip) {
    tooltip.clear();
    getComponentManager().getComponent(Registration.SOURCE_MACHINE_COMPONENT.get()).ifPresent(component -> {
      if (cma$toPos == null) {
        tooltip.add(Component.translatable("custommachineryars.relay.no_to"));
      } else {
        tooltip.add(Component.translatable("custommachineryars.relay.one_to", 1));
      }
      if (cma$fromPos == null) {
        tooltip.add(Component.translatable("custommachineryars.relay.no_from"));
      } else {
        tooltip.add(Component.translatable("custommachineryars.relay.one_from", 1));
      }
    });
  }
}
