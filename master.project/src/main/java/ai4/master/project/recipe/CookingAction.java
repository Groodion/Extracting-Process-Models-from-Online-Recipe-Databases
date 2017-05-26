package ai4.master.project.recipe;

import java.util.List;

public class CookingAction extends NamedObject {
	private String result;
	
	private ResultType resultFinder;
	
	
	public CookingAction() {
		result = null;
	}
	
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}

	public ResultType getResultFinder() {
		return resultFinder;
	}
	public void setResultFinder(ResultType resultFinder) {
		this.resultFinder = resultFinder;
	}

	public Ingredient transform(Ingredient mainIngredient, List<Ingredient> ingredients) {
		if(result == null) {
			return mainIngredient;
		}
		return null;
	}

}