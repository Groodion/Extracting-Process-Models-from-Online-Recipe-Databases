package ai4.master.project;

import ai4.master.project.apirequests.RecipeGetterChefkoch;
import ai4.master.project.recipe.baseObject.BaseRecipe;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;

public class RecipeGetterTest {

    /*
    Main will get 5 IDs for "Käse" and will print the recipes for it.
     */
    public static void main(String[] args) {

        String ids = RecipeGetterChefkoch.recipeGetterFactory().getRecipeIDs("käse", 5);
        String[] splittedIds = ids.split(",");
        BaseRecipe recipe = new BaseRecipe();
        for (String id :
                splittedIds) {
            if (!id.equals("") && id != null) {
                System.out.println("id:" + id);
                System.out.println(RecipeGetterChefkoch.recipeGetterFactory().getRecipePreparation(id, recipe));
            }

        }
        //System.out.println(RecipeGetterChefkoch.recipeGetterFactory().getRecipePreparation("1256061231073046"));

        /* Creating a model instance via framework */
        BpmnModelInstance modelInstance = Bpmn.createEmptyModel(); // Just to show that the framework is here..
        // https://docs.camunda.org/manual/7.5/user-guide/model-api/bpmn-model-api/create-a-model/ look here for more

    }
}
