package ai4.master.project.recipe;

import ai4.master.project.recipe.baseObject.BaseIngredient;
import ai4.master.project.recipe.baseObject.BaseIngredientGroup;
import ai4.master.project.recipe.object.Ingredient;

import java.util.ArrayList;
import java.util.List;

//TODO remove überprüfen, da nicht alle bzw falsche objeckte beim parsen entfernt werden 

public class IngredientList extends ArrayList<Ingredient> {

	private static final long serialVersionUID = 1L;

	public List<Ingredient> get(BaseIngredient baseIngredient) {
		List<Ingredient> ingredients = new ArrayList<Ingredient>();

		Ingredient best = null;
		for (Ingredient ingredient : this) {
			if (ingredient.getBaseObject() == baseIngredient) {
				if (best == null || best.getTags().size() < ingredient.getTags().size()) {
					best = ingredient;
				}
			}
		}
		if (best != null) {
			ingredients.add(best);
		}
		
		if (ingredients.size() == 0 && baseIngredient instanceof BaseIngredientGroup) {
			for (Ingredient ingredient : this) {
				if (ingredient.getBaseObject().getIngredientGroups().contains(baseIngredient)) {
					ingredients.add(ingredient);
				}
			}
		}
		
		return ingredients;
	}

	public void remove(BaseIngredient baseIngredient) {
		ArrayList<Ingredient> ri = new ArrayList<Ingredient>();

		for (Ingredient i : this) {
			if (i.getBaseObject().equals(baseIngredient)) {
				ri.add(i);
			}
		}
		removeAll(ri);
	}

	public boolean contains(BaseIngredient bi) {
		return !get(bi).isEmpty();
	}
}