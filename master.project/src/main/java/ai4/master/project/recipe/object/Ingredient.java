package ai4.master.project.recipe.object;

import java.util.List;

import ai4.master.project.recipe.baseObject.BaseIngredient;
import ai4.master.project.recipe.object.ingredientTag.IngredientTag;

public class Ingredient extends NamedObject<BaseIngredient> {

	private List<IngredientTag> tags;

	
	public Ingredient(String name, BaseIngredient baseObject) {
		super(name, baseObject);
	}

	public List<IngredientTag> getTags() {
		return tags;
	}

	@Override
	public String toString() {
		return "Ingredient [name=" + getName() + ", tags=" + tags + "]";
	}	
}