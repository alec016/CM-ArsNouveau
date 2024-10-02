package es.degrassi.custommachineryars.client.integration.jei.element;

import es.degrassi.custommachineryars.guielement.SourceGuiElement;
import fr.frinn.custommachinery.api.crafting.IMachineRecipe;
import fr.frinn.custommachinery.api.integration.jei.IJEIElementRenderer;
import net.minecraft.client.gui.GuiGraphics;

public class SourceGuiElementJeiRenderer implements IJEIElementRenderer<SourceGuiElement>  {
  @Override
  public void renderElementInJEI(GuiGraphics matrix, SourceGuiElement element, IMachineRecipe recipe, int mouseX, int mouseY) {
    int posX = element.getX();
    int posY = element.getY();
    int width = element.getWidth();
    int height = element.getHeight();

    matrix.blit(element.getEmptyTexture(), posX, posY, 0, 0, width, height, width, height);
  }
}
