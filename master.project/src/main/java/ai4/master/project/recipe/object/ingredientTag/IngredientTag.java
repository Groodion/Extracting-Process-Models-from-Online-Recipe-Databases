package ai4.master.project.recipe.object.ingredientTag;

import ai4.master.project.recipe.object.Ingredient;

public class IngredientTag {
	
	private String name;
	
	
	public IngredientTag(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public IngredientTag replace(Ingredient ingredient) {
		return new IngredientTag(name.replace("INGREDIENT", ingredient.getName()));
	}
	
	@Override
	public String toString() {
		return name;
	}
}
