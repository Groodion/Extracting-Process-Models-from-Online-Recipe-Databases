package ai4.master.project;

import ai4.master.project.apirequests.RecipeGetterChefkoch;
import ai4.master.project.recipe.Recipe;
import ai4.master.project.recipe.LANG_FLAG;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;

public class RecipeGetterTest {

    /*
    Main will get 5 IDs for "Käse" and will print the recipes for it.
     */
    public static void main(String[] args) {

        String ids = RecipeGetterChefkoch.recipeGetterFactory().getRecipeIDs("käse", 5);
        String[] splittedIds = ids.split(",");
        Recipe recipe = new Recipe(LANG_FLAG.DE);
        for (String id :
                splittedIds) {
            if (!id.equals("") && id != null) {
                System.out.println("id:" + id);
                System.out.println(RecipeGetterChefkoch.recipeGetterFactory().getRecipePreparation(id, recipe));
            }

        }
        //System.out.println(RecipeGetterChefkoch.recipeGetterFactory().getRecipePreparation("1256061231073046"));
    }
}
