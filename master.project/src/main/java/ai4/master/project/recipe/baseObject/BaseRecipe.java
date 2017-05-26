package ai4.master.project.recipe.baseObject;

import java.util.ArrayList;
import java.util.List;

import ai4.master.project.recipe.Step;

/**
 * Created by René Bärnreuther on 04.05.2017.
 * .
 */
public class BaseRecipe {

	private List<BaseIngredient> ingredients;
	private List<Step> steps;

	private String preparation;


	public BaseRecipe() {
		ingredients = new ArrayList<BaseIngredient>();
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
		BaseIngredient ingredient1 = new BaseIngredient();
		ingredient1.setName(ingredient);
		ingredients.add(ingredient1);
	}
	public List<BaseIngredient> getIngredients() {
		return ingredients;
	}
	public List<Step> getSteps() {
		return steps;
	}
}