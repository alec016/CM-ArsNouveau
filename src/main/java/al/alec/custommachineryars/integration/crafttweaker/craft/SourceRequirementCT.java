package al.alec.custommachineryars.integration.crafttweaker.craft;

import al.alec.custommachineryars.requirement.SourceRequirement;
import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import fr.frinn.custommachinery.api.requirement.RequirementIOMode;
import fr.frinn.custommachinery.common.integration.crafttweaker.CTConstants;
import fr.frinn.custommachinery.common.integration.crafttweaker.CustomCraftRecipeCTBuilder;
import org.openzen.zencode.java.ZenCodeType;

@ZenRegister
@ZenCodeType.Expansion(CTConstants.RECIPE_BUILDER_CRAFT)
public class SourceRequirementCT {

  @ZenCodeType.Method
  public static CustomCraftRecipeCTBuilder requireSource(CustomCraftRecipeCTBuilder builder, int source) {
    return addSourceRequirement(builder, RequirementIOMode.INPUT, source);
  }

  @ZenCodeType.Method
  public static CustomCraftRecipeCTBuilder produceSource(CustomCraftRecipeCTBuilder builder, int source) {
    return addSourceRequirement(builder, RequirementIOMode.OUTPUT, source);
  }

  private static CustomCraftRecipeCTBuilder addSourceRequirement(CustomCraftRecipeCTBuilder builder, RequirementIOMode mode, int source) {
    if (source < 0)
      return builder.error("Source value cannot be negative");
    return builder.addRequirement(new SourceRequirement(mode, source));
  }
}
