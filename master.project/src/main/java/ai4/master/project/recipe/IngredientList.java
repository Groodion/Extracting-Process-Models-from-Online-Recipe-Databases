package ai4.master.project.recipe;

import java.util.ArrayList;
import java.util.List;

import ai4.master.project.recipe.baseObject.BaseIngredient;
import ai4.master.project.recipe.baseObject.BaseIngredientGroup;
import ai4.master.project.recipe.object.Ingredient;

public class IngredientList extends ArrayList<Ingredient> {

	private static final long serialVersionUID = 1L;

	public List<Ingredient> get(BaseIngredient baseIngredient) {
		List<Ingredient> ingredients = new ArrayList<Ingredient>();
		
		if(baseIngredient instanceof BaseIngredientGroup) {
			for(Ingredient ingredient : this) {
				if(ingredient.getBaseObject().getIngredientGroups().contains(baseIngredient)) {
					ingredients.add(ingredient);
				}
			}
		} else {
			for(Ingredient ingredient : this) {
				if(ingredient.getBaseObject() == baseIngredient) {
					ingredients.add(ingredient);
				}
			}
		}
		
		return ingredients;
	}
	public void remove(BaseIngredient baseIngredient) {
		ArrayList<Ingredient> ri = new ArrayList<Ingredient>();
		
		for(Ingredient i : this) {
			if(i.getBaseObject().equals(baseIngredient)) {
				ri.add(i);
			}
		}
		
		removeAll(ri);
	}
	public boolean contains(BaseIngredient bi) {
		return !get(bi).isEmpty();
	}
}
