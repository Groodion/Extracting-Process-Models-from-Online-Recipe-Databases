package ai4.master.project.recipe;

import java.util.ArrayList;
import java.util.List;

public class Step {
	
	private List<Ingredient> ingredients;
	private List<Tool> tools;
	private CookingAction cookingAction;
	
	private List<Ingredient> products;
	
	private String completePrep;
	
	
	public Step() {
		ingredients = new ArrayList<Ingredient>();
	}
	
	public List<Tool> getTools() {
		return tools;
	}
	public CookingAction getCookingAction() {
		return cookingAction;
	}
	public void setCookingAction(CookingAction cookingAction) {
		this.cookingAction = cookingAction;
	}
	public List<Ingredient> getProducts() {
		return products;
	}
	public String getCompletePrep() {
		return completePrep;
	}
	public void setCompletePrep(String completePrep) {
		this.completePrep = completePrep;
	}
	public List<Ingredient> getIngredients() {
		return ingredients;
	}
	
}