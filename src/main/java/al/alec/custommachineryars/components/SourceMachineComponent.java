package al.alec.custommachineryars.components;

import al.alec.custommachineryars.Registration;
import al.alec.custommachineryars.client.integration.jei.source.Source;
import al.alec.custommachineryars.util.IWandableMachineTile;
import com.hollingsworth.arsnouveau.api.source.AbstractSourceMachine;
import com.hollingsworth.arsnouveau.api.source.ISourceTile;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
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
import fr.frinn.custommachinery.common.network.syncable.StringSyncable;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

@SuppressWarnings("unused")
public class SourceMachineComponent implements IMachineComponent, ITickableComponent, ISerializableComponent, IComparatorInputComponent, IDumpComponent, ISyncableStuff, ISourceTile {
  private int source;
  private final int capacity, maxIn, maxOut;
  private final IMachineComponentManager manager;
  private ComponentIOMode mode;

  public SourceMachineComponent(IMachineComponentManager manager) {
    this (manager, ComponentIOMode.BOTH, 1, 0, 0);
  }

  public SourceMachineComponent(IMachineComponentManager manager, ComponentIOMode mode, int capacity, int maxIn, int maxOut) {
    this.manager = manager;
    this.mode = mode;
    this.capacity = capacity;
    this.maxIn = Math.min(maxIn, capacity);
    this.maxOut = Math.min(maxOut, capacity);
  }

  public ComponentIOMode setMode(ComponentIOMode mode) {
    this.mode = mode;
    this.getManager().markDirty();
    return mode;
  }

  @Override
  public int getComparatorInput() {
    return (int) (15 * ((double)this.source / (double)this.capacity));
  }

  @Override
  public int getTransferRate() {
    return switch(getMode()) {
      case INPUT -> maxIn;
      case OUTPUT -> maxOut;
      case BOTH -> Math.min(maxIn, maxOut);
      case NONE -> 0;
    };
  }

  @Override
  public boolean canAcceptSource() {
    return getMode() == ComponentIOMode.INPUT || getMode() == ComponentIOMode.BOTH;
  }

  public int getSource () {
    return this.source;
  }

  @Override
  public int getMaxSource() {
    return capacity;
  }

  @Override
  public void setMaxSource(int max) {

  }

