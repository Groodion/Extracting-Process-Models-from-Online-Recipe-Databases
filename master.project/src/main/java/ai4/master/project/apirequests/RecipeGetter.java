package ai4.master.project.apirequests;

import ai4.master.project.recipe.Recipe;

/**
 * Created by René Bärnreuther on 16.05.2017.
 */
public interface RecipeGetter {

    /**
     * @param id     the recipe to look for (in chefkoch every recipe has an specific id)
     * @param recipe
     * @return
     */
    String getRecipePreparation(String id, Recipe recipe);

    String getRecipeIngredients(String id, Recipe recipe);


}
