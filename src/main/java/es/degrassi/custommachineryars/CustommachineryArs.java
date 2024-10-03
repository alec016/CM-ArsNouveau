package es.degrassi.custommachineryars;

import com.hollingsworth.arsnouveau.api.item.IWandable;
import com.hollingsworth.arsnouveau.common.items.DominionWand;
import com.hollingsworth.arsnouveau.common.items.data.DominionWandData;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import fr.frinn.custommachinery.common.init.CustomMachineTile;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(CustommachineryArs.MODID)
public class CustommachineryArs {

  // Define mod id in a common place for everything to reference
  public static final String MODID = "custommachineryars";

  public CustommachineryArs(final IEventBus MOD_BUS) {
    registryInit(MOD_BUS);

    NeoForge.EVENT_BUS.addListener(this::handleWandClick);
  }

  private void registryInit(IEventBus bus) {
    Registration.GUI_ELEMENTS.register(bus);
    Registration.MACHINE_COMPONENTS.register(bus);
    Registration.REQUIREMENTS.register(bus);
    Registration.DATAS.register(bus);
  }

  private void handleWandClick(final PlayerInteractEvent.RightClickBlock event) {
    if (event.getEntity() instanceof ServerPlayer player && !player.isShiftKeyDown()) {
      if (player.getItemInHand(event.getHand()).getItem() instanceof DominionWand wand
        && player.level().getBlockEntity(event.getPos()) instanceof CustomMachineTile tile) {
        if (!tile.getComponentManager().hasComponent(Registration.SOURCE_MACHINE_COMPONENT.get())) return;
        ItemStack stack = player.getItemInHand(event.getHand());
        DominionWandData data = stack.getOrDefault(DataComponentRegistry.DOMINION_WAND.get(), new DominionWandData());
        if (!data.hasStoredData()) {
          data = data.storePos(event.getPos().immutable());
          if (data.strict()) data = data.setFace(event.getFace());
          stack.set(DataComponentRegistry.DOMINION_WAND.get(), data);
          PortUtil.sendMessage(player, Component.translatable("ars_nouveau.dominion_wand.position_set"));
          event.setCancellationResult(InteractionResult.CONSUME);
          event.setCanceled(true);
          return;
        }
        if (data.storedPos().isPresent() && player.getCommandSenderWorld().getBlockEntity(data.storedPos().get()) instanceof IWandable wandable) {
          wandable.onFinishedConnectionFirst(data.storedPos().get(), (LivingEntity) player.level().getEntity(data.storedEntityId()), player);
        }
        if (tile instanceof IWandable wandable) {
          wandable.onFinishedConnectionLast(data.storedPos().get(), (LivingEntity) player.level().getEntity(data.storedEntityId()), player);
          tile.getComponentManager().markDirty();
        }
        if (data.storedEntityId() != -1 && player.level().getEntity(data.storedEntityId()) instanceof IWandable wandable) {
          wandable.onFinishedConnectionFirst(event.getPos(), null, player);
        }
        wand.clear(stack, player);
        event.setCancellationResult(InteractionResult.CONSUME);
      }
    }
  }
}
