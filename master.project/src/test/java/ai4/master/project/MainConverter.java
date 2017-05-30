package ai4.master.project;

import ai4.master.project.process.RecipeConverter;
import ai4.master.project.recipe.TestRecipeFactory;

/**
 * Created by René Bärnreuther on 30.05.2017.
 */
public class MainConverter {

    public static void main(String[] args){
        RecipeConverter recipeConverter = new RecipeConverter();
        recipeConverter.convertToBpmn(new TestRecipeFactory().createRecipe());
    }
}
