package ai4.master.project.recipe;

import java.util.List;

public class CookingAction extends NamedObject {
	private String result;
	
	public CookingAction() {
		result = null;
	}
	
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}

	public Ingredient transform(Ingredient mainIngredient, List<Ingredient> ingredients) {
		if(result == null) {
			return mainIngredient;
		}
		return null;
	}
}
