package ai4.master.project;

import ai4.master.project.apirequests.RecipeGetterChefkoch;
import ai4.master.project.process.ProcessModeler;
import ai4.master.project.process.ProcessModelerImpl;
import ai4.master.project.recipe.Recipe;
import ai4.master.project.stanfordParser.Parser;

import java.net.URL;

public class ParserTest {
/*	public static void main(String[] args) throws Exception {
		XMLLoader loader = new XMLLoader();
		KeyWordDatabase kwdb = loader.load(new URL("file","","resources/Lib.xml"));
		Parser parser = new Parser("lib/models/german-fast.tagger");
		parser.setKwdb(kwdb);
		//1340271238839144
		//997991205154456,185511079703831,150681066371674,1726761281857676,2397571379105119,982031203667502,914011196708021,1033741208508759,965881202287446,43611014899035,

		Recipe recipe = new RecipeGetterChefkoch().getRecipe("43611014899035");
		
		parser.parseRecipe(recipe);
		
		for(Step step : recipe.getSteps()) {
			System.out.println(step.toEasyToReadString());
		}

		ProcessModeler processModeler = new ProcessModelerImpl();
		processModeler.convertToProcess(recipe);
	}*/

    static String[] ids = {"1340271238839144", "997991205154456", "185511079703831", "150681066371674", "1726761281857676", "2397571379105119", "982031203667502", "914011196708021",
            "1033741208508759", "965881202287446", "43611014899035"};


    public static void main(String[] args) throws Exception {

        XMLLoader loader = new XMLLoader();
        KeyWordDatabase kwdb = loader.load(new URL("file", "", "resources/Lib.xml"));
        Parser parser = new Parser("lib/models/german-fast.tagger");
        parser.setKwdb(kwdb);
        for (int i = 0; i < ids.length; i++) {
            String currentRecipe = ids[i];
            System.out.println("PARSING RECIPE WITH ID " + currentRecipe);
            try {
                Recipe recipe = new RecipeGetterChefkoch().getRecipe(currentRecipe);
                parser.parseRecipe(recipe);
                //Now steps are saved in recipe
                ProcessModeler processModeler = new ProcessModelerImpl();
                processModeler.setFileName(currentRecipe + "toBpmn");
                processModeler.convertToProcess(recipe);
            } catch (Exception ex) {
                System.err.println("Could not parse Recipe with id: " + currentRecipe);
                System.err.println("Following error occured: ");
                ex.printStackTrace();
            }
            System.out.println("FINISHED PARSING RECIPE WITH ID " + currentRecipe);
            System.out.println("#################################################");
        }
       /* XMLLoader loader = new XMLLoader();
        KeyWordDatabase kwdb = loader.load(new URL("file", "", "resources/Lib.xml"));
        Parser parser = new Parser("lib/models/german-fast.tagger");
        parser.setKwdb(kwdb);
        //1340271238839144
        //997991205154456,185511079703831,150681066371674,1726761281857676,2397571379105119,982031203667502,914011196708021,1033741208508759,965881202287446,43611014899035,

        Recipe recipe = new RecipeGetterChefkoch().getRecipe("997991205154456");


        parser.parseRecipe(recipe);

        for (Step step : recipe.getSteps()) {
            System.out.println(step);
            System.out.println(step.getEvents());
        }

        ProcessModeler processModeler = new ProcessModelerImpl();
        processModeler.setFileName("997991205154456");
        processModeler.convertToProcess(recipe);


        Parser parser1 = new Parser("lib/models/german-fast.tagger");
        parser1.setKwdb(kwdb);

        Recipe recipe1 = new RecipeGetterChefkoch().getRecipe("1340271238839144");
        parser1.parseRecipe(recipe1);

        ProcessModeler processModeler1 = new ProcessModelerImpl();
        processModeler.setFileName("1340271238839144");
        processModeler1.convertToProcess(recipe1);
*/

    }
}
