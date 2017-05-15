package ai4.master.project.process;

import ai4.master.project.recipe.Recipe;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;

/**
 * Created by René Bärnreuther on 15.05.2017.
 */
public class ProcessModelerImpl implements ProcessModeler {


    private BpmnModelInstance modelInstance;

    public static void main(String[] args) {

    }

    /**
     * Returns an empty instance currently
     * @param recipe The recipe containing the steps annd other informations for convesion
     * @return
     */
    public BpmnModelInstance convertToProcess(Recipe recipe) {
        //this.modelInstance = Bpmn.createProcess("Test").name("TestProzess").startEvent().userTask("Kochen").name("Some cooking to do").endEvent().done();
        //System.out.println(createXml());
        // System.out.println("---");

        // Be careful: When opening the xml in Camunda Modeler, "Zwiebeln" and "Knoblauch"- schneiden will overlap.
        this.modelInstance = Bpmn.createProcess()
                .name("Kochrezept")
                .startEvent()
                .userTask()
                .name("Sachen bereitstellen")
                .parallelGateway("parallel")
                .userTask("zwiebeln")
                .name("Zwieben schneiden")
                .moveToNode("parallel")
                .userTask("knoblauch")
                .name("Knoblauch schneiden")
                .parallelGateway("parallel_end")
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
