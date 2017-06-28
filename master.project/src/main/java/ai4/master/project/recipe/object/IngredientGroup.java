package ai4.master.project.recipe.object;

import ai4.master.project.recipe.baseObject.BaseIngredientGroup;

import java.util.ArrayList;
import java.util.List;


public class IngredientGroup extends Ingredient {

	private List<Ingredient> ingredients;

	
	public IngredientGroup(String name, BaseIngredientGroup baseObject) {
		super(name, baseObject);
		
		ingredients = new ArrayList<Ingredient>();
	}
	
	public List<Ingredient> getIngredients() {
		return ingredients;
	}

	@Override
	public String toString() {
		return "IngredientGroup [name=" + getName() + ", ingredients=" + ingredients + "]";
	}
}