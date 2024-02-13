package al.alec.custommachineryars.client.handler;

import al.alec.custommachineryars.CustomMachineryArsNouveau;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = CustomMachineryArsNouveau.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class MiscModels {
  public static final MiscModels INSTANCE = new MiscModels();

  public final Material SOURCE = mainAtlas("ars_nouveau", "blocks/source_still");

  public void onModelRegister(ResourceManager manager, Consumer<ResourceLocation> consumer) {
    ModelBakery.UNREFERENCED_TEXTURES.add(SOURCE);
  }

  private MiscModels() {
  }

  @SubscribeEvent
  @OnlyIn(Dist.CLIENT)
  public static void onModelRegister(ModelEvent.RegisterAdditional evt) {
    MiscModels.INSTANCE.onModelRegister(Minecraft.getInstance().getResourceManager(), evt::register);
  }

  private Material mainAtlas(String namespace, String path) {
    return new Material(InventoryMenu.BLOCK_ATLAS, new ResourceLocation(namespace, path));
  }
}
