package ai4.master.project.process;

import ai4.master.project.recipe.Step;
import ai4.master.project.recipe.Recipe;
import ai4.master.project.recipe.object.Ingredient;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.builder.ProcessBuilder;
import org.camunda.bpm.model.bpmn.builder.StartEventBuilder;
import org.camunda.bpm.model.bpmn.builder.UserTaskBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by René Bärnreuther on 24.05.2017.
 */
public class RecipeConverter {


    private Recipe recipe;

    public BpmnModelInstance convertToBpmn(Recipe recipe){
        BpmnModelInstance currentModel = Bpmn.createEmptyModel();
        ProcessBuilder currentBuilder = Bpmn.createProcess("Recipe");
        StartEventBuilder startEventBuilder = currentBuilder.startEvent("Start");
        UserTaskBuilder userTaskBuilder = null;
        int i = 0;
        for(Step step : recipe.getSteps()) {
        if(i == 0) {
            userTaskBuilder = startEventBuilder.userTask("id" + i).name(step.getText());
        }else{
            userTaskBuilder = userTaskBuilder.userTask("id" + i).name(step.getText());
        }

            addIngredients(userTaskBuilder, step);
            addProducts(userTaskBuilder,step);

            i++;

        }
        currentModel = currentBuilder.done();

        Bpmn.validateModel(currentModel);
        Bpmn.writeModelToStream(System.out, currentModel);
        return currentModel;
    }



    private void sortProducts(Recipe baseRecipe){
        List<Step> orderdSteps = new ArrayList<Step>();

        for (Step s :
                baseRecipe.getSteps()) {
            for(Ingredient i: s.getProducts()){
               // System.out.println(i.getIngredientName());
            }

        }
    }
    /**
     * Adds ingredients from the step to the usertask
     * @param userTaskBuilder the current userTas
     * @param step the step containing the informaton
     */
    private void addIngredients(UserTaskBuilder userTaskBuilder, Step step){
        StringBuilder ingredientBuilder = new StringBuilder();
        for (Ingredient i :
                step.getIngredients()) {
            //ingredientBuilder.append(i.getIngredientName());
        }
        userTaskBuilder.camundaInputParameter("Ingredients",ingredientBuilder.toString());
    }

    /**
     * Adds products from the step to the usertask
     * @param userTaskBuilder the current userTas
     * @param step the step containing the informaton
     */
    private void addProducts(UserTaskBuilder userTaskBuilder, Step step){
        StringBuilder productBuilder = new StringBuilder();
        if (step.getProducts() != null) {
            for (Ingredient i : step.getProducts()) {
                productBuilder.append(i.toString());
            }
        }
        userTaskBuilder.camundaOutputParameter("Products",productBuilder.toString());
    }
}
