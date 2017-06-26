package ai4.master.project.recipe.object.ingredientTag;

public class IngredientTag {
	
	private String name;
	
	
	public IngredientTag(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}


	@Override
	public boolean equals(Object o){
		IngredientTag i = (IngredientTag) o;
		if(i.getName().equals(this.getName())){
			return true;
		}
		return false;
	}
}
