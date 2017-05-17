package ai4.master.project.recipe;

import java.util.ArrayList;
import java.util.List;

public class Step {
	
	private List<Ingredient> ingredients;
	private List<Tool> tools;
	private CookingAction cookingAction;
	
	private Ingredient product;
	
	private String text;
	
	
	public Step() {
		ingredients = new ArrayList<Ingredient>();
		tools = new ArrayList<Tool>();
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
	public Ingredient getProduct() {
		return product;
	}
	public void setProduct(Ingredient product) {
		this.product = product;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public List<Ingredient> getIngredients() {
		return ingredients;
	}

	@Override
	public String toString() {
		return "Step [ingredients=" + ingredients + ", tools=" + tools + ", cookingAction=" + cookingAction + ", product="
				+ product + ", \'" + text + "']";
	}
}