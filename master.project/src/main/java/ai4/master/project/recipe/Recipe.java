package ai4.master.project.recipe;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by René Bärnreuther on 04.05.2017. TODO Use me and implement me, of
 * course.
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

	public List<Ingredient> getIngredients() {
		return ingredients;
	}
	public List<Step> getSteps() {
		return steps;
	}
}