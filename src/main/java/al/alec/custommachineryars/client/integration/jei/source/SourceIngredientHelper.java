package al.alec.custommachineryars.client.integration.jei.source;

import al.alec.custommachineryars.client.integration.jei.CustomIngredientTypes;
import fr.frinn.custommachinery.CustomMachinery;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class SourceIngredientHelper implements IIngredientHelper<Source> {
  @Override
  public IIngredientType<Source> getIngredientType() {
    return CustomIngredientTypes.SOURCE;
  }

  @Override
  public String getDisplayName(Source source) {
    return Component.translatable("custommachinerybotania.jei.ingredient.mana", source.getAmount()).getString();
  }

  @Override
  public String getUniqueId(Source mana, UidContext context) {
    return "" + mana.getAmount() + mana.isPerTick();
  }

  @Override
  public Source copyIngredient(Source energy) {
    return new Source(energy.getAmount(), energy.isPerTick());
  }

  @Override
  public String getErrorInfo(@Nullable Source energy) {
    return "";
  }

  @Override
  public ResourceLocation getResourceLocation(Source ingredient) {
    return new ResourceLocation(CustomMachinery.MODID, "source");
  }
}
