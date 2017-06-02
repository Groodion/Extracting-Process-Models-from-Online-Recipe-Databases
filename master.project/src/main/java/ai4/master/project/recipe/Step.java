package ai4.master.project.recipe;

import java.util.ArrayList;
import java.util.List;

import ai4.master.project.recipe.object.CookingAction;
import ai4.master.project.recipe.object.Ingredient;
import ai4.master.project.recipe.object.Tool;

public class Step {
	
	private List<Ingredient> ingredients;
	private List<Tool> tools;
	private CookingAction cookingAction;
	
	private List<Ingredient> products;
	
	private String text;
	
	private CookingEvent event;
	
	
	public Step() {
		ingredients = new ArrayList<Ingredient>();
		products = new ArrayList<Ingredient>();
		tools = new ArrayList<Tool>();
	}
	
	/**
	 * Liste mit im Arbeitsschritt verwendeten Werkzeugen. Sowohl implizite als auch explizite.
	 * @return Werkzeugliste
	 */
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

	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	/**
	 * Liste mit Zutaten die im aktuellen Arbeitsschritt ben�tigt werden. Enth�llt auch implizierte 
	 * und referenzierte Objekte.
	 * @return Zutatenliste
	 */
	public List<Ingredient> getIngredients() {
		return ingredients;
	}

	public CookingEvent getEvent() {
		return event;
	}
	public void setEvent(CookingEvent event) {
		this.event = event;
	}
	
	@Override
	public String toString() {
		return "Step [ingredients=" + ingredients + ", tools=" + tools + ", cookingAction=" + cookingAction + ", products="
				+ products + ", \'" + text + "']";
	}
}