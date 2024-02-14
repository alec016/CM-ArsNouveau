package al.alec.custommachineryars.client.integration.jei.wrapper;

import al.alec.custommachineryars.Registration;
import al.alec.custommachineryars.client.integration.jei.CustomIngredientTypes;
import al.alec.custommachineryars.client.integration.jei.source.Source;
import al.alec.custommachineryars.client.integration.jei.source.SourceJEIIngredientRenderer;
import al.alec.custommachineryars.guielement.SourceGuiElement;
import fr.frinn.custommachinery.api.guielement.IGuiElement;
import fr.frinn.custommachinery.api.integration.jei.IJEIIngredientWrapper;
import fr.frinn.custommachinery.api.integration.jei.IRecipeHelper;
import fr.frinn.custommachinery.api.requirement.RequirementIOMode;
import fr.frinn.custommachinery.common.util.Utils;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import net.minecraft.network.chat.Component;

public class SourceIngredientWrapper implements IJEIIngredientWrapper<Source> {

  private final RequirementIOMode mode;
  private final int recipeTime;
  private final Source source;

  public SourceIngredientWrapper(RequirementIOMode mode, int amount, boolean isPerTick, int recipeTime) {
    this.mode = mode;
    this.recipeTime = recipeTime;
    this.source = new Source(amount, isPerTick);
  }
  @Override
  public boolean setupRecipe(IRecipeLayoutBuilder builder, int xOffset, int yOffset, IGuiElement element, IRecipeHelper helper) {
    if(!(element instanceof SourceGuiElement manaElement) || element.getType() != Registration.SOURCE_GUI_ELEMENT.get())
      return false;

    builder.addSlot(roleFromMode(this.mode), element.getX() - xOffset + 2, element.getY() - yOffset + 2)
      .setCustomRenderer(CustomIngredientTypes.SOURCE, new SourceJEIIngredientRenderer(manaElement))
      .addIngredient(CustomIngredientTypes.SOURCE, this.source)
      .addTooltipCallback((recipeSlotView, tooltip) -> {
        Component component;
        String amount = Utils.format(this.source.getAmount());
        if(this.source.isPerTick()) {
          String totalMana = Utils.format(this.source.getAmount() * this.recipeTime);
          if(this.mode == RequirementIOMode.INPUT)
            component = Component.translatable("custommachineryars.jei.ingredient.source.pertick.input", totalMana, amount);
          else
            component = Component.translatable("custommachineryars.jei.ingredient.source.pertick.output", totalMana, amount);
        } else {
          if(this.mode == RequirementIOMode.INPUT)
            component = Component.translatable("custommachineryars.jei.ingredient.source.input", amount);
          else
            component = Component.translatable("custommachineryars.jei.ingredient.source.output", amount);
        }
        tooltip.set(0, component);
      });
    return true;
  }
}
