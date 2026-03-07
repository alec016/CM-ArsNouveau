package es.degrassi.custommachineryars.components;

import com.hollingsworth.arsnouveau.api.source.AbstractSourceMachine;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry;
import es.degrassi.custommachineryars.Registration;
import es.degrassi.custommachineryars.client.integration.jei.source.Source;
import es.degrassi.custommachineryars.util.IWandableMachineTile;
import fr.frinn.custommachinery.api.codec.NamedCodec;
import fr.frinn.custommachinery.api.component.ComponentIOMode;
import fr.frinn.custommachinery.api.component.IComparatorInputComponent;
import fr.frinn.custommachinery.api.component.IDumpComponent;
import fr.frinn.custommachinery.api.component.IMachineComponentManager;
import fr.frinn.custommachinery.api.component.IMachineComponentTemplate;
import fr.frinn.custommachinery.api.component.ISerializableComponent;
import fr.frinn.custommachinery.api.component.ITickableComponent;
import fr.frinn.custommachinery.api.component.MachineComponentType;
import fr.frinn.custommachinery.api.network.ISyncable;
import fr.frinn.custommachinery.api.network.ISyncableStuff;
import fr.frinn.custommachinery.common.init.CustomMachineTile;
import fr.frinn.custommachinery.common.network.syncable.IntegerSyncable;
import fr.frinn.custommachinery.impl.component.AbstractMachineComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class SourceMachineComponent extends AbstractMachineComponent implements ITickableComponent, ISerializableComponent, IComparatorInputComponent,
    IDumpComponent, ISyncableStuff, ISourceCapExtension {
  private int source;
  private int clientCapacity;
  private final Supplier<Integer> capacity;
  private final Supplier<Integer> maxIn;
  private final Supplier<Integer> maxOut;

  public SourceMachineComponent(IMachineComponentManager manager, int capacity, int maxIn, int maxOut) {
    super(manager, ComponentIOMode.BOTH);
    this.capacity = upgradeableI(capacity, v -> this.source = Math.min(this.source, v)) ;
    this.maxIn = upgradeableI(maxIn, "max_input");
    this.maxOut = upgradeableI(maxOut, "max_output");
    this.clientCapacity = capacity;
  }

  private Supplier<Integer> upgradeableI(int defaultValue, Consumer<Integer> onChange) {
    Supplier<Double> supplier = this.getManager().addUpgradeableComponentValue(this, "capacity", defaultValue, 1, Integer.MAX_VALUE, value -> onChange.accept(value.intValue()));
    return () -> supplier.get().intValue();
  }

  protected Supplier<Integer> upgradeableI(int defaultValue, String target) {
    return this.upgradeableI(defaultValue, target, 0, Integer.MAX_VALUE);
  }

  @Override
  public int getComparatorInput() {
    return (int) (15 * ((double) this.source / (double) this.capacity.get()));
  }

  public boolean canAcceptSource() {
    return getMaxInput() > 0 ;
  }

  public boolean canProvideSource() {
    return getMaxOutput() > 0;
  }

  @Override
  public boolean canAcceptSource(int source) {
    return canReceive() && getSource() + source <= getSourceCapacity();
  }

  @Override
  public boolean canProvideSource(int source) {
    return canExtract() && getSource() >= source;
  }

  @Override
  public boolean canExtract() {
    return canProvideSource();
  }

  @Override
  public boolean canReceive() {
    return canAcceptSource();
  }

  @Override
  public int getMaxExtract() {
    return maxOut.get();
  }

  @Override
  public int getMaxReceive() {
    return maxIn.get();
  }

  public int getSource() {
    return this.source;
  }

  @Override
  public int getSourceCapacity() {
    return getMaxSource();
  }

  @Override
  public int getMaxSource() {
    return capacity.get();
  }

  public void setSource(int source) {
    this.source = source;
    getManager().markDirty();
  }

  @Override
  public void setMaxSource(int max) {
    this.clientCapacity = max;
  }

  public int addSource(int source) {
    return receiveSource(source);
  }

  @Override
  public int addSource(int transferRate, boolean simulate) {
    return receiveSource(transferRate, simulate);
  }

  public int removeSource(int source) {
    return extractSource(source);
  }

  @Override
  public int removeSource(int transferRate, boolean simulate) {
    return extractSource(transferRate, simulate);
  }

  public int receiveSource(int receive) {
    return receiveSource(receive, false);
  }

  public int receiveSource(int maxReceive, boolean simulate) {
    if (this.getMaxInput() <= 0) return 0;
    int sourceReceived = Math.min(this.getCapacity() - this.getSource(), Math.min(this.getMaxInput(), maxReceive));
    return receive(sourceReceived, simulate);
  }

  public int receiveSourceRecipe(int maxReceive, boolean simulate) {
    int sourceReceived = Math.min(this.getCapacity() - this.getSource(), maxReceive);
    return receive(sourceReceived, simulate);
  }

  private int receive(int sourceReceived, boolean simulate) {
    if (!simulate && sourceReceived > 0) {
      this.setSource(this.getSource() + sourceReceived);
      this.getManager().markDirty();
    }
    return sourceReceived;
  }

  public int extractSource(int extract) {
    return extractSource(extract, false);
  }

  public int extractSource(int maxExtract, boolean simulate) {
    if (this.getMaxOutput() <= 0) return 0;
    int sourceExtracted = Math.min(this.getSource(), Math.min(this.getMaxOutput(), maxExtract));
    return extract(sourceExtracted, simulate);
  }

  public int extractSourceRecipe(int maxExtract, boolean simulate) {
    int sourceExtracted = Math.min(this.getSource(), maxExtract);
    return extract(sourceExtracted, simulate);
  }

  private int extract(int sourceExtracted, boolean simulate) {
    if (!simulate && sourceExtracted > 0) {
      this.setSource(this.getSource() - sourceExtracted);
      this.getManager().markDirty();
    }
    return sourceExtracted;
  }

  public double getFillPercent() {
    return (double) this.source / this.capacity.get();
  }

  public boolean isFull() {
    return this.capacity.get() == this.source;
  }

  public int getCapacity() {
    return this.capacity.get();
  }

  public int getMaxInput() {
    return maxIn.get();
  }

  public int getMaxOutput() {
    return maxOut.get();
  }

  @Override
  public void dump(List<String> ids) {
    setSource(0);
    this.getManager().markDirty();
  }

  @Override
  public void serialize(CompoundTag nbt, HolderLookup.Provider registries) {
    nbt.putInt("source", this.source);
  }

  @Override
  public void deserialize(CompoundTag nbt, HolderLookup.Provider registries) {
    if (nbt.contains("source", Tag.TAG_INT))
      this.source = Math.min(nbt.getInt("source"), this.capacity.get());
  }

  @Override
  public MachineComponentType<?> getType() {
    return Registration.SOURCE_MACHINE_COMPONENT.get();
  }

  @Override
  public void getStuffToSync(Consumer<ISyncable<?, ?>> container) {
    container.accept(IntegerSyncable.create(() -> this.source, mana -> this.source = mana));
    container.accept(IntegerSyncable.create(this.capacity, v -> this.clientCapacity = v));
  }

  private ParticleColor getParticleColor() {
    return ParticleColor.defaultParticleColor();
  }

  @Override
  public void serverTick() {
    IWandableMachineTile wandableMachine = (IWandableMachineTile) getManager().getTile();
    Level level = getManager().getLevel();
    var fromPos = wandableMachine.cma$getFromPos();
    var toPos = wandableMachine.cma$getToPos();
    if (canAcceptSource() && fromPos != null && level.isLoaded(fromPos)) {
      var fromState = level.getBlockState(fromPos);
      var fromEntity = level.getBlockEntity(fromPos);
      var fromCap = CapabilityRegistry.SOURCE_CAPABILITY.getCapability(level, fromPos,
          fromState,
          fromEntity,
          null);
      // Block has been removed
      if (!(fromEntity instanceof AbstractSourceMachine)) {
        if ((fromEntity instanceof CustomMachineTile tile)) {
          if (tile.getComponentManager().getComponent(Registration.SOURCE_MACHINE_COMPONENT.get()).isEmpty()) {
            wandableMachine.cma$setFromPos(null);
          } else {
            if (wandableMachine.cma$transferSource(tile.getComponentManager().getComponent(Registration.SOURCE_MACHINE_COMPONENT.get()).get(), this) > 0) {
              ParticleUtil.spawnFollowProjectile(level, fromPos,
                  getManager().getTile().getBlockPos(), getParticleColor());
            }
          }
          return;
        }
        wandableMachine.cma$setFromPos(null);
      } else if (level.getBlockEntity(fromPos) instanceof AbstractSourceMachine fromTile) {
        // Transfer source fromPos to this
        if (wandableMachine.cma$transferSource(fromTile, this) > 0) {
          ParticleUtil.spawnFollowProjectile(level, fromPos,
              getManager().getTile().getBlockPos(), getParticleColor());
        }
      } else if (fromCap != null) {
        if (wandableMachine.cma$transferSource(fromCap, this) > 0) {
          ParticleUtil.spawnFollowProjectile(level, fromPos,
              getManager().getTile().getBlockPos(), getParticleColor());
        }
      }
    }

    if (canProvideSource() && toPos != null && level.isLoaded(toPos)) {
      var toState = level.getBlockState(toPos);
      var toEntity = level.getBlockEntity(toPos);
      var toCap = CapabilityRegistry.SOURCE_CAPABILITY.getCapability(level, toPos,
          toState,
          toEntity,
          null);
      if (!(toEntity instanceof AbstractSourceMachine)) {
        if ((toEntity instanceof CustomMachineTile tile)) {
          if (tile.getComponentManager().getComponent(Registration.SOURCE_MACHINE_COMPONENT.get()).isEmpty()) {
            wandableMachine.cma$setToPos(null);
          } else {
            if (wandableMachine.cma$transferSource(this, tile.getComponentManager().getComponent(Registration.SOURCE_MACHINE_COMPONENT.get()).get()) > 0) {
              ParticleUtil.spawnFollowProjectile(level, toPos,
                  getManager().getTile().getBlockPos(), getParticleColor());
            }
          }
          return;
        }
        wandableMachine.cma$setToPos(null);
      } else if (toEntity instanceof AbstractSourceMachine toTile) {
        // Transfer source fromPos to this
        if (wandableMachine.cma$transferSource(this, toTile) > 0) {
          ParticleUtil.spawnFollowProjectile(level, getManager().getTile().getBlockPos(), toPos,
              getParticleColor());
        }
      } else if (toCap != null) {
        if (wandableMachine.cma$transferSource(this, toCap) > 0) {
          ParticleUtil.spawnFollowProjectile(level, getManager().getTile().getBlockPos(),
              toPos, getParticleColor());
        }
      }
    }
  }

  public void getTooltip(List<Component> tooltip, BlockPos toPos, BlockPos fromPos) {
    if (getMaxOutput() > 0) {
      if (toPos == null) {
        tooltip.add(Component.translatable("custommachineryars.relay.no_to"));
      } else {
        tooltip.add(Component.translatable("custommachineryars.relay.one_to", 1));
      }
    }

    if (getMaxInput() > 0) {
      if (fromPos == null) {
        tooltip.add(Component.translatable("custommachineryars.relay.no_from"));
      } else {
        tooltip.add(Component.translatable("custommachineryars.relay.one_from", 1));
      }
    }
  }

  public record Template(
      int capacity,
      int maxInput,
      int maxOutput
  ) implements IMachineComponentTemplate<SourceMachineComponent> {
    public static final NamedCodec<Template> CODEC = NamedCodec.record(templateInstance ->
        templateInstance.group(
            NamedCodec.intRange(1, Integer.MAX_VALUE).fieldOf("capacity").forGetter(template -> template.capacity),
            NamedCodec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("maxInput").forGetter(template -> Optional.of(template.maxInput)),
            NamedCodec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("maxOutput").forGetter(template -> Optional.of(template.maxOutput))
        ).apply(templateInstance, (capacity, maxIn, maxOut) -> new Template(capacity,
            maxIn.orElse(capacity), maxOut.orElse(capacity))), "Source machine component"
    );

    @Override
    public MachineComponentType<SourceMachineComponent> getType() {
      return Registration.SOURCE_MACHINE_COMPONENT.get();
    }

    @Override
    public String getId() {
      return "source";
    }

    @Override
    public boolean canAccept(Object ingredient, boolean isInput, IMachineComponentManager manager) {
      return ingredient instanceof Source;
    }

    @Override
    public SourceMachineComponent build(IMachineComponentManager manager) {
      return new SourceMachineComponent(manager, this.capacity, this.maxInput, this.maxOutput);
    }
  }
}
