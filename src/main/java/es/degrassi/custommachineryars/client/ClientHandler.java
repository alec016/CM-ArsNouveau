package es.degrassi.custommachineryars.client;

import es.degrassi.custommachineryars.Registration;
import es.degrassi.custommachineryars.client.integration.jei.element.SourceGuiElementJeiRenderer;
import es.degrassi.custommachineryars.client.render.element.SourceGuiElementWidget;
import com.mojang.blaze3d.systems.RenderSystem;
import es.degrassi.custommachineryars.client.screen.creation.component.builder.SourceComponentBuilder;
import es.degrassi.custommachineryars.client.screen.creation.gui.builder.SourceGuiElementBuilder;
import fr.frinn.custommachinery.api.guielement.RegisterGuiElementWidgetSupplierEvent;
import fr.frinn.custommachinery.api.integration.jei.RegisterGuiElementJEIRendererEvent;
import fr.frinn.custommachinery.client.screen.creation.component.RegisterComponentBuilderEvent;
import fr.frinn.custommachinery.client.screen.creation.gui.RegisterGuiElementBuilderEvent;
import net.minecraft.client.gui.GuiGraphics;

public class ClientHandler {
  public static void clientInit() {
    RegisterGuiElementWidgetSupplierEvent.EVENT.register(ClientHandler::registerGuiElementWidgets);
    RegisterComponentBuilderEvent.EVENT.register(ClientHandler::registerMachineComponentBuilders);
    RegisterGuiElementBuilderEvent.EVENT.register(ClientHandler::registerGuiElementBuilders);
    RegisterGuiElementJEIRendererEvent.EVENT.register(ClientHandler::registerGuiElementJeiRenderers);
  }

  private static void registerMachineComponentBuilders(final RegisterComponentBuilderEvent event) {
    event.register(Registration.SOURCE_MACHINE_COMPONENT.get(), new SourceComponentBuilder());
  }
  private static void registerGuiElementBuilders(final RegisterGuiElementBuilderEvent event) {
    event.register(Registration.SOURCE_GUI_ELEMENT.get(), new SourceGuiElementBuilder());
  }

  private static void registerGuiElementWidgets(RegisterGuiElementWidgetSupplierEvent event) {
    event.register(Registration.SOURCE_GUI_ELEMENT.get(), SourceGuiElementWidget::new);
  }

  private static void registerGuiElementJeiRenderers(RegisterGuiElementJEIRendererEvent event) {
    event.register(Registration.SOURCE_GUI_ELEMENT.get(), new SourceGuiElementJeiRenderer());
  }

  public static void renderSlotHighlight(GuiGraphics pose, int x, int y, int width, int height) {
    RenderSystem.disableDepthTest();
    RenderSystem.colorMask(true, true, true, false);
    pose.fill(x, y, x + width, y + height, -2130706433);
    RenderSystem.colorMask(true, true, true, true);
    RenderSystem.enableDepthTest();
  }
}
