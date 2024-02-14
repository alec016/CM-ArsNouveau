package al.alec.custommachineryars.client.integration.jei.source;

import al.alec.custommachineryars.client.integration.jei.CustomIngredientTypes;
import al.alec.custommachineryars.client.render.element.SourceGuiElementWidget;
import al.alec.custommachineryars.guielement.SourceGuiElement;
import com.mojang.blaze3d.vertex.PoseStack;
import fr.frinn.custommachinery.api.integration.jei.JEIIngredientRenderer;
import fr.frinn.custommachinery.common.util.Utils;
import java.util.ArrayList;
import java.util.List;
import mezz.jei.api.ingredients.IIngredientType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

public class SourceJEIIngredientRenderer extends JEIIngredientRenderer<Source, SourceGuiElement> {

  public SourceJEIIngredientRenderer(SourceGuiElement element) {
    super(element);
  }

  @Override
  public IIngredientType<Source> getType() {
    return CustomIngredientTypes.SOURCE;
  }

  @Override
  public int getWidth() {
    return this.element.getWidth() - 4;
  }

  @Override
  public int getHeight() {
    return this.element.getHeight() - 4;
  }

  @Override
  public void render(@NotNull PoseStack stack, @NotNull Source ingredient) {
    int width = this.element.getWidth() - 4;
    int height = this.element.getHeight() - 4;

    SourceGuiElementWidget.renderSource(stack, height, 0, 0, width, height);
  }

  @Override
  public @NotNull List<Component> getTooltip(Source ingredient, @NotNull TooltipFlag tooltipFlag) {
    List<Component> tooltips = new ArrayList<>();
    String amount = Utils.format(ingredient.getAmount());
    if(ingredient.isPerTick())
      tooltips.add(Component.translatable("custommachineryars.jei.ingredient.source.pertick", amount));
    else
      tooltips.add(Component.translatable("custommachineryars.jei.ingredient.source", amount));
    return tooltips;
  }
}
