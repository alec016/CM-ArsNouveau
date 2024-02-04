package al.alec.custommachineryars;

import al.alec.custommachineryars.client.ClientHandler;
import al.alec.custommachineryars.mixin.client.ModelBakeryAccessor;
import com.hollingsworth.arsnouveau.api.item.IWandable;
import com.hollingsworth.arsnouveau.common.items.DominionWand;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.InteractionEvent;
import dev.architectury.platform.forge.EventBuses;
import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;
import fr.frinn.custommachinery.api.component.variant.RegisterComponentVariantEvent;
import fr.frinn.custommachinery.common.init.CustomMachineTile;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(CustomMachineryArsNouveau.MODID)
public class CustomMachineryArsNouveau {
  public static final String MODID = "custommachineryars";
  public static final Material source = new Material(InventoryMenu.BLOCK_ATLAS, new ResourceLocation("ars_nouveau", "blocks/source_still"));

  public CustomMachineryArsNouveau() {
    final IEventBus MOD_BUS = FMLJavaModLoadingContext.get().getModEventBus();
    EventBuses.registerModEventBus(CustomMachineryArsNouveau.MODID, MOD_BUS);
    Registration.GUI_ELEMENTS.register(MOD_BUS);
    Registration.MACHINE_COMPONENTS.register(MOD_BUS);
    Registration.REQUIREMENTS.register(MOD_BUS);
    Registration.DATAS.register(MOD_BUS);

    RegisterComponentVariantEvent.EVENT.register(Registration::registerComponentVariants);

    EnvExecutor.runInEnv(Env.CLIENT, () -> ClientHandler::clientInit);

    InteractionEvent.RIGHT_CLICK_BLOCK.register(CustomMachineryArsNouveau::handleWandClick);
  }

  private static EventResult handleWandClick(Player player, InteractionHand hand, BlockPos pos, Direction face) {
    if (
        !player.level.isClientSide() &&
        !player.isShiftKeyDown() &&
        player.getItemInHand(hand).getItem() instanceof DominionWand wand &&
        player.level.getBlockEntity(pos) instanceof CustomMachineTile tile
    ) {
      ItemStack stack = player.getItemInHand(hand);
      DominionWand.DominionData data = new DominionWand.DominionData(stack);
      if (!data.hasStoredData()) {
        data.setStoredPos(pos.immutable());
        PortUtil.sendMessage(player, Component.translatable("ars_nouveau.dominion_wand.position_set"));
        return EventResult.interrupt(true);
      }
      if (data.getStoredPos() != null && player.getCommandSenderWorld().getBlockEntity(data.getStoredPos()) instanceof IWandable wandable) {
        wandable.onFinishedConnectionFirst(data.getStoredPos(), (LivingEntity) player.level.getEntity(data.getStoredEntityID()), player);
      }
      if (tile instanceof IWandable wandable) {
        wandable.onFinishedConnectionLast(data.getStoredPos(), (LivingEntity) player.level.getEntity(data.getStoredEntityID()), player);
        tile.getComponentManager().markDirty();
      }
      if (data.getStoredEntityID() != -1 && player.level.getEntity(data.getStoredEntityID()) instanceof IWandable wandable) {
        wandable.onFinishedConnectionFirst(pos, null, player);
      }
      wand.clear(stack, player);
      return EventResult.interrupt(true);
    }
    return EventResult.pass();
  }

  @SuppressWarnings("unused")
  @Mod.EventBusSubscriber(modid = CustomMachineryArsNouveau.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
  static class ClientSetup {
    @SubscribeEvent
    public static void onModelRegister(ModelEvent.RegisterAdditional evt) {
      var resourceManager = Minecraft.getInstance().getResourceManager();
      onModelRegister(resourceManager, evt::register);
    }

    public static void onModelRegister(ResourceManager rm, Consumer<ResourceLocation> consumer) {
      ModelBakeryAccessor.getMaterials().add(source);
    }
  }
}
