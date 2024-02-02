package al.alec.custommachineryars.client.integration.jei.element;

import al.alec.custommachineryars.client.ClientHandler;
import al.alec.custommachineryars.guielement.SourceGuiElement;
import com.mojang.blaze3d.vertex.PoseStack;
import fr.frinn.custommachinery.api.crafting.IMachineRecipe;
import fr.frinn.custommachinery.api.integration.jei.IJEIElementRenderer;
import net.minecraft.client.gui.GuiComponent;

public class SourceGuiElementJeiRenderer implements IJEIElementRenderer<SourceGuiElement>  {
  @Override
  public void renderElementInJEI(PoseStack matrix, SourceGuiElement element, IMachineRecipe recipe, int mouseX, int mouseY) {
    int posX = element.getX();
    int posY = element.getY();
    int width = element.getWidth();
    int height = element.getHeight();

    ClientHandler.bindTexture(element.getEmptyTexture());
    GuiComponent.blit(matrix, posX, posY, 0, 0, width, height, width, height);
  }
}
