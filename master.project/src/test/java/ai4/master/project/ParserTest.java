package ai4.master.project;

import ai4.master.project.apirequests.RecipeGetterChefkoch;
import ai4.master.project.recipe.Recipe;
import ai4.master.project.recipe.Step;
import ai4.master.project.stanfordParser.Parser;

import java.net.URL;

public class ParserTest {
	public static void main(String[] args) throws Exception {
		XMLLoader loader = new XMLLoader();
		KeyWordDatabase kwdb = loader.load(new URL("file","","resources/Lib.xml"));
		Parser parser = new Parser("lib/models/german-fast.tagger");
		parser.setKwdb(kwdb);
		//1340271238839144
		//997991205154456,185511079703831,150681066371674,1726761281857676,2397571379105119,982031203667502,914011196708021,1033741208508759,965881202287446,43611014899035,

		Recipe recipe = new RecipeGetterChefkoch().getRecipe("1726761281857676");
		
		parser.parseRecipe(recipe);
		
		for(Step step : recipe.getSteps()) {
			System.out.println(step);
			System.out.println(step.getEvents());
		}

		//ProcessModeler processModeler = new ProcessModelerImpl();
		//processModeler.convertToProcess(recipe);
	}
}
