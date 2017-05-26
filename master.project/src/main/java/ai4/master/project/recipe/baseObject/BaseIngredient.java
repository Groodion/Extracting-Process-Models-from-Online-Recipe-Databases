package ai4.master.project.recipe.baseObject;

import ai4.master.project.recipe.object.Ingredient;

public class BaseIngredient extends BaseNamedObject<Ingredient, BaseIngredient> {

	@Override
	public Ingredient toObject() {
		String name = "UNNAMED";
		
		if(getNames().size() != 0) {
			name = getNames().iterator().next();
		}
		return new Ingredient(name, this);
	}

}
