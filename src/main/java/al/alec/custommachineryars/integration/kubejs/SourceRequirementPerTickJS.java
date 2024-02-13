package al.alec.custommachineryars.integration.kubejs;

import al.alec.custommachineryars.requirement.SourceRequirementPerTick;
import fr.frinn.custommachinery.api.integration.kubejs.RecipeJSBuilder;
import fr.frinn.custommachinery.api.requirement.RequirementIOMode;

@SuppressWarnings("unused")
public interface SourceRequirementPerTickJS extends RecipeJSBuilder {

  default RecipeJSBuilder requireSourcePerTick(int source) {
    return addSourcePerTickRequirement(RequirementIOMode.INPUT, source);
  }

  default RecipeJSBuilder produceSourcePerTick(int source) {
    return addSourcePerTickRequirement(RequirementIOMode.OUTPUT, source);
  }

  default RecipeJSBuilder addSourcePerTickRequirement(RequirementIOMode mode, int source) {
    if (source < 0)
      return error("Source value cannot be negative");
    return addRequirement(new SourceRequirementPerTick(mode, source));
  }
}
