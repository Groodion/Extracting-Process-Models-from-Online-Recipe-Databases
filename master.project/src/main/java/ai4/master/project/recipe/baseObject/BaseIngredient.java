package ai4.master.project.recipe.baseObject;

import java.util.List;
import java.util.ArrayList;

import ai4.master.project.recipe.object.Ingredient;


public class BaseIngredient extends BaseNamedObject<Ingredient, BaseIngredient> {

	private List<BaseIngredientGroup> groups;
	
	
	public BaseIngredient() {
		groups = new ArrayList<BaseIngredientGroup>();
	}
	
	public List<BaseIngredientGroup> getIngredientGroups() {
		return groups;
	}
	
	@Override
	public Ingredient toObject() {
		String name = "UNNAMED";
		
		if(getNames().size() != 0) {
			name = getNames().iterator().next();
		}
		return new Ingredient(name, this);
	}
}