package ai4.master.project.apirequests;

import ai4.master.project.recipe.LANG_FLAG;
import ai4.master.project.recipe.Recipe;

/**
 * Created by René Bärnreuther on 16.05.2017.
 *
 * Implements a interface to retrieve data from different cooking apis.
 */
public interface RecipeGetter {


    default Recipe getRecipe(String id){
        Recipe recipe = new Recipe(getLanguage());
        getRecipeIngredients(id, recipe);
        getRecipePreparation(id, recipe);
        return recipe;
    }

    /**
     * Returns the preparation for a specific recipe from any given website.
     * @param id     the recipe to look for in the api(in chefkoch every recipe has an specific id)
     * @param recipe the recipe class to save the informations in
     * @return a string containing the preparation for debug purposes especially
     */
    String getRecipePreparation(String id, Recipe recipe);

    /**
     *Returns the ingredients for a specific recipe from any given website
     * @param id     the recipe to look for in the api(in chefkoch every recipe has an specific id)
     * @param recipe the recipe class to save the informations in
     * @return a string containing the ingredients for debug purposes especially
     */
    String getRecipeIngredients(String id, Recipe recipe);


    default LANG_FLAG getLanguage(){return LANG_FLAG.DE;}
}
