package al.alec.custommachineryars.client.render.element;

import al.alec.custommachineryars.Registration;
import al.alec.custommachineryars.client.ClientHandler;
import al.alec.custommachineryars.guielement.SourceGuiElement;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import fr.frinn.custommachinery.api.guielement.IMachineScreen;
import fr.frinn.custommachinery.common.util.Utils;
import fr.frinn.custommachinery.impl.guielement.TexturedGuiElementWidget;
import java.util.Collections;
import java.util.List;
import net.minecraft.network.chat.Component;

public class SourceGuiElementWidget extends TexturedGuiElementWidget<SourceGuiElement> {
  private static final int TEXTURE_SIZE = 16;

  public SourceGuiElementWidget(SourceGuiElement element, IMachineScreen screen) {
    super(element, screen, Component.literal("Mana"));
  }



  public static void renderSource(PoseStack poseStack, int manaHeight, int x, int y, int width, int height) {
    RenderSystem.enableBlend();

    poseStack.pushPose();
    poseStack.translate(x, y, 0);

//    TextureAtlasSprite sprite = MiscellaneousModels.INSTANCE.manaWater.sprite();
//
//    drawTiledSprite(poseStack, width, height, manaHeight, sprite);

    poseStack.popPose();

    RenderSystem.disableBlend();
  }

  @Override
  public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
    super.renderButton(poseStack, mouseX, mouseY, partialTicks);
    this.getScreen().getTile().getComponentManager().getComponent(Registration.SOURCE_MACHINE_COMPONENT.get()).ifPresent(mana -> {
      double fillPercent = mana.getFillPercent();
      int manaHeight = (int)(fillPercent * (double)(this.height - 2));

      renderSource(poseStack, manaHeight, x + 2, y, width - 4, height - 2);
    });
    if(this.isHoveredOrFocused() && this.getElement().highlight())
      ClientHandler.renderSlotHighlight(poseStack, this.x + 2, this.y, this.width - 4, this.height - 2);
  }

  @Override
  public List<Component> getTooltips() {
    if(!this.getElement().getTooltips().isEmpty())
      return this.getElement().getTooltips();
    return this.getScreen().getTile().getComponentManager()
      .getComponent(Registration.SOURCE_MACHINE_COMPONENT.get())
      .map(component -> Collections.singletonList((Component)
        Component.translatable(
          "custommachineryars.gui.element.source.tooltip",
          Utils.format(component.getSource()),
          Utils.format(component.getCapacity())
        )
      ))
      .orElse(Collections.emptyList());
  }
}
