package al.alec.custommachineryars.client.render.element;

import al.alec.custommachineryars.CustomMachineryArsNouveau;
import al.alec.custommachineryars.Registration;
import al.alec.custommachineryars.client.ClientHandler;
import al.alec.custommachineryars.guielement.SourceGuiElement;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;
import fr.frinn.custommachinery.api.guielement.IMachineScreen;
import fr.frinn.custommachinery.common.util.Utils;
import fr.frinn.custommachinery.impl.guielement.TexturedGuiElementWidget;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.InventoryMenu;

public class SourceGuiElementWidget extends TexturedGuiElementWidget<SourceGuiElement> {
  private static final int TEXTURE_SIZE = 16;
  public SourceGuiElementWidget(SourceGuiElement element, IMachineScreen screen) {
    super(element, screen, Component.literal("Mana"));
  }

  public static void renderSource(PoseStack poseStack, int sourceHeight, int x, int y, int width, int height) {
    RenderSystem.enableBlend();

    poseStack.pushPose();
    poseStack.translate(x, y, 0);

    TextureAtlasSprite sprite = CustomMachineryArsNouveau.source.sprite();

    drawTiledSprite(poseStack, width, height, sourceHeight, sprite);

    poseStack.popPose();

    RenderSystem.disableBlend();
  }

  @Override
  public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
    super.renderButton(poseStack, mouseX, mouseY, partialTicks);
    this.getScreen().getTile().getComponentManager().getComponent(Registration.SOURCE_MACHINE_COMPONENT.get()).ifPresent(source -> {
      double fillPercent = source.getFillPercent();
      int manaHeight = (int)(fillPercent * (double)(this.height - 4));

      renderSource(poseStack, manaHeight, x + 2, y + 2, width - 4, height - 4);
    });
    if(this.isHoveredOrFocused() && this.getElement().highlight())
      ClientHandler.renderSlotHighlight(poseStack, this.x + 2, this.y + 2, this.width - 4, this.height - 4);
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

  private static void drawTiledSprite(PoseStack poseStack, final int tiledWidth, final int tiledHeight, int scaledAmount, TextureAtlasSprite sprite) {
    RenderSystem.setShader(GameRenderer::getPositionTexShader);
    RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
    Matrix4f matrix = poseStack.last().pose();

    float r = ((0xFFFFFF >> 16) & 0xFF) / 255.0F;
    float g = ((0xFFFFFF >> 8) & 0xFF) / 255.0F;
    float b = ((0xFFFFFF) & 0xFF) / 255.0F;

    RenderSystem.setShaderColor(r, g, b, 1F);

    final int xTileCount = tiledWidth / TEXTURE_SIZE;
    final int xRemainder = tiledWidth - (xTileCount * TEXTURE_SIZE);
    final int yTileCount = scaledAmount / TEXTURE_SIZE;
    final int yRemainder = scaledAmount - (yTileCount * TEXTURE_SIZE);

    for (int xTile = 0; xTile <= xTileCount; xTile++) {
      for (int yTile = 0; yTile <= yTileCount; yTile++) {
        int width = (xTile == xTileCount) ? xRemainder : TEXTURE_SIZE;
        int height = (yTile == yTileCount) ? yRemainder : TEXTURE_SIZE;
        int x = (xTile * TEXTURE_SIZE);
        int y = tiledHeight - ((yTile + 1) * TEXTURE_SIZE);
        if (width > 0 && height > 0) {
          int maskTop = TEXTURE_SIZE - height;
          int maskRight = TEXTURE_SIZE - width;

          drawTextureWithMasking(matrix, x, y, sprite, maskTop, maskRight);
        }
      }
    }

    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
  }

  private static void drawTextureWithMasking(Matrix4f matrix, float xCoord, float yCoord, TextureAtlasSprite textureSprite, int maskTop, int maskRight) {
    float uMin = textureSprite.getU0();
    float uMax = textureSprite.getU1();
    float vMin = textureSprite.getV0();
    float vMax = textureSprite.getV1();
    uMax = uMax - (maskRight / 16F * (uMax - uMin));
    vMax = vMax - (maskTop / 16F * (vMax - vMin));

    float zLevel = 100;

    Tesselator tesselator = Tesselator.getInstance();
    BufferBuilder bufferBuilder = tesselator.getBuilder();
    bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
    bufferBuilder.vertex(matrix, xCoord, yCoord + 16, zLevel).uv(uMin, vMax).endVertex();
    bufferBuilder.vertex(matrix, xCoord + 16 - maskRight, yCoord + 16, zLevel).uv(uMax, vMax).endVertex();
    bufferBuilder.vertex(matrix, xCoord + 16 - maskRight, yCoord + maskTop, zLevel).uv(uMax, vMin).endVertex();
    bufferBuilder.vertex(matrix, xCoord, yCoord + maskTop,zLevel).uv(uMin, vMin).endVertex();
    tesselator.end();
  }
}
