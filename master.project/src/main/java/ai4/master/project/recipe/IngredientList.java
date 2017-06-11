package ai4.master.project.recipe;

import java.util.ArrayList;

import ai4.master.project.recipe.baseObject.BaseIngredient;
import ai4.master.project.recipe.object.Ingredient;
import ai4.master.project.recipe.object.IngredientGroup;

public class IngredientList extends ArrayList<Ingredient> {

	private static final long serialVersionUID = 1L;

	
	public void group(IngredientGroup group) {
		for(Ingredient ingredient : this) {
			if(ingredient.getBaseObject().getIngredientGroups().contains(group.getBaseObject())) {
				group.getIngredients().add(ingredient);
			}
		}
		
		removeAll(group.getIngredients());
		add(group);
	}
	public void split(IngredientGroup group) {
		addAll(group.getIngredients());
		
		remove(group);
	}
	
	public Ingredient get(BaseIngredient baseIngredient) {
		for(Ingredient ingredient : this) {
			if(ingredient.getBaseObject() == baseIngredient) {
				return ingredient;
			}
		}
		
		return null;
	}
}
