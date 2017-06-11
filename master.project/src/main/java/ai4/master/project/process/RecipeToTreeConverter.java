package ai4.master.project.process;

import ai4.master.project.recipe.Step;
import ai4.master.project.recipe.baseObject.BaseRecipe;
import ai4.master.project.recipe.object.Ingredient;
import ai4.master.project.tree.Node;
import ai4.master.project.tree.Tree;
import ai4.master.project.tree.TreeTraverser;

import java.util.List;

/**
 * Created by Michael, René on 08.06.2017.
 */
public class RecipeToTreeConverter {

    public Tree convertTree(BaseRecipe recipe){
        Tree<Step> tree = new Tree();
        List<Step> steps = recipe.getSteps();
        Node<Step> root = new Node<Step>();
        root.setData(new Step()); // start node
        tree.setRoot(root);

        for (int i = 0; i < steps.size(); i++) {

            Node<Step> currentNode = new Node();
            Step currentStep = steps.get(i);
            currentNode.setData(currentStep);

            boolean isDependent = false;
            for(int j = 0; j < steps.size(); j++){
                if(compare(currentStep.getIngredients(), steps.get(j).getIngredients())){
                    addChildToNode(tree, steps.get(j), currentStep);
                    currentNode.addChild(steps.get(j));
                    isDependent =true;
                }
            }
            if(!isDependent){
                tree.getRoot().addChild(currentNode);
            }
        }

        return tree;
    }


    private void addChildToNode(Tree<Step> tree, Step father, Step child){
        TreeTraverser<Step> treeTraverser = new TreeTraverser<Step>(tree);

        List<Node<Step>> allNodes = treeTraverser.preOrder();

        // Is it really THAT simple? We will see
        for(Node node : allNodes){
            if(node.getData().equals(father)){
                node.getChildren().add(child);
            }
        }
        // TODO traverse list and set children
    }


    private boolean compare(List<Ingredient> inputIngredient, List<Ingredient> outputIngredient) {
        for (Ingredient input :
                inputIngredient) {
            for (Ingredient output :
                    outputIngredient) {
                if (input.equals(output)) {
                    return true;
                }
            }
        }
        return false;
    }
}
