package es.degrassi.custommachineryars.requirement;

import es.degrassi.custommachineryars.Registration;
import es.degrassi.custommachineryars.client.integration.jei.source.Source;
import es.degrassi.custommachineryars.client.integration.jei.wrapper.SourceIngredientWrapper;
import es.degrassi.custommachineryars.components.SourceMachineComponent;
import fr.frinn.custommachinery.api.codec.NamedCodec;
import fr.frinn.custommachinery.api.component.MachineComponentType;
import fr.frinn.custommachinery.api.crafting.CraftingResult;
import fr.frinn.custommachinery.api.crafting.ICraftingContext;
import fr.frinn.custommachinery.api.crafting.IMachineRecipe;
import fr.frinn.custommachinery.api.crafting.IRequirementList;
import fr.frinn.custommachinery.api.integration.jei.IJEIIngredientRequirement;
import fr.frinn.custommachinery.api.integration.jei.IJEIIngredientWrapper;
import fr.frinn.custommachinery.api.requirement.IRequirement;
import fr.frinn.custommachinery.api.requirement.RecipeRequirement;
import fr.frinn.custommachinery.api.requirement.RequirementIOMode;
import fr.frinn.custommachinery.api.requirement.RequirementType;
import java.util.Collections;
import java.util.List;
import net.minecraft.network.chat.Component;

public record SourceRequirementPerTick(
  RequirementIOMode mode,
  int source
) implements IRequirement<SourceMachineComponent>, IJEIIngredientRequirement<Source> {
  public static final NamedCodec<SourceRequirementPerTick> CODEC = NamedCodec.record(manaRequirementInstance ->
      manaRequirementInstance.group(
        RequirementIOMode.CODEC.fieldOf("mode").forGetter(IRequirement::getMode),
        NamedCodec.intRange(0, Integer.MAX_VALUE).fieldOf("source").forGetter(requirement -> requirement.source)
      ).apply(manaRequirementInstance, SourceRequirementPerTick::new),
    "Source requirement per tick"
  );

  @Override
  public RequirementIOMode getMode() {
    return this.mode;
  }

  @Override
  public List<IJEIIngredientWrapper<Source>> getJEIIngredientWrappers(IMachineRecipe recipe, RecipeRequirement<?, ?> requirement) {
    return Collections.singletonList(new SourceIngredientWrapper(this.getMode(), this.source, true, recipe.getRecipeTime()));
  }

  @Override
  public void gatherRequirements(IRequirementList<SourceMachineComponent> list) {
    if(this.mode == RequirementIOMode.INPUT)
      list.processEachTick(this::processInputs);
    else
      list.processEachTick(this::processOutputs);
  }

  private CraftingResult processInputs(SourceMachineComponent component, ICraftingContext context) {
    int amount = (int)context.getPerTickIntegerModifiedValue(this.source, this, null);
    int canExtract = component.extractSourceRecipe(amount, true);
    if(canExtract == amount) {
      component.extractSourceRecipe(amount, false);
      return CraftingResult.success();
    }
    return CraftingResult.error(Component.translatable(
      "custommachineryars.requirements.sourcepertick.error.input",
      source,
      component.getSource()
    ));
  }

  private CraftingResult processOutputs(SourceMachineComponent component, ICraftingContext context) {
    int amount = (int)context.getPerTickIntegerModifiedValue(this.source, this, null);
    int canReceive = component.receiveSourceRecipe(amount, true);
    if(canReceive == amount) {
      component.receiveSourceRecipe(amount, false);
      return CraftingResult.success();
    }
    return CraftingResult.error(Component.translatable(
      "custommachineryars.requirements.sourcepertick.error.output",
      source
    ));
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
}
