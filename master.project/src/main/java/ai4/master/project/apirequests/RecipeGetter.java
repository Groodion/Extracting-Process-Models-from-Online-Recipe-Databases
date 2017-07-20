package ai4.master.project.apirequests;

import ai4.master.project.apirequests.exceptions.ServerOfflineException;
import ai4.master.project.recipe.LANG_FLAG;
import ai4.master.project.recipe.Recipe;

/**
 * Created by René Bärnreuther on 16.05.2017.
 *
 * Implements a interface to retrieve data from different cooking apis.
 */
public interface RecipeGetter {


    default Recipe getRecipe(String id) throws ServerOfflineException {
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
    String getRecipePreparation(String id, Recipe recipe) throws ServerOfflineException;

    /**
     *Returns the ingredients for a specific recipe from any given website
     * @param id     the recipe to look for in the api(in chefkoch every recipe has an specific id)
     * @param recipe the recipe class to save the informations in
     * @return a string containing the ingredients for debug purposes especially
     */
    String getRecipeIngredients(String id, Recipe recipe) throws ServerOfflineException;


    default LANG_FLAG getLanguage(){return LANG_FLAG.DE;}

	default Recipe getRecipeByID(String id) throws ServerOfflineException {
		return getRecipe(id);
	}

	Recipe getRecipeByLink(String link) throws ServerOfflineException;

	Recipe getRecipeByCategory(String category) throws ServerOfflineException;
}
