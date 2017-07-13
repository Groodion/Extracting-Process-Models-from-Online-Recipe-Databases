package ai4.master.project.process;

import ai4.master.project.recipe.Recipe;
import ai4.master.project.recipe.Step;
import ai4.master.project.recipe.object.Ingredient;
import ai4.master.project.recipe.object.Tool;
import ai4.master.project.tree.Node;
import ai4.master.project.tree.Tree;
import ai4.master.project.tree.TreeTraverser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Michael, René on 08.06.2017.
 */
public class RecipeToTreeConverter {


    // Test Rezept ist in der TestRecipeFactory mit implementiert von mir. Ich denke, da sollten alle Fälle abgedeckt sein.
    public Tree createTree(Recipe recipe) {
        List<Tree> treeList = new ArrayList<Tree>();
        Tree<Step> tree = new Tree();
        treeList.add(tree);

        List<Step> steps = recipe.getSteps();
       // translateSteps(steps);
        for (int i = 0; i < steps.size(); i++) {
            if (i == 0) {
                //If i = 0 we need to add it as the root because its the first node.
                tree.setRoot(new Node(steps.get(i)));
                continue;
            }
            //We actually start at iteration 1. ;)
            // We need to check all steps for a dependency on a already used step. If we have one, we will add it as child of this node.

            // We need a traversal of every tree we have to look for dependencys.

            List<Node<Step>> usedNodes = new ArrayList<>();
            for (Tree t : treeList) {
                TreeTraverser<Step> treeTraverser = new TreeTraverser<>(t);
                usedNodes.addAll(treeTraverser.preOrder());
            }


            // We check for a dependency in the currentnode with a used node.

            Step currentStep = steps.get(i);
            boolean foundOne = false;
            for (int k = 0; k < usedNodes.size(); k++) {
                List<Step> foundNodes = new ArrayList<>();
                if (compare(usedNodes.get(k).getData().getProducts(), currentStep.getIngredients(), usedNodes.get(k).getData().getTools(), currentStep.getTools())) {


                    foundOne = true;


                    System.out.println("Match");
                    System.out.println(usedNodes.get(k).getData().getText());
                    //System.out.println("product : " + usedNodes.get(k).getData().printProducts());
                    System.out.println("DEPENDING ON");
                    //System.out.println(currentStep.getIngredients());
                    System.out.println(currentStep.getText());
                    System.out.println();
                    foundNodes.add(currentStep);
                    //Now we know: Current step needs usedNodes.get(k) as a child (this means it has to be done before!
                    //usedNodes.get(k).addChild(currentStep);
                }

                // FIx for wrong parallel gateways
                for(Step s : foundNodes){
                    if(usedNodes.get(k).getChildren().size() == 0){
                        usedNodes.get(k).addChild(s);
                    }else{
                        //don't add it.. let's see what happens
                    }
                }
            }

            if (!foundOne) {
                // Create a new tree with the node and add it to the list.
                Tree<Step> tree1 = new Tree<>(new Node<Step>(currentStep));
                treeList.add(tree1);
            }

        }


        Tree<Step> finalTree = new Tree<>(new Node<>(new Step()));
        for (Tree t :
                treeList) {
            if(finalTree.getRoot() == null){
                System.out.println("FinalTree root == null");
            }
            if(t.getRoot() == null){
                System.out.println("Child root == null");
            }
            finalTree.getRoot().addChild(t.getRoot());
        }

        TreeTraverser t = new TreeTraverser(finalTree);
        // t.activatePrint();
        t.preOrder();
        System.out.println("FINISHED TRAVERSING FINAL TREE");
        return finalTree;


    }




    /*
    Compares the two lists and returns true if at least one thing is equal.
     */
    private boolean compare(List<Ingredient> inputIngredient, List<Ingredient> outputIngredient, List<Tool> inputTool, List<Tool> outputTool) {
        for (Ingredient input :
                inputIngredient) {
            for (Ingredient output :
                    outputIngredient) {
                //System.out.println("Comparing: Input: " + input.getIngredientName() + " with Output: " + output.getIngredientName());
                if (input.equals(output)) {
                    return true;

                }
            }
        }
        // TODO We need to compare tools somehow?
        for(Tool input : inputTool ){
            for(Tool output: outputTool){
                if(input.isCharged() ||output.isCharged()){
                if(input.equals(output)){
                    System.out.println("Checking tools, mate");
                    return true;
                }}
            }
        }
        return false;
    }

}
