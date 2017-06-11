package ai4.master.project;

import java.net.URL;

import ai4.master.project.apirequests.RecipeGetterChefkoch;
import ai4.master.project.recipe.Recipe;
import ai4.master.project.recipe.Step;
import ai4.master.project.stanfordParser.Parser;

public class ParserTest {
	public static void main(String[] args) throws Exception {
		XMLLoader loader = new XMLLoader();
		KeyWordDatabase kwdb = loader.load(new URL("file:///D:\\Dropbox\\workspace\\Extracting-Process-Models-from-Online-Recipe-Databases\\master.project\\resources\\Lib.xml"));
		
		Parser parser = new Parser("lib/models/german-fast.tagger");
		parser.setKwdb(kwdb);

		//997991205154456,185511079703831,150681066371674,1726761281857676,2397571379105119,982031203667502,914011196708021,1033741208508759,965881202287446,43611014899035,

		Recipe recipe = RecipeGetterChefkoch.recipeGetterFactory().getRecipe("2397571379105119");
		
		parser.parseRecipe(recipe);
		
		for(Step step : recipe.getSteps()) {
			System.out.println(step);
		}
	}
}
