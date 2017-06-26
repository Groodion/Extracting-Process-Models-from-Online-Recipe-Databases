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

	/**
	 * Ersetzt die Zeichenkette "INGREDIENT" im Tag-Name mit dem Namen der gegebenen Zutat 
	 * und gibt einen neuen Tag mit diesem Namen zur�ck
	 * @param ingredient
	 * @return
	 */
	public IngredientTag replace(Ingredient ingredient) {
		return new IngredientTag(name.replace("INGREDIENT", ingredient.getName()));
	}
	
	@Override
	public String toString() {
		return name;
	}
}
