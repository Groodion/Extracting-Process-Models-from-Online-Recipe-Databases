package ai4.master.project;

import ai4.master.project.apirequests.RecipeGetterChefkoch;
import ai4.master.project.process.ProcessModeler;
import ai4.master.project.process.ProcessModelerImpl;
import ai4.master.project.recipe.Recipe;
import ai4.master.project.recipe.Step;
import ai4.master.project.recipe.TestRecipeFactory;
import ai4.master.project.stanfordParser.Parser;
import ai4.master.project.viewFx.Controller;

import java.net.URL;

public class ParserTest {
//funktionieren: 997991205154456, 43611014899035, 150681066371674, 982031203667502

/*    static String[] ids = {"1340271238839144", "997991205154456", "185511079703831", "150681066371674", "1726761281857676", "2397571379105119", "982031203667502", "914011196708021",
            "1033741208508759", "965881202287446", "43611014899035"};
*/
	//15XX not working with tool compare
	static String[] ids = {
		"997991205154456"
	};
    public static void main(String[] args) throws Exception {
//    	testParser();
   	testProcessModeler();
//    	testSimpleProcessExample();
    }


    public static void testSimpleProcessExample(){
            Recipe r = new TestRecipeFactory().create();
            ProcessModeler processModeler = new ProcessModelerImpl();
            processModeler.setFileName("test-layout");
        //    processModeler.convertToProcess(r);
    }
	public static void testParser() throws Exception {
		XMLLoader loader = new XMLLoader();
		KeyWordDatabase kwdb = loader.load(new URL("file","","resources/Lib.xml"));
		Parser parser = new Parser("lib/models/german-fast.tagger");
		parser.setKwdb(kwdb);
		//1340271238839144
		//997991205154456,185511079703831,150681066371674,1726761281857676,2397571379105119,982031203667502,914011196708021,1033741208508759,965881202287446,43611014899035,

		Recipe recipe = new RecipeGetterChefkoch().getRecipe("982031203667502");
		
		parser.parseRecipe(recipe);
		System.err.println(Controller.MESSAGES);		
		
		for(Step step : recipe.getSteps()) {
			System.out.println(step.toEasyToReadString());
		}
	}

    public static void testProcessModeler() throws Exception {

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
                
        		for(Step step : recipe.getSteps()) {
        			System.out.println(step.toEasyToReadString());
        		}

                //Now steps are saved in recipe
                ProcessModeler processModeler = new ProcessModelerImpl();
                processModeler.setFileName(currentRecipe + "toBpmn");
                processModeler.createBpmn(recipe);
            } catch (Exception ex) {
                System.err.println("Could not parse Recipe with id: " + currentRecipe);
                System.err.println("Following error occured: ");
                ex.printStackTrace();
            }
            System.out.println("FINISHED PARSING RECIPE WITH ID " + currentRecipe);
            System.out.println("#################################################");
        }
    }
}
