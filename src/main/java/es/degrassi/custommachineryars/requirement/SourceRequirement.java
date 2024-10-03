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

public record SourceRequirement(
  RequirementIOMode mode,
  int source
) implements IRequirement<SourceMachineComponent>, IJEIIngredientRequirement<Source> {
  public static final NamedCodec<SourceRequirement> CODEC = NamedCodec.record(manaRequirementInstance ->
      manaRequirementInstance.group(
        RequirementIOMode.CODEC.fieldOf("mode").forGetter(IRequirement::getMode),
        NamedCodec.intRange(0, Integer.MAX_VALUE).fieldOf("source").forGetter(requirement -> requirement.source)
      ).apply(manaRequirementInstance, SourceRequirement::new),
    "Source requirement"
  );

  @Override
  public List<IJEIIngredientWrapper<Source>> getJEIIngredientWrappers(IMachineRecipe recipe, RecipeRequirement<?, ?> requirement) {
    return Collections.singletonList(new SourceIngredientWrapper(this.getMode(), this.source, false, recipe.getRecipeTime()));
  }

  @Override
  public RequirementType<SourceRequirement> getType() {
    return Registration.SOURCE_REQUIREMENT.get();
  }

  @Override
  public MachineComponentType<SourceMachineComponent> getComponentType() {
    return Registration.SOURCE_MACHINE_COMPONENT.get();
  }

  @Override
  public RequirementIOMode getMode() {
    return this.mode;
  }

  @Override
  public boolean test(SourceMachineComponent component, ICraftingContext context) {
    return switch (getMode()) {
      case INPUT -> component.getSource() >= source;
      case OUTPUT -> component.getCapacity() - component.getSource() >= source;
    };
  }

  @Override
  public void gatherRequirements(IRequirementList<SourceMachineComponent> list) {
    if(this.mode == RequirementIOMode.INPUT)
      list.processOnStart(this::processInputs);
    else
      list.processOnEnd(this::processOutputs);
  }

  private CraftingResult processInputs(SourceMachineComponent component, ICraftingContext context) {
    int amount = (int)context.getIntegerModifiedValue(this.source, this, null);
    int canExtract = component.extractSource(amount, true);
    if(canExtract == amount) {
      component.extractSource(amount, false);
      return CraftingResult.success();
    }
    return CraftingResult.error(Component.translatable(
      "custommachineryars.requirements.source.error.input",
      source,
      component.getSource()
    ));
  }

  private CraftingResult processOutputs(SourceMachineComponent component, ICraftingContext context) {
    int amount = (int)context.getIntegerModifiedValue(this.source, this, null);
    int canReceive = component.receiveSource(amount, true);
    if(canReceive == amount) {
      component.receiveSource(amount, false);
      return CraftingResult.success();
    }
    return CraftingResult.error(Component.translatable(
      "custommachineryars.requirements.source.error.output",
      source
    ));
  }
}
