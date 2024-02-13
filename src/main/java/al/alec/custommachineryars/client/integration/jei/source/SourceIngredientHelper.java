package al.alec.custommachineryars.client.integration.jei.source;

import al.alec.custommachineryars.client.integration.jei.CustomIngredientTypes;
import fr.frinn.custommachinery.CustomMachinery;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SourceIngredientHelper implements IIngredientHelper<Source> {
  @Override
  public @NotNull IIngredientType<Source> getIngredientType() {
    return CustomIngredientTypes.SOURCE;
  }

  @Override
  public @NotNull String getDisplayName(Source source) {
    return Component.translatable("custommachineryars.jei.ingredient.source", source.getAmount()).getString();
  }

  @Override
  public @NotNull String getUniqueId(Source mana, @NotNull UidContext context) {
    return "" + mana.getAmount() + mana.isPerTick();
  }

  @Override
  public @NotNull Source copyIngredient(Source energy) {
    return new Source(energy.getAmount(), energy.isPerTick());
  }

  @Override
  public @NotNull String getErrorInfo(@Nullable Source energy) {
    return "";
  }

  @Override
  public @NotNull ResourceLocation getResourceLocation(@NotNull Source ingredient) {
    return new ResourceLocation(CustomMachinery.MODID, "source");
  }
}
