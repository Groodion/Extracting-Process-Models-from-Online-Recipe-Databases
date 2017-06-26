package ai4.master.project.recipe.object;

import java.util.ArrayList;
import java.util.List;

import ai4.master.project.recipe.baseObject.BaseIngredient;
import ai4.master.project.recipe.object.ingredientTag.IngredientTag;
import ai4.master.project.recipe.object.ingredientTag.QuantifierTag;

public class Ingredient extends NamedObject<BaseIngredient> {

	private List<IngredientTag> tags;

	
	public Ingredient(String name, BaseIngredient baseObject) {
		super(name, baseObject);
		
		tags = new ArrayList<IngredientTag>();
	}

	public List<IngredientTag> getTags() {
		return tags;
	}

	public String getCompleteName() {
		StringBuilder sB = new StringBuilder();
		
		sB.append(getName());
		
		List<IngredientTag> ingredientTags = new ArrayList<IngredientTag>();
		
		for(IngredientTag tag : tags) {
			if(!(tag instanceof QuantifierTag)) {
				ingredientTags.add(tag);
			}
		}
		
		for(int i = 0; i < ingredientTags.size(); i++) {
			if(i != 0) {
				if(i == ingredientTags.size() - 1) {
					sB.append(" und ");
				} else {
					sB.append(", ");
				}
			} else {
				sB.append(' ');
			}
			
			sB.append(ingredientTags.get(i).getName());
		}
		
		return sB.toString();
	}
	
	@Override
	public String toString() {
		return "Ingredient [name=" + getName() + ", tags=" + tags + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((tags == null) ? 0 : tags.hashCode());
		result = prime * result + ((getBaseObject() == null) ? 0 : getBaseObject().hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Ingredient other = (Ingredient) obj;
		if (tags == null) {
			if (other.tags != null)
				return false;
		} else if (!tags.equals(other.tags))
			return false;
		if (getBaseObject() == null) {
			if (other.getBaseObject() != null)
				return false;
		} else if (!getBaseObject().equals(other.getBaseObject()))
			return false;
		return true;
	}	
}