package ai4.master.project.recipe;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by René Bärnreuther on 04.05.2017.
 * .
 */
public class Recipe {

	private List<Ingredient> ingredients;
	private List<Step> steps;

	private String preparation;


	public Recipe() {
		ingredients = new ArrayList<Ingredient>();
		steps = new ArrayList<Step>();
		
		preparation = "";
	}

	public String getPreparation() {
		return preparation;
	}
	public void setPreparation(String preparation) {
		this.preparation = preparation;
	}

	public void addIngredient(String ingredient){
		Ingredient ingredient1 = new Ingredient();
		ingredient1.setName(ingredient);
		ingredients.add(ingredient1);
	}
	public List<Ingredient> getIngredients() {
		return ingredients;
	}
	public List<Step> getSteps() {
		return steps;
	}
}