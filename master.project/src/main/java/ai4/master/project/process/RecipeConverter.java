package ai4.master.project.process;

import ai4.master.project.output.XMLWriter;
import ai4.master.project.recipe.Recipe;
import ai4.master.project.recipe.Step;
import ai4.master.project.recipe.object.Ingredient;
import ai4.master.project.tree.Node;
import ai4.master.project.tree.Tree;
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
@Deprecated
public class RecipeConverter {


    private Recipe recipe;

    public BpmnModelInstance convertToBpmn(Recipe recipe){
        //sortProducts(recipe);
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
        String xml = Bpmn.convertToString(currentModel);
        Bpmn.writeModelToStream(System.out, currentModel);
        XMLWriter xmlWriter = new XMLWriter("example").writeTo(xml);
        return currentModel;
    }



    // Creates the recipe tree Starting with the steplist
    private Node<Step> createRecipeTree(List<Step> stepList) throws Exception{
        List<Step> inversedStepList = reverse(stepList);
        if(inversedStepList.size() == 0){
            throw new Exception("Somethings wrong with the stepList in createRecipeTree. The inversedList is empty.");
        }
        List<Tree<Step>> treeList = new ArrayList<Tree<Step>>();

        Tree<Step> tree = new Tree<Step>();
        Node<Step> root = new Node<Step>();
        root.setData(inversedStepList.get(0)); //This is the last element of the recipe.
        tree.setRoot(root);
        treeList.add(tree);

        inversedStepList.remove(0);
        List<Integer> toRemove = new ArrayList<Integer>();

        /*
        Basic Algorithmn Idea:
        Beginning from root:
            Check all other steps: If at least one ingredient of root
            is a product of currStep: add it as a child. This means it depends
            on it and has to be done before.
            Otherwise it is not dependent and should build another tree.
            The used nodes have to be removed from the list.
            Do until no Tree changes anymore probably. Have to think through this.

         */
        for (Tree<Step> t :
                treeList) {

            List<Ingredient> ingredientsOfRoot =t.getRoot().getData().getIngredients();
            for (Step s:
                 inversedStepList) {
                if(isDependent(ingredientsOfRoot, s.getProducts())){
                    t.getRoot().addChild(s);
                }else{
                    Tree newTree = new Tree();
                    newTree.setRoot(new Node<Step>(s));
                    treeList.add(newTree);
                }

            }

        }


        return root;
    }

    private boolean isDependent(List<Ingredient> parent, List<Ingredient> maybeChild){
        // TODO Implement me

        return false;
    }

    private List<Step> reverse(List<Step> list){
        List<Step> reversedList = new ArrayList<Step>();

        // TODO

        return reversedList;
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
            ingredientBuilder.append(i.getCompleteName());
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
