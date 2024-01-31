package al.alec.custommachineryars.forge.client;


import al.alec.custommachineryars.CustomMachineryArsNouveau;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD, modid = CustomMachineryArsNouveau.MODID)
public class CustomMachineryArsNouveauForgeClient {

  @SubscribeEvent
  public static void clientInit(FMLClientSetupEvent event) {
    var bus = MinecraftForge.EVENT_BUS;
    bus.addGenericListener(BlockEntity.class, CustomMachineryArsNouveauForgeClient::attachBeCapabilities);
  }

  private static void attachBeCapabilities(AttachCapabilitiesEvent<BlockEntity> e) {
    var be = e.getObject();

//    var makeWandHud = WAND_HUD.get().get(be.getType());
//    if (makeWandHud != null) {
//      e.addCapability(prefix("wand_hud"),
//        CapabilityUtil.makeProvider(BotaniaForgeClientCapabilities.WAND_HUD, makeWandHud.apply(be)));
//    }
  }
}
