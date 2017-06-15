package ai4.master.project;

import ai4.master.project.process.ProcessModeler;
import ai4.master.project.process.ProcessModelerImpl;
import ai4.master.project.process.RecipeToTreeConverter;
import ai4.master.project.recipe.Recipe;
import ai4.master.project.recipe.TestRecipeFactory;
import ai4.master.project.tree.Tree;

/**
 * Created by René Bärnreuther on 30.05.2017.
 */
public class MainConverter {

    public static void main(String[] args){


        RecipeToTreeConverter recipeToTreeConverter = new RecipeToTreeConverter();
        TestRecipeFactory testRecipeFactory = new TestRecipeFactory();
        Recipe recipe = testRecipeFactory.createSimpleRecipe();
      /*  for(Step ingredient : recipe.getSteps()){
            System.out.println("Pro: " + ingredient.printProducts());
            System.out.println("Ing: " + ingredient.printIngredients());
        }*/
        Tree tree = recipeToTreeConverter.createTree(recipe);


        ProcessModeler processModeler = new ProcessModelerImpl();
        processModeler.convertToProcess(recipe);

    }
}
