package ai4.master.project.recipe.object.ingredientTag;

import ai4.master.project.recipe.object.Ingredient;

public class QuantifierTag extends IngredientTag {

	public QuantifierTag(String name) {
		super(name);
	}

	@Override
	public QuantifierTag replace(Ingredient ingredient) {
		return new QuantifierTag(getName().replace("INGREDIENT", ingredient.getName()));
	}
}
