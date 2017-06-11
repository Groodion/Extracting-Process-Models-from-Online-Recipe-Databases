package ai4.master.project;

import ai4.master.project.process.RecipeToTreeConverter;
import ai4.master.project.recipe.Step;
import ai4.master.project.recipe.TestRecipeFactory;
import ai4.master.project.recipe.baseObject.BaseRecipe;

/**
 * Created by René Bärnreuther on 30.05.2017.
 */
public class MainConverter {

    public static void main(String[] args){


        RecipeToTreeConverter recipeToTreeConverter = new RecipeToTreeConverter();
       // Tree tree = recipeToTreeConverter.convertTree(new TestRecipeFactory().createRecipe());
        BaseRecipe baseRecipe = new TestRecipeFactory().createRecipe();

        for (Step step : baseRecipe.getSteps()){
            System.out.println(step);
        }

               // TreeTraverser treeTraverser = new TreeTraverser(tree);
       // treeTraverser.preOrder();
    }
}
