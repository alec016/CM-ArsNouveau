package es.degrassi.custommachineryars.components;

import com.hollingsworth.arsnouveau.api.source.AbstractSourceMachine;
import com.hollingsworth.arsnouveau.api.source.ISourceTile;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import es.degrassi.custommachineryars.Registration;
import es.degrassi.custommachineryars.client.integration.jei.source.Source;
import es.degrassi.custommachineryars.util.IWandableMachineTile;
import fr.frinn.custommachinery.api.codec.NamedCodec;
import fr.frinn.custommachinery.api.component.ComponentIOMode;
import fr.frinn.custommachinery.api.component.IComparatorInputComponent;
import fr.frinn.custommachinery.api.component.IDumpComponent;
import fr.frinn.custommachinery.api.component.IMachineComponent;
import fr.frinn.custommachinery.api.component.IMachineComponentManager;
import fr.frinn.custommachinery.api.component.IMachineComponentTemplate;
import fr.frinn.custommachinery.api.component.ISerializableComponent;
import fr.frinn.custommachinery.api.component.ITickableComponent;
import fr.frinn.custommachinery.api.component.MachineComponentType;
import fr.frinn.custommachinery.api.network.ISyncable;
import fr.frinn.custommachinery.api.network.ISyncableStuff;
import fr.frinn.custommachinery.common.init.CustomMachineTile;
import fr.frinn.custommachinery.common.network.syncable.IntegerSyncable;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public class SourceMachineComponent implements IMachineComponent, ITickableComponent, ISerializableComponent,
    IComparatorInputComponent, IDumpComponent, ISyncableStuff, ISourceTile {
  private int source;
  private final int capacity, maxIn, maxOut;
  private final IMachineComponentManager manager;

  public SourceMachineComponent(IMachineComponentManager manager) {
    this(manager, 1, 0, 0);
  }

  public SourceMachineComponent(IMachineComponentManager manager, int capacity, int maxIn, int maxOut) {
    this.manager = manager;
    this.capacity = capacity;
    this.maxIn = Math.min(maxIn, capacity);
    this.maxOut = Math.min(maxOut, capacity);
  }

  @Override
  public int getComparatorInput() {
    return (int) (15 * ((double) this.source / (double) this.capacity));
  }

  @Override
  public int getTransferRate() {
    return switch (getMode()) {
      case INPUT -> maxIn;
      case OUTPUT -> maxOut;
      case BOTH -> Math.min(maxIn, maxOut);
      case NONE -> 0;
    };
  }

  @Override
  public boolean canAcceptSource() {
    return true;
  }

  @Override
  public boolean canProvideSource() {
    return this.getSource() > 0;
  }

  public int getSource() {
    return this.source;
  }

  @Override
  public int getMaxSource() {
    return capacity;
  }

  public int setSource(int source) {
    this.source = source;
    getManager().markDirty();
    return source;
  }

  @Override
  public int addSource(int source) {
    return receiveSource(source);
  }

  @Override
  public int removeSource(int source) {
    return extractSource(source);
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
      this.manager.markDirty();
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
      this.manager.markDirty();
    }
    return sourceExtracted;
  }

  public double getFillPercent() {
    return (double) this.source / this.capacity;
  }

  public boolean isFull() {
    return this.capacity == this.source;
  }

  public int getCapacity() {
    return this.capacity;
  }

  public int getMaxInput() {
    return maxIn;
  }

  public int getMaxOutput() {
    return maxOut;
  }

  @Override
  public void dump(List<String> ids) {
    setSource(0);
    this.manager.markDirty();
  }

  @Override
  public void serialize(CompoundTag nbt, HolderLookup.Provider registries) {
    nbt.putInt("source", this.source);
  }

  @Override
  public void deserialize(CompoundTag nbt, HolderLookup.Provider registries) {
    if (nbt.contains("source", Tag.TAG_INT))
      this.source = Math.min(nbt.getInt("source"), this.capacity);
  }

  @Override
  public MachineComponentType<?> getType() {
    return Registration.SOURCE_MACHINE_COMPONENT.get();
  }

  @Override
  public ComponentIOMode getMode() {
    return ComponentIOMode.BOTH;
  }

  @Override
  public IMachineComponentManager getManager() {
    return manager;
  }

  @Override
  public void getStuffToSync(Consumer<ISyncable<?, ?>> container) {
    container.accept(IntegerSyncable.create(() -> this.source, mana -> this.source = mana));
  }

  private ParticleColor getParticleColor() {
    return ParticleColor.defaultParticleColor();
  }

  @Override
  public void serverTick() {
    IWandableMachineTile wandableMachine = (IWandableMachineTile) manager.getTile();
    if (wandableMachine.cma$getFromPos() != null && manager.getLevel().isLoaded(wandableMachine.cma$getFromPos())) {
      // Block has been removed
      if (!(manager.getLevel().getBlockEntity(wandableMachine.cma$getFromPos()) instanceof AbstractSourceMachine)) {
        if ((manager.getLevel().getBlockEntity(wandableMachine.cma$getFromPos()) instanceof CustomMachineTile tile)) {
          if (tile.getComponentManager().getComponent(Registration.SOURCE_MACHINE_COMPONENT.get()).isEmpty()) {
            wandableMachine.cma$setFromPos(null);
          } else {
            if (wandableMachine.cma$transferSource(tile.getComponentManager().getComponent(Registration.SOURCE_MACHINE_COMPONENT.get()).get(), this) > 0) {
              ParticleUtil.spawnFollowProjectile(manager.getLevel(), wandableMachine.cma$getFromPos(),
                  manager.getTile().getBlockPos(), getParticleColor());
            }
          }
          manager.markDirty();
          return;
        }
        wandableMachine.cma$setFromPos(null);
        manager.markDirty();
      } else if (manager.getLevel().getBlockEntity(wandableMachine.cma$getFromPos()) instanceof AbstractSourceMachine fromTile) {
        // Transfer mana fromPos to this
        if (wandableMachine.cma$transferSource(fromTile, this) > 0) {
          manager.markDirty();
          ParticleUtil.spawnFollowProjectile(manager.getLevel(), wandableMachine.cma$getFromPos(),
              manager.getTile().getBlockPos(), getParticleColor());
        }
      }
    }

    if (wandableMachine.cma$getToPos() != null && manager.getLevel().isLoaded(wandableMachine.cma$getToPos())) {
      if (!(manager.getLevel().getBlockEntity(wandableMachine.cma$getToPos()) instanceof AbstractSourceMachine toTile)) {
        if ((manager.getLevel().getBlockEntity(wandableMachine.cma$getToPos()) instanceof CustomMachineTile tile)) {
          if (tile.getComponentManager().getComponent(Registration.SOURCE_MACHINE_COMPONENT.get()).isEmpty()) {
            wandableMachine.cma$setToPos(null);
          } else {
            if (wandableMachine.cma$transferSource(tile.getComponentManager().getComponent(Registration.SOURCE_MACHINE_COMPONENT.get()).get(), this) > 0) {
              ParticleUtil.spawnFollowProjectile(manager.getLevel(), wandableMachine.cma$getToPos(),
                  manager.getTile().getBlockPos(), getParticleColor());
            }
          }
          manager.markDirty();
          return;
        }
        wandableMachine.cma$setToPos(null);
        manager.markDirty();
        return;
      }
      if (wandableMachine.cma$transferSource(this, toTile) > 0) {
        ParticleUtil.spawnFollowProjectile(manager.getLevel(), manager.getTile().getBlockPos(),
            wandableMachine.cma$getToPos(), getParticleColor());
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
      return "";
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
