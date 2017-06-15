package ai4.master.project.process;

import ai4.master.project.recipe.Recipe;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.bpmndi.*;
import org.camunda.bpm.model.bpmn.instance.dc.Bounds;
import org.camunda.bpm.model.bpmn.instance.di.Waypoint;


/**
 * Created by René Bärnreuther on 15.05.2017.
 */
public class ProcessModelerImpl implements ProcessModeler {


    /*
    If a node has more than one children we need a parallel gate. So maybe we could call a method that creates everything starting from there (the gate) on?
    We give the method the "subtree" and create the BPMN Model from there on and return and append it maybe.
     */

    private String lastUseNodeID = null;

    BpmnModelInstance modelInstance;
    public BpmnModelInstance convertToProcess(Recipe recipe){
        // create an empty model
        modelInstance = Bpmn.createEmptyModel();
        Definitions definitions = modelInstance.newInstance(Definitions.class);
        definitions.setTargetNamespace("http://camunda.org/examples");
        modelInstance.setDefinitions(definitions);

        // create the process
        org.camunda.bpm.model.bpmn.instance.Process process = modelInstance.newInstance(Process.class);
        process.setAttributeValue("id", "process-one-task", true);
        definitions.addChildElement(process);

        BpmnDiagram diagram = modelInstance.newInstance(BpmnDiagram.class);
        BpmnPlane plane = modelInstance.newInstance(BpmnPlane.class);
        plane.setBpmnElement(process);
        diagram.setBpmnPlane(plane);
        definitions.addChildElement(diagram);


        // WE NEED TO DO IT LIKE STHIS
        // create start event, user task and end event
        StartEvent startEvent = createElement(process, "start", "Di generation wanted",
                StartEvent.class, plane, 15, 15, 50, 50, true);

        UserTask userTask = createElement(process, "userTask", "Generate Model with DI",
                UserTask.class, plane, 100, 0, 80, 100, false);

        createSequenceFlow(process, startEvent, userTask, plane, 65, 40, 100, 40);

        EndEvent endEvent = createElement(process, "end", "DI generation completed",
                EndEvent.class, plane, 250, 15, 50, 50, true);

        createSequenceFlow(process, userTask, endEvent, plane, 200, 40, 250, 40);

        // validate and write model to file
        Bpmn.validateModel(modelInstance);

        System.out.println(Bpmn.convertToString(modelInstance));

        System.out.println("----");


        return modelInstance;

    }

    protected <T extends BpmnModelElementInstance> T createElement(BpmnModelElementInstance parentElement,
                                                                   String id, String name, Class<T> elementClass, BpmnPlane plane,
                                                                   double x, double y, double heigth, double width, boolean withLabel) {
        T element = modelInstance.newInstance(elementClass);
        element.setAttributeValue("id", id, true);
        element.setAttributeValue("name", name, false);
        parentElement.addChildElement(element);

        BpmnShape bpmnShape = modelInstance.newInstance(BpmnShape.class);
        bpmnShape.setBpmnElement((BaseElement) element);

        Bounds bounds = modelInstance.newInstance(Bounds.class);
        bounds.setX(x);
        bounds.setY(y);
        bounds.setHeight(heigth);
        bounds.setWidth(width);
        bpmnShape.setBounds(bounds);

        if (withLabel) {
            BpmnLabel bpmnLabel = modelInstance.newInstance(BpmnLabel.class);
            Bounds labelBounds = modelInstance.newInstance(Bounds.class);
            labelBounds.setX(x);
            labelBounds.setY(y + heigth);
            labelBounds.setHeight(heigth);
            labelBounds.setWidth(width);
            bpmnLabel.addChildElement(labelBounds);
            bpmnShape.addChildElement(bpmnLabel);
        }
        plane.addChildElement(bpmnShape);

        return element;
    }

    public SequenceFlow createSequenceFlow(org.camunda.bpm.model.bpmn.instance.Process process, FlowNode from, FlowNode to, BpmnPlane plane,
                                           int... waypoints) {
        String identifier = from.getId() + "-" + to.getId();
        SequenceFlow sequenceFlow = modelInstance.newInstance(SequenceFlow.class);
        sequenceFlow.setAttributeValue("id", identifier, true);
        process.addChildElement(sequenceFlow);
        sequenceFlow.setSource(from);
        from.getOutgoing().add(sequenceFlow);
        sequenceFlow.setTarget(to);
        to.getIncoming().add(sequenceFlow);

        BpmnEdge bpmnEdge = modelInstance.newInstance(BpmnEdge.class);
        bpmnEdge.setBpmnElement(sequenceFlow);
        for (int i = 0; i < waypoints.length / 2; i++) {
            double waypointX = waypoints[i*2];
            double waypointY = waypoints[i*2+1];
            Waypoint wp = modelInstance.newInstance(Waypoint.class);
            wp.setX(waypointX);
            wp.setY(waypointY);
            bpmnEdge.addChildElement(wp);
        }
        plane.addChildElement(bpmnEdge);

        return sequenceFlow;
    }
    private String createIdOf(String input){
        return input.replace(" ", "_");
    }
    /**
     * Returns an empty instance currently
     * @param recipe The recipe containing the steps annd other informations for convesion
     * @return
     */
  /*  public BpmnModelInstance convertToProcess1(Recipe recipe) {
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
*/
    /**
     * Creates a XML-Instance of the given model
     * @return the model in bpmn xml format
     */
    public String createXml(){
        //Bpmn.validateModel(modelInstance);
        return null;
    }
}
