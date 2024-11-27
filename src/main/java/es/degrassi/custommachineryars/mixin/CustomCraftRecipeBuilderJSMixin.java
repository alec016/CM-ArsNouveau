package es.degrassi.custommachineryars.mixin;

import es.degrassi.custommachineryars.integration.kubejs.SourceRequirementJS;
import fr.frinn.custommachinery.common.integration.kubejs.CustomCraftRecipeBuilderJS;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ CustomCraftRecipeBuilderJS.class})
public abstract class CustomCraftRecipeBuilderJSMixin implements SourceRequirementJS {
}
