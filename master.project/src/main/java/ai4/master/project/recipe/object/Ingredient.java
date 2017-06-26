package ai4.master.project.recipe.object;

import ai4.master.project.recipe.baseObject.BaseIngredient;
import ai4.master.project.recipe.object.ingredientTag.IngredientTag;

import java.util.ArrayList;
import java.util.List;

public class Ingredient extends NamedObject<BaseIngredient> {

	private List<IngredientTag> tags;

	
	public Ingredient(String name, BaseIngredient baseObject) {
		super(name, baseObject);
		tags = new ArrayList<IngredientTag>();
	}

	public List<IngredientTag> getTags() {
		return tags;
	}

	@Override
	public String toString() {
		return "Ingredient [name=" + getName() + ", tags=" + tags + "]";
	}


	public String getIngredientName(){
	    return this.getName();
    }

    @Override
	public boolean equals(Object o){
		boolean isEqual = true;
		Ingredient i = (Ingredient)o;
		if(!this.getIngredientName().equals(i.getIngredientName())){
			isEqual = false;
		}

		for(IngredientTag ingredientTag : tags){
			for(IngredientTag iTag : i.getTags()){
				if(!ingredientTag.equals(iTag)){
					isEqual = false;
				}
			}
		}
		return isEqual;
	}
}