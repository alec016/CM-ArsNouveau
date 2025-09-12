package es.degrassi.custommachineryars.client.integration.jei;

import es.degrassi.custommachineryars.client.integration.jei.source.Source;
import es.degrassi.custommachineryars.client.integration.jei.source.SourceIngredientHelper;
import es.degrassi.custommachineryars.CustommachineryArs;
import fr.frinn.custommachinery.client.integration.jei.DummyIngredientRenderer;
import java.util.ArrayList;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IModIngredientRegistration;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
@JeiPlugin
public class CMAJeiPlugin implements IModPlugin {

  public static final ResourceLocation PLUGIN_ID = ResourceLocation.fromNamespaceAndPath(CustommachineryArs.MODID, "jei_plugin");

  @Override
  public @NotNull ResourceLocation getPluginUid() {
    return PLUGIN_ID;
  }

  @Override
  public void registerIngredients(IModIngredientRegistration registration) {
    registration.register(CustomIngredientTypes.SOURCE, new ArrayList<>(), new SourceIngredientHelper(), new DummyIngredientRenderer<>(), Source.CODEC);
  }
}