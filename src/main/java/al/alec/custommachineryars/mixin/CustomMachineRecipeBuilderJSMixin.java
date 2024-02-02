package al.alec.custommachineryars.mixin;

import al.alec.custommachineryars.integration.kubejs.SourceRequirementJS;
import al.alec.custommachineryars.integration.kubejs.SourceRequirementPerTickJS;
import fr.frinn.custommachinery.common.integration.kubejs.CustomMachineRecipeBuilderJS;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ CustomMachineRecipeBuilderJS.class})
public abstract class CustomMachineRecipeBuilderJSMixin implements SourceRequirementJS, SourceRequirementPerTickJS {
}
