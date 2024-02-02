package al.alec.custommachineryars.client.integration.jei;

import al.alec.custommachineryars.client.integration.jei.source.Source;
import mezz.jei.api.ingredients.IIngredientType;

public class CustomIngredientTypes {
  public static final IIngredientType<Source> SOURCE = () -> Source.class;
}
