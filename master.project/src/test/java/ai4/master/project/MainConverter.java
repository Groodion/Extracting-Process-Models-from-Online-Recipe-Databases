package ai4.master.project;

import ai4.master.project.process.ProcessModeler;
import ai4.master.project.process.ProcessModelerImpl;
import ai4.master.project.process.RecipeToTreeConverter;
import ai4.master.project.recipe.Recipe;
import ai4.master.project.recipe.Step;
import ai4.master.project.recipe.TestRecipeFactory;
import ai4.master.project.tree.Tree;
import ai4.master.project.tree.TreeTraverser;

/**
 * Created by René Bärnreuther on 30.05.2017.
 */
public class MainConverter {

    public static void main(String[] args){


        RecipeToTreeConverter recipeToTreeConverter = new RecipeToTreeConverter();
        TestRecipeFactory testRecipeFactory = new TestRecipeFactory();
        Recipe recipe = testRecipeFactory.createSimpleRecipe();
      /*  for(Step ingredient : recipe.getSteps()){
            System.out.println("Pro: " + ingredient.printProducts());
            System.out.println("Ing: " + ingredient.printIngredients());
        }*/
        Tree tree = recipeToTreeConverter.createTree(recipe);

        TreeTraverser<Step> treeTraverser = new TreeTraverser<>(tree);
        treeTraverser.activatePrint();
        treeTraverser.preOrder();

        ProcessModeler processModeler = new ProcessModelerImpl();
        processModeler.convertToProcess(recipe);
        //new TestRecipeFactory().createRecipe().getSteps().toString();
      /*  System.out.println("Final Preorder");
        for(Tree t: tree){
            System.out.println("--- New Tree ---");
            TreeTraverser<Step> treeTraverser = new TreeTraverser<>(t);
            treeTraverser.activatePrint();
            treeTraverser.preOrder();
        }*/
    }
}
