package ai4.master.project;

import ai4.master.project.apirequests.RecipeGetterChefkoch;
import ai4.master.project.process.ProcessModeler;
import ai4.master.project.process.ProcessModelerImpl;
import ai4.master.project.recipe.Recipe;
import ai4.master.project.recipe.LANG_FLAG;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by René Bärnreuther on 15.05.2017.
 */
public class Main {

    public static void main(String[] args) {

        RecipeGetterChefkoch recipeGetter = RecipeGetterChefkoch.recipeGetterFactory();
        String ids = recipeGetter.getRecipeIDs("Käse",1);

        String[] id = ids.split(",");
        List<Recipe> recipes = new ArrayList<Recipe>();
        for(int i = 0; i < id.length;  i++){
            System.out.println(id[i]);
            Recipe r = new Recipe(LANG_FLAG.DE);
            recipeGetter.getRecipeIngredients(id[i],r);
            recipeGetter.getRecipePreparation(id[i],r);
            recipes.add(r);
        }

        ProcessModeler processModeler = new ProcessModelerImpl();
        //processModeler.convertToProcess(new Recipe());

        //ProcessModeler example = new ExampleRecipe();
       // example.convertToProcess(new Recipe(LANG_FLAG.DE));

    }
}
