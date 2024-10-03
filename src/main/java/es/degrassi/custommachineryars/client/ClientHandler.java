package es.degrassi.custommachineryars.client;

import com.mojang.blaze3d.systems.RenderSystem;
import es.degrassi.custommachineryars.CustommachineryArs;
import es.degrassi.custommachineryars.Registration;
import es.degrassi.custommachineryars.client.integration.jei.element.SourceGuiElementJeiRenderer;
import es.degrassi.custommachineryars.client.render.element.SourceGuiElementWidget;
import es.degrassi.custommachineryars.client.screen.creation.component.builder.ItemSourceComponentBuilder;
import es.degrassi.custommachineryars.client.screen.creation.component.builder.SourceComponentBuilder;
import es.degrassi.custommachineryars.client.screen.creation.gui.builder.SourceGuiElementBuilder;
import fr.frinn.custommachinery.api.guielement.RegisterGuiElementWidgetSupplierEvent;
import fr.frinn.custommachinery.api.integration.jei.RegisterGuiElementJEIRendererEvent;
import fr.frinn.custommachinery.client.screen.creation.component.RegisterComponentBuilderEvent;
import fr.frinn.custommachinery.client.screen.creation.gui.RegisterGuiElementBuilderEvent;
import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = CustommachineryArs.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientHandler {
  public static void clientInit() {
  }

  @SubscribeEvent
  private static void registerMachineComponentBuilders(final RegisterComponentBuilderEvent event) {
    event.register(Registration.SOURCE_MACHINE_COMPONENT.get(), new SourceComponentBuilder());
    event.register(Registration.ITEM_SOURCE_MACHINE_COMPONENT.get(), new ItemSourceComponentBuilder());
  }
  @SubscribeEvent
  private static void registerGuiElementBuilders(final RegisterGuiElementBuilderEvent event) {
    event.register(Registration.SOURCE_GUI_ELEMENT.get(), new SourceGuiElementBuilder());
  }

  @SubscribeEvent
  private static void registerGuiElementWidgets(RegisterGuiElementWidgetSupplierEvent event) {
    event.register(Registration.SOURCE_GUI_ELEMENT.get(), SourceGuiElementWidget::new);
  }

  @SubscribeEvent
  private static void registerGuiElementJeiRenderers(RegisterGuiElementJEIRendererEvent event) {
    event.register(Registration.SOURCE_GUI_ELEMENT.get(), new SourceGuiElementJeiRenderer());
  }

  public static void renderSlotHighlight(GuiGraphics pose, int x, int y, int width, int height) {
    RenderSystem.disableDepthTest();
    RenderSystem.colorMask(true, true, true, false);
    pose.pose().translate(0, 0, 110);
    pose.fill(x, y, x + width, y + height, -2130706433);
    pose.pose().popPose();
    RenderSystem.colorMask(true, true, true, true);
    RenderSystem.enableDepthTest();
  }
}
