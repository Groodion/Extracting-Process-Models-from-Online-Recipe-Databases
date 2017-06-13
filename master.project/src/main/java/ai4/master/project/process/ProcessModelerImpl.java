package ai4.master.project.process;

import ai4.master.project.recipe.Recipe;
import ai4.master.project.recipe.Step;
import ai4.master.project.tree.Node;
import ai4.master.project.tree.Tree;
import ai4.master.project.tree.TreeTraverser;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.GatewayDirection;
import org.camunda.bpm.model.bpmn.builder.AbstractFlowNodeBuilder;
import org.camunda.bpm.model.bpmn.builder.ProcessBuilder;
import org.camunda.bpm.model.bpmn.builder.StartEventBuilder;

import java.util.List;


/**
 * Created by René Bärnreuther on 15.05.2017.
 */
public class ProcessModelerImpl implements ProcessModeler {


    private BpmnModelInstance modelInstance;
    private String lastUseNodeID = null;

    public BpmnModelInstance convertToProcess(Recipe recipe){
        RecipeToTreeConverter recipeToTreeConverter = new RecipeToTreeConverter();
        Tree<Step> createdTree = recipeToTreeConverter.createTree(recipe);

        modelInstance = Bpmn.createEmptyModel();

        List<Node<Step>> treeTraversed = new TreeTraverser<Step>(createdTree).preOrder();

        ProcessBuilder processBuilder = Bpmn.createProcess();

        StartEventBuilder startEventBuilder = processBuilder.name("Testrezept").startEvent("Start");
        for (Node<Step> node :
                treeTraversed) {

            if(node.getData().getText() == null){
                continue;
            }
            AbstractFlowNodeBuilder afnb = null;
            if(lastUseNodeID != null) {
                 afnb = startEventBuilder.moveToNode(lastUseNodeID);
            }
            if(afnb != null){
                afnb.userTask(createIdOf(node.getData().getText())).name(node.getData().getText());
            }else{
                 startEventBuilder.userTask(createIdOf(node.getData().getText())).name(node.getData().getText());
            }

            lastUseNodeID = createIdOf(node.getData().getText());





        }




        modelInstance = processBuilder.done();

        System.out.println(Bpmn.convertToString(modelInstance));

        return modelInstance;

    }



    private String createIdOf(String input){
        return input.replace(" ", "_");
    }
    /**
     * Returns an empty instance currently
     * @param recipe The recipe containing the steps annd other informations for convesion
     * @return
     */
    public BpmnModelInstance convertToProcess1(Recipe recipe) {
        //this.modelInstance = Bpmn.createProcess("Test").name("TestProzess").startEvent().userTask("Kochen").name("Some cooking to do").endEvent().done();
        //System.out.println(createXml());
        // System.out.println("---");
        modelInstance = Bpmn.createEmptyModel();


        // Be careful: When opening the xml in Camunda Modeler, "Zwiebeln" and "Knoblauch"- schneiden will overlap.
        this.modelInstance = Bpmn.createProcess()
                .name("Kochrezept")
                .startEvent()
                .serviceTask()
                .name("Sachen bereitstellen")
                .camundaInputParameter("Zutaten", "Zwiebeln")
                .camundaInputParameter("Zutaten", "Knoblauch")
                .camundaInputParameter("Werkzeug", "Messer")
                .camundaOutputParameter("Werkzeug", "Messer")
                .camundaOutputParameter("Rezept", "Bereitgestelltes Kochzeug")
                .parallelGateway("parallel")
                .gatewayDirection(GatewayDirection.Diverging)
                .userTask("zwiebeln")
                .name("Zwieben schneiden")
                .camundaInputParameter("Zutaten", "Zwiebeln")
                .camundaInputParameter("Werkzeug", "Messer")
                .camundaOutputParameter("Zutaten", "Gehackte Zwiebeln")
                .moveToNode("parallel")
                .userTask("knoblauch")
                .name("Knoblauch schneiden")
                .parallelGateway("parallel_end")
                .gatewayDirection(GatewayDirection.Diverging)
                .moveToActivity("zwiebeln")
                .connectTo("parallel_end")
                .moveToActivity("knoblauch")
                .connectTo("parallel_end")
                .moveToNode("parallel_end")
                .userTask("Anschwitzen")
                .name("Beides anschwitzen")
                .endEvent()
                .done();

        System.out.println(createXml());
        return modelInstance;
    }

    /**
     * Creates a XML-Instance of the given model
     * @return the model in bpmn xml format
     */
    public String createXml(){
        Bpmn.validateModel(modelInstance);
        return Bpmn.convertToString(modelInstance);
    }
}
