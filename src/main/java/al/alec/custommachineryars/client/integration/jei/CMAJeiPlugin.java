package al.alec.custommachineryars.client.integration.jei;

import al.alec.custommachineryars.CustomMachineryArsNouveau;
import al.alec.custommachineryars.client.integration.jei.source.SourceIngredientHelper;
import fr.frinn.custommachinery.client.integration.jei.DummyIngredientRenderer;
import java.util.Collections;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IModIngredientRegistration;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
@JeiPlugin
public class CMAJeiPlugin implements IModPlugin {

  public static final ResourceLocation PLUGIN_ID = new ResourceLocation(CustomMachineryArsNouveau.MODID, "jei_plugin");

  @Override
  public @NotNull ResourceLocation getPluginUid() {
    return PLUGIN_ID;
  }

  @Override
  public void registerIngredients(IModIngredientRegistration registration) {
    registration.register(CustomIngredientTypes.SOURCE, Collections.emptyList(), new SourceIngredientHelper(), new DummyIngredientRenderer<>());
  }
}