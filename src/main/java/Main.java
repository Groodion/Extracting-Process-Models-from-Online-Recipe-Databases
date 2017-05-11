import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import recipe.Recipe;
import requests.RecipeGetter;

public class Main {

    /*
    Main will get 5 IDs for "Käse" and will print the recipes for it.
     */
    public static void main(String[] args) {

        Recipe recipe = new Recipe();
        String ids = RecipeGetter.recipeGetterFactory().getRecipeIDs("käse", 1);
        String[] splittedIds = ids.split(",");
        for (String id :
                splittedIds) {
            if (!id.equals("") && id != null) {
                System.out.println("id:" + id);
                System.out.println(RecipeGetter.recipeGetterFactory().getRecipeIngredigents(id, recipe));
                System.out.println(RecipeGetter.recipeGetterFactory().getRecipePreparation(id, recipe));
            }

        }
        //System.out.println(RecipeGetter.recipeGetterFactory().getRecipePreparation("1256061231073046"));

        /* Creating a model instance via framework */
        BpmnModelInstance modelInstance = Bpmn.createEmptyModel(); // Just to show that the framework is here..
        // https://docs.camunda.org/manual/7.5/user-guide/model-api/bpmn-model-api/create-a-model/ look here for more

    }
}
