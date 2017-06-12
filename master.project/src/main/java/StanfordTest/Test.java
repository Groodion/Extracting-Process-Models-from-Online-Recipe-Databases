package StanfordTest;

import ai4.master.project.recipe.Recipe;
import ai4.master.project.recipe.Step;
import ai4.master.project.recipe.TestRecipeFactory;
import ai4.master.project.recipe.object.Ingredient;

public class Test {
	public static void main(String args[]) {
		Recipe recipe = TestRecipeFactory.getInstance().createRecipe();
		
		for(Step step : recipe.getSteps()) {
			System.out.println(step.toString());
			for(Ingredient product : step.getProducts()) {
				System.out.println(product.getCompleteName());
			}
		}
	}
}
