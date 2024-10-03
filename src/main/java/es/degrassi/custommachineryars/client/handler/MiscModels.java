package es.degrassi.custommachineryars.client.handler;

import es.degrassi.custommachineryars.CustommachineryArs;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;

@SuppressWarnings("unused")
@EventBusSubscriber(modid = CustommachineryArs.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class MiscModels {
  public static final MiscModels INSTANCE = new MiscModels();

  public final Material SOURCE = mainAtlas("ars_nouveau", "blocks/mana_still");

  public void onModelRegister(ResourceManager manager, Consumer<ModelResourceLocation> consumer) {

//    ModelBakery.
//    consumer.accept(new ResourceLocation("ars_nouveau", "blocks/mana_still"));
  }

  private MiscModels() {
  }

  @SubscribeEvent
  @OnlyIn(Dist.CLIENT)
  public static void onModelRegister(ModelEvent.RegisterAdditional evt) {
    MiscModels.INSTANCE.onModelRegister(Minecraft.getInstance().getResourceManager(), evt::register);
  }

  private Material mainAtlas(String namespace, String path) {
    return new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.fromNamespaceAndPath(namespace, path));
  }
}
