package al.alec.custommachineryars.requirement;

import al.alec.custommachineryars.Registration;
import al.alec.custommachineryars.client.integration.jei.source.Source;
import al.alec.custommachineryars.client.integration.jei.wrapper.SourceIngredientWrapper;
import al.alec.custommachineryars.components.SourceMachineComponent;
import fr.frinn.custommachinery.api.codec.NamedCodec;
import fr.frinn.custommachinery.api.component.MachineComponentType;
import fr.frinn.custommachinery.api.crafting.CraftingResult;
import fr.frinn.custommachinery.api.crafting.ICraftingContext;
import fr.frinn.custommachinery.api.crafting.IMachineRecipe;
import fr.frinn.custommachinery.api.integration.jei.IJEIIngredientRequirement;
import fr.frinn.custommachinery.api.integration.jei.IJEIIngredientWrapper;
import fr.frinn.custommachinery.api.requirement.IRequirement;
import fr.frinn.custommachinery.api.requirement.ITickableRequirement;
import fr.frinn.custommachinery.api.requirement.RequirementIOMode;
import fr.frinn.custommachinery.api.requirement.RequirementType;
import fr.frinn.custommachinery.impl.requirement.AbstractRequirement;
import java.util.Collections;
import java.util.List;
import net.minecraft.network.chat.Component;

public class SourceRequirementPerTick extends AbstractRequirement<SourceMachineComponent> implements ITickableRequirement<SourceMachineComponent>, IJEIIngredientRequirement<Source> {
  public static final NamedCodec<SourceRequirementPerTick> CODEC = NamedCodec.record(manaRequirementInstance ->
      manaRequirementInstance.group(
        RequirementIOMode.CODEC.fieldOf("mode").forGetter(IRequirement::getMode),
        NamedCodec.intRange(0, Integer.MAX_VALUE).fieldOf("source").forGetter(requirement -> requirement.source)
      ).apply(manaRequirementInstance, SourceRequirementPerTick::new),
    "Source requirement per tick"
  );

  private final int source;
  public SourceRequirementPerTick(RequirementIOMode mode, int source) {
    super(mode);
    this.source = source;
  }
  @Override
  public List<IJEIIngredientWrapper<Source>> getJEIIngredientWrappers(IMachineRecipe recipe) {
    return Collections.singletonList(new SourceIngredientWrapper(this.getMode(), this.source, true, recipe.getRecipeTime()));
  }

  @Override
  public CraftingResult processTick(SourceMachineComponent component, ICraftingContext context) {
    if (getMode() == RequirementIOMode.OUTPUT) {
      if ((component.getCapacity() - component.getSource()) < source)
        return CraftingResult.error(Component.translatable(
          "custommachineryars.requirements.sourcepertick.error.output",
          source
        ));
      component.receiveSource(source, false);
      return CraftingResult.success();
    } else if (getMode() == RequirementIOMode.INPUT) {
      if (component.getSource() < source)
        return CraftingResult.error(Component.translatable(
          "custommachineryars.requirements.sourcepertick.error.input",
          source,
          component.getSource()
        ));
      component.extractSource(source, false);
      return CraftingResult.success();
    }
    return CraftingResult.pass();
  }

  @Override
  public RequirementType<? extends IRequirement<?>> getType() {
    return Registration.SOURCE_REQUIREMENT_PER_TICK.get();
  }

  @Override
  public MachineComponentType<SourceMachineComponent> getComponentType() {
    return Registration.SOURCE_MACHINE_COMPONENT.get();
  }

  @Override
  public boolean test(SourceMachineComponent component, ICraftingContext context) {
    return switch (getMode()) {
      case INPUT -> component.getSource() >= source;
      case OUTPUT -> component.getCapacity() - component.getSource() >= source;
    };
  }

  @Override
  public CraftingResult processStart(SourceMachineComponent component, ICraftingContext context) {
    return CraftingResult.pass();
  }

  @Override
  public CraftingResult processEnd(SourceMachineComponent component, ICraftingContext context) {
    return CraftingResult.pass();
  }
}
