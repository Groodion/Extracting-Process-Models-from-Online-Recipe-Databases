package ai4.master.project.recipe.object.ingredientTag;

import ai4.master.project.recipe.object.Ingredient;

public class IngredientTag {
	
	private String name;
	
	
	public IngredientTag(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	/**
	 * Ersetzt die Zeichenkette "INGREDIENT" im Tag-Name mit dem Namen der gegebenen Zutat 
	 * und gibt einen neuen Tag mit diesem Namen zurück
	 * @param ingredient
	 * @return
	 */
	public IngredientTag replace(Ingredient ingredient) {
		return new IngredientTag(name.replace("INGREDIENT", ingredient.getName()));
	}
	
	@Override
	public String toString() {
		return name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IngredientTag other = (IngredientTag) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}