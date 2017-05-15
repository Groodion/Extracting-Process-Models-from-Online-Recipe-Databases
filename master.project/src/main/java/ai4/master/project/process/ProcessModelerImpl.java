package ai4.master.project.process;

import ai4.master.project.recipe.Recipe;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;
import org.camunda.bpm.model.bpmn.instance.Process;

import java.io.File;
import java.io.IOException;

import static org.camunda.bpm.model.xml.test.AbstractModelElementInstanceTest.modelInstance;

/**
 * Created by René Bärnreuther on 15.05.2017.
 */
public class ProcessModelerImpl implements ProcessModeler {


    private BpmnModelInstance modelInstance;

    /**
     * Returns an empty instance currently
     * @param recipe The recipe containing the steps annd other informations for convesion
     * @return
     */
    public BpmnModelInstance convertToProcess(Recipe recipe) {
        this.modelInstance = Bpmn.createProcess("Test").name("TestProzess").startEvent().userTask("Kochen").name("Some cooking to do").endEvent().done();

        System.out.println(createXml());

        return modelInstance;
    }
    private <T extends BpmnModelElementInstance> T createElement(BpmnModelElementInstance parentElement, String id, Class<T> elementClass) {
        T element = modelInstance.newInstance(elementClass);
        element.setAttributeValue("id", id, true);
        parentElement.addChildElement(element);
        return element;
    }

    private SequenceFlow createSequenceFlow(Process process, FlowNode from, FlowNode to) {
        String identifier = from.getId() + "-" + to.getId();
        SequenceFlow sequenceFlow = this.createElement(process, identifier, SequenceFlow.class);
        process.addChildElement(sequenceFlow);
        sequenceFlow.setSource(from);
        from.getOutgoing().add(sequenceFlow);
        sequenceFlow.setTarget(to);
        to.getIncoming().add(sequenceFlow);
        return sequenceFlow;
    }

    private Definitions setDefinitions(BpmnModelInstance modelInstance){
        Definitions definitions = modelInstance.newInstance(Definitions.class);
        definitions.setTargetNamespace("http://camunda.org/examples");
        modelInstance.setDefinitions(definitions);
        return definitions;
    }

    /**
     * Creates a XML-Instance of the given model
     * @return the model in bpmn xml format
     */
    public String createXml(){
        Bpmn.validateModel(modelInstance);
        return Bpmn.convertToString(modelInstance);
    }


    public static void main(String[] args){
        ProcessModelerImpl processModeler = new ProcessModelerImpl();
        processModeler.convertToProcess(new Recipe());
    }
}
