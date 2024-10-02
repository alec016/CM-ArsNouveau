package es.degrassi.custommachineryars.mixin;

import es.degrassi.custommachineryars.integration.kubejs.SourceRequirementJS;
import fr.frinn.custommachinery.common.integration.kubejs.CustomCraftRecipeJSBuilder;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ CustomCraftRecipeJSBuilder.class})
public abstract class CustomCraftRecipeBuilderJS implements SourceRequirementJS {
}
