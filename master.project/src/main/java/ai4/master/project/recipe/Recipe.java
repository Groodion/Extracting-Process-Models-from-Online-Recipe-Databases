package ai4.master.project.recipe;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ren� B�rnreuther on 04.05.2017.
 * .
 */
public class Recipe {

	private List<String> ingredients;
	private List<Step> steps;

	private String preparation;

	private LANG_FLAG language = LANG_FLAG.DE;
	

	public Recipe(LANG_FLAG language) {
		ingredients = new ArrayList<String>();
		steps = new ArrayList<Step>();
		this.language = language;
		preparation = "";
	}

	public String getPreparation() {
		return preparation;
	}
	public void setPreparation(String preparation) {
		this.preparation = preparation;
	}

	public List<String> getIngredients() {
		return ingredients;
	}
	public List<Step> getSteps() {
		return steps;
	}

	public LANG_FLAG getLanguage(){
		return language;
	}

	public void setLanguage(LANG_FLAG language){
		this.language = language;
	}
}