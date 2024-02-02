package al.alec.custommachineryars.integration.kubejs;

import al.alec.custommachineryars.requirement.SourceRequirement;
import fr.frinn.custommachinery.api.integration.kubejs.RecipeJSBuilder;
import fr.frinn.custommachinery.api.requirement.RequirementIOMode;

public interface SourceRequirementJS extends RecipeJSBuilder {

  default RecipeJSBuilder requireSource(int source) {
    return addSourceRequirement(RequirementIOMode.INPUT, source);
  }

  default RecipeJSBuilder produceSource(int source) {
    return addSourceRequirement(RequirementIOMode.OUTPUT, source);
  }

  default RecipeJSBuilder addSourceRequirement(RequirementIOMode mode, int source) {
    if (source < 0)
      return error("Source value cannot be negative");
    return addRequirement(new SourceRequirement(mode, source));
  }
}
