package ai4.master.project.recipe.object;

import java.util.ArrayList;
import java.util.List;

import ai4.master.project.recipe.baseObject.BaseIngredient;
import ai4.master.project.recipe.object.ingredientTag.IngredientTag;

public class Ingredient extends NamedObject<BaseIngredient> {

	private List<IngredientTag> tags;

	
	public Ingredient(String name, BaseIngredient baseObject) {
		super(name, baseObject);
		
		tags = new ArrayList<IngredientTag>();
	}

	public List<IngredientTag> getTags() {
		return tags;
	}

	public Ingredient tag(IngredientTag tag) {
		Ingredient taggedIngredient = new Ingredient(getName(), getBaseObject());
		
		taggedIngredient.getTags().addAll(tags);
		taggedIngredient.getTags().add(tag);
		
		return taggedIngredient;
	}
	
	@Override
	public String toString() {
		return "Ingredient [name=" + getName() + ", tags=" + tags + "]";
	}	
}