package ai4.master.project.process;

import ai4.master.project.recipe.Recipe;
import ai4.master.project.recipe.Step;
import ai4.master.project.recipe.object.Ingredient;
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
            for(Tree t: treeList){
                TreeTraverser<Step> treeTraverser = new TreeTraverser<>(t);
                usedNodes.addAll(treeTraverser.preOrder());
            }


            // We check for a dependency in the currentnode with a used node.

            Step currentStep = steps.get(i);
            boolean foundOne = false;
            for (int k = 0; k < usedNodes.size(); k++) {
                if (compare(usedNodes.get(k).getData().getProducts(), currentStep.getIngredients())) {
                    foundOne = true;



                    System.out.println("Match");
                    System.out.println(usedNodes.get(k).getData().getText());
                    //System.out.println("product : " + usedNodes.get(k).getData().printProducts());
                    System.out.println("DEPENDING ON");
                    //System.out.println(currentStep.getIngredients());
                    System.out.println(currentStep.getText());
                    System.out.println();

                    //Now we know: Current step needs usedNodes.get(k) as a child (this means it has to be done before!
                    usedNodes.get(k).addChild(currentStep);
                }
            }

            if(!foundOne){
                // Create a new tree with the node and add it to the list.
                Tree<Step> tree1 = new Tree<>(new Node<Step>(currentStep));
                treeList.add(tree1);
            }

        }


        Tree<Step> finalTree = new Tree<>(new Node<>(new Step()));
        for (Tree t :
                treeList) {
            finalTree.getRoot().addChild(t.getRoot());
        }

        TreeTraverser t = new TreeTraverser(finalTree);
       // t.activatePrint();
        t.preOrder();
        System.out.println("FINISHED TRAVERSING FINAL TREE");
        return finalTree;


    }

    /*
    Returns a list with all steps starting at k
     */
    private List<Step> createRemainingSteps(int k, List<Step> steps) {
        List<Step> remaining = new ArrayList<>();
        for (int i = k; i < steps.size(); i++) {
            remaining.add(steps.get(i));
        }
        return remaining;
    }

    @Deprecated
    public Tree convertTree(Recipe recipe) {
        Tree<Step> tree = new Tree();
        List<Step> steps = recipe.getSteps();
        Node<Step> root = new Node<Step>();
        root.setData(new Step()); // start node
        tree.setRoot(new Node<Step>(steps.get(0)));

        for (int i = 0; i < steps.size(); i++) {

            Node<Step> currentNode = new Node();
            Step currentStep = steps.get(i);
            currentNode.setData(currentStep);

            boolean isDependent = false;
            for (int j = 0; j < steps.size(); j++) {
                Step jStep = steps.get(j);
                if (compare(currentNode.getData().getIngredients(), jStep.getProducts())) {
                    addChildToNode(tree, steps.get(j), currentStep);
                    currentNode.addChild(steps.get(j));
                    isDependent = true;
                }
            }
            if (!isDependent) {
                tree.getRoot().addChild(currentNode);
            }
        }

        return tree;
    }

    @Deprecated
    private void addChildToNode(Tree<Step> tree, Step father, Step child) {
        TreeTraverser<Step> treeTraverser = new TreeTraverser<Step>(tree);

        List<Node<Step>> allNodes = treeTraverser.preOrder();

        // Is it really THAT simple? We will see
        for (Node node : allNodes) {
            if (node.getData().equals(father)) { //This equals doesnt work
                node.getChildren().add(child);
            }
        }
        // TODO traverse list and set children
    }


    /*
    Compares the two lists and returns true if at least one thing is equal.
     */
    private boolean compare(List<Ingredient> inputIngredient, List<Ingredient> outputIngredient) {
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
        return false;
    }
}
