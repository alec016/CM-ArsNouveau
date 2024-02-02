package al.alec.custommachineryars.integration.crafttweaker.machine;

import al.alec.custommachineryars.requirement.SourceRequirement;
import al.alec.custommachineryars.requirement.SourceRequirementPerTick;
import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import fr.frinn.custommachinery.api.requirement.RequirementIOMode;
import fr.frinn.custommachinery.common.integration.crafttweaker.CTConstants;
import fr.frinn.custommachinery.common.integration.crafttweaker.CustomMachineRecipeCTBuilder;
import org.openzen.zencode.java.ZenCodeType;

@ZenRegister
@ZenCodeType.Expansion(CTConstants.RECIPE_BUILDER_MACHINE)
public class SourceRequirementCT {

  @ZenCodeType.Method
  public static CustomMachineRecipeCTBuilder requireSource(CustomMachineRecipeCTBuilder builder, int source) {
    return addSourceRequirement(builder, RequirementIOMode.INPUT, source, false);
  }

  @ZenCodeType.Method
  public static CustomMachineRecipeCTBuilder requireSourcePerTick(CustomMachineRecipeCTBuilder builder, int source) {
    return addSourceRequirement(builder, RequirementIOMode.INPUT, source, true);
  }

  @ZenCodeType.Method
  public static CustomMachineRecipeCTBuilder produceSource(CustomMachineRecipeCTBuilder builder, int source) {
    return addSourceRequirement(builder, RequirementIOMode.OUTPUT, source, false);
  }

  @ZenCodeType.Method
  public static CustomMachineRecipeCTBuilder produceSourcePerTick(CustomMachineRecipeCTBuilder builder, int source) {
    return addSourceRequirement(builder, RequirementIOMode.OUTPUT, source, true);
  }

  private static CustomMachineRecipeCTBuilder addSourceRequirement(CustomMachineRecipeCTBuilder builder, RequirementIOMode mode, int source, boolean perTick) {
    if (source < 0)
      return builder.error("Source value cannot be negative");
    return builder.addRequirement(perTick ? new SourceRequirementPerTick(mode, source) : new SourceRequirement(mode, source));
  }
}