  public int setSource (int source) {
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

  public int receiveSource (int maxReceive, boolean simulate) {
    if (this.getMaxInput() <= 0) return 0;
    int manaReceived = Math.min(this.getCapacity() - this.getSource(), Math.min(this.getMaxInput(), maxReceive));
    if (!simulate && manaReceived > 0) {
      this.setSource(this.getSource() + manaReceived);
      this.getManager().markDirty();
    }
    return manaReceived;
  }

  public int extractSource(int extract) {
    return extractSource (extract, false);
  }

  public int extractSource (int maxExtract, boolean simulate) {
    if (this.getMaxOutput() <= 0) return 0;
    int manaExtracted = Math.min(this.getSource(), Math.min(this.getMaxOutput(), maxExtract));
    if (!simulate && manaExtracted > 0) {
      this.setSource(this.getSource() - manaExtracted);
      this.getManager().markDirty();
    }
    return manaExtracted;
  }

  public double getFillPercent() {
    return (double)this.source / this.capacity;
  }

  public boolean isFull() {
    return this.capacity == this.source;
  }

  public int getCapacity () {
    return this.capacity;
  }

  public int getMaxInput () {
    return maxIn;
  }

  public int getMaxOutput () {
    return maxOut;
  }

  @Override
  public void dump(List<String> ids) {
    setSource(0);
    this.getManager().markDirty();
  }

  @Override
  public void serialize(CompoundTag nbt) {
    nbt.putInt("source", this.source);
    nbt.putString("mode", this.mode.toString());
  }

  @Override
  public void deserialize(CompoundTag nbt) {
    if (nbt.contains("source", Tag.TAG_INT))
      this.source = Math.min(nbt.getInt("source"), this.capacity);
    if (nbt.contains("mode", Tag.TAG_STRING))
      this.mode = ComponentIOMode.value(nbt.getString("mode"));
  }

  @Override
  public MachineComponentType<?> getType() {
    return Registration.SOURCE_MACHINE_COMPONENT.get();
  }

  @Override
  public ComponentIOMode getMode() {
    return mode;
  }

  @Override
  public IMachineComponentManager getManager() {
    return manager;
  }

  @Override
  public void getStuffToSync(Consumer<ISyncable<?, ?>> container) {
    container.accept(IntegerSyncable.create(() -> this.source, mana -> this.source = mana));
    container.accept(StringSyncable.create(() -> this.getMode().toString().toLowerCase(Locale.ENGLISH), modeS -> this.mode = ComponentIOMode.value(modeS)));
  }

  @Override
  public void serverTick () {
    IWandableMachineTile wandableMachine = (IWandableMachineTile) manager.getTile();
    if (wandableMachine.cma$getFromPos() != null && manager.getLevel().isLoaded(wandableMachine.cma$getFromPos())) {
      // Block has been removed
      if (!(manager.getLevel().getBlockEntity(wandableMachine.cma$getFromPos()) instanceof AbstractSourceMachine)) {
        if ((manager.getLevel().getBlockEntity(wandableMachine.cma$getFromPos()) instanceof CustomMachineTile tile)) {
          if (tile.getComponentManager().getComponent(Registration.SOURCE_MACHINE_COMPONENT.get()).isEmpty()) {
            wandableMachine.cma$setFromPos(null);
          } else {
            if (wandableMachine.cma$transferSource((ISourceTile) tile, this) > 0) {
              ParticleUtil.spawnFollowProjectile(manager.getLevel(), wandableMachine.cma$getFromPos(), manager.getTile().getBlockPos());
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
          ParticleUtil.spawnFollowProjectile(manager.getLevel(), wandableMachine.cma$getFromPos(), manager.getTile().getBlockPos());
        }
      }
    }

    if (wandableMachine.cma$getToPos() != null && manager.getLevel().isLoaded(wandableMachine.cma$getToPos())) {
      if (!(manager.getLevel().getBlockEntity(wandableMachine.cma$getToPos()) instanceof AbstractSourceMachine toTile)) {
        if ((manager.getLevel().getBlockEntity(wandableMachine.cma$getToPos()) instanceof CustomMachineTile tile)) {
          if (tile.getComponentManager().getComponent(Registration.SOURCE_MACHINE_COMPONENT.get()).isEmpty()) {
            wandableMachine.cma$setToPos(null);
          } else {
            if (wandableMachine.cma$transferSource((ISourceTile) tile, this) > 0) {
              ParticleUtil.spawnFollowProjectile(manager.getLevel(), wandableMachine.cma$getToPos(), manager.getTile().getBlockPos());
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
        ParticleUtil.spawnFollowProjectile(manager.getLevel(), manager.getTile().getBlockPos(), wandableMachine.cma$getToPos());
      }
    }
  }
  public static class Template implements IMachineComponentTemplate<SourceMachineComponent> {

    public static final NamedCodec<Template> CODEC = NamedCodec.record(templateInstance ->
      templateInstance.group(
        NamedCodec.enumCodec(ComponentIOMode.class).optionalFieldOf("mode", ComponentIOMode.BOTH).forGetter(template -> template.mode),
        NamedCodec.intRange(1, Integer.MAX_VALUE).fieldOf("capacity").forGetter(template -> template.capacity),
        NamedCodec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("maxInput").forGetter(template -> Optional.of(template.maxInput)),
        NamedCodec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("maxOutput").forGetter(template -> Optional.of(template.maxOutput))
      ).apply(templateInstance, (mode, capacity, maxIn, maxOut) -> new Template(mode, capacity, maxIn.orElse(capacity), maxOut.orElse(capacity))), "Mana machine component"
    );

    private final int capacity, maxInput, maxOutput;
    private final ComponentIOMode mode;

    private Template(ComponentIOMode mode, int capacity, int maxInput, int maxOutput) {
      this.capacity = capacity;
      this.maxInput = maxInput;
      this.maxOutput = maxOutput;
      this.mode = mode;
    }

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
      return new SourceMachineComponent(manager, this.mode, this.capacity, this.maxInput, this.maxOutput);
    }
  }
}
