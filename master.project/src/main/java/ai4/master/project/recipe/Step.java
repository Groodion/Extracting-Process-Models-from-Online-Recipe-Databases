package ai4.master.project.recipe;

import java.util.ArrayList;
import java.util.List;

public class Step {
	
	private List<Ingredient> ingredients;
	private Tool tool;
	private CookingAction cookingAction;
	
	private Ingredient product;
	
	private String text;
	
	
	public Step() {
		ingredients = new ArrayList<Ingredient>();
	}
	
	public Tool getTool() {
		return tool;
	}
	public void setTool(Tool tool) {
		this.tool = tool;
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
	
}