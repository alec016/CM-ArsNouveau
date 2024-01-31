package al.alec.custommachineryars.forge;

import dev.architectury.platform.forge.EventBuses;
import al.alec.custommachineryars.CustomMachineryArsNouveau;
import fr.frinn.custommachinery.common.init.CustomMachineTile;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;

@Mod(CustomMachineryArsNouveau.MODID)
public class CustomMachineryArsNouveauForge {
  public CustomMachineryArsNouveauForge() {
    final IEventBus MOD_BUS = FMLJavaModLoadingContext.get().getModEventBus();
    EventBuses.registerModEventBus(CustomMachineryArsNouveau.MODID, MOD_BUS);
    MOD_BUS.addListener(this::onRegister);
    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
  }

  private void commonSetup(FMLCommonSetupEvent evt) {
    registerEvents();
  }

  private void registerEvents () {
    IEventBus bus = MinecraftForge.EVENT_BUS;
    bus.addGenericListener(BlockEntity.class, this::attachBeCaps);
  }


  /**
   * Delay the initialization of the deferred registries because CM might not have created the registries at that time.
   */
  private void onRegister(final RegisterEvent event) {
    if(event.getRegistryKey() == Registry.BLOCK_REGISTRY)
      CustomMachineryArsNouveau.init();
  }

  private void attachBeCaps(final AttachCapabilitiesEvent<BlockEntity> e) {
    var be = e.getObject();
    if (be instanceof CustomMachineTile tile) {
//      e.addCapability(
//        prefix( "mana_receiver"),
//        CapabilityUtil.makeProvider(BotaniaForgeCapabilities.MANA_RECEIVER, (ManaReceiver) tile)
//      );
//      e.addCapability(
//        prefix("wandable"),
//        CapabilityUtil.makeProvider(BotaniaForgeCapabilities.WANDABLE, (Wandable) tile)
//      );
    }
  }
}