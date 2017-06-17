package ai4.master.project.process;

import ai4.master.project.output.XMLWriter;
import ai4.master.project.recipe.Recipe;
import ai4.master.project.recipe.Step;
import ai4.master.project.tree.Node;
import ai4.master.project.tree.Tree;
import ai4.master.project.tree.TreeTraverser;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.bpmndi.*;
import org.camunda.bpm.model.bpmn.instance.dc.Bounds;
import org.camunda.bpm.model.bpmn.instance.di.Waypoint;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by René Bärnreuther on 15.05.2017.
 */
public class ProcessModelerImpl implements ProcessModeler {


    // TODO Add Input/Output-Parameter
    // TODO Add Tools

    /*
    If a node has more than one children we need a parallel gate. So maybe we could call a method that creates everything starting from there (the gate) on?
    We give the method the "subtree" and create the BPMN Model from there on and return and append it maybe.
     */

    private String lastUseNodeID = null;

    BpmnModelInstance modelInstance;
    List<UserTask> userTasks = new ArrayList<>();
    List<SequenceFlow> flows = new ArrayList<>();

    public BpmnModelInstance convertToProcess(Recipe recipe) {

        Tree<Step> t = new RecipeToTreeConverter().createTree(recipe);
        List<Node<Step>> nodes = new TreeTraverser<Step>(t).preOrder();
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


        StartEvent startEvent = createElement(process, "start", "Start", StartEvent.class, plane, 15, 15, 50, 50, true);
        EndEvent endEvent = createElement(process, "end", "Ende", EndEvent.class, plane, 150, 150, 50, 50, false);

        //First we create the user tasks for all nodes.
        createUserTasks(nodes, process, plane);

        // We connect every node to every child node.
        // In case there are more than 1 children, we put a parallel gateway in between.
        createConnectionToChildren(nodes, process, plane);

        // The first node is an empty root node. Every children from there has to be connected to the start node.
        createStartEventToConnections(t, startEvent, process, plane);


        //Every node without children belongs to the endEvent
        createNodeToEndEventConnection(nodes, endEvent, process, plane);

        // validate and write model to file
        Bpmn.validateModel(modelInstance);
        createXml();
        System.out.println(Bpmn.convertToString(modelInstance));

        System.out.println("----");


        return modelInstance;

    }

    /*
    Connects every "last" node with the endpoint.
     */
    private void createNodeToEndEventConnection(List<Node<Step>> nodes, EndEvent endEvent, Process process, BpmnPlane plane) {
        for (Node<Step> node : nodes) {
            if (node.getChildren().size() == 0) {
                System.out.println("Connection to end-event");
                if (node.getData().getText() != null) {
                    if (!sequenceExists(createId(getUserTaskTo(node), endEvent))) {
                        flows.add(createSequenceFlow(process, getUserTaskTo(node), endEvent, plane, 65, 40, 100, 40));
                    }
                }
            }
        }
    }

    /*
    Creates connection from startevent to starting nodes.
     */
    private void createStartEventToConnections(Tree<Step> t, StartEvent startEvent, Process process, BpmnPlane plane) {
        for (Node<Step> node : t.getRoot().getChildren()) {
            flows.add(createSequenceFlow(process, startEvent, getUserTaskTo(node), plane, 65, 40, 100, 40));
        }

    }

    /*
    Creates connection to children. Every parent is connected to every children. If there are more than one children we need a gateway in between.
    TODO Implement the gateway.
     */
    private void createConnectionToChildren(List<Node<Step>> nodes, Process process, BpmnPlane plane) {
        int i = 0;
        for (Node<Step> node :
                nodes) {

            if (node.getData().getText() == null) {
                continue;
            }
            boolean useGateway = false;
            UserTask from = getUserTaskTo(node);
            System.out.println(from.getAttributeValue("id"));


            // TODO we need to think about how to add parallel gateways

            for (Node<Step> childNode :
                    node.getChildren()) {

                UserTask to = getUserTaskTo(childNode);
                System.out.println("From: " + from.getAttributeValue("name") + " to: " + to.getAttributeValue("name"));

                if (!sequenceExists(createId(from, to))) {

                    flows.add(createSequenceFlow(process, from, to, plane, 65, 40, 100, 40));

                }

            }
            i++;
        }
    }

    /*
    Create all user tasks.
     */
    private void createUserTasks(List<Node<Step>> nodes, Process process, BpmnPlane plane) {
        for (Node<Step> node : nodes) {
            if (node.getData().getText() == null) {
                continue;
            }
            if (!idExists(createIdOf(node.getData().getText()))) {
                UserTask userTask = createElement(process, createIdOf(node.getData().getText()), node.getData().getText(), UserTask.class, plane, 100, 0, 80, 100, false);
                userTasks.add(userTask);
                System.out.println("Creating " + node.getData().getText() + "with id " + createIdOf(node.getData().getText()));

            }

        }
    }

    /*
    Returns the by default created id for flows to check for their existence already because we cannot add dupplicates.
     */
    @NotNull
    private String createId(FlowNode from, FlowNode to) {
        return from.getId() + "-" + to.getId();
    }

    /*
    Returns true if a given userTaskID exists already.
     */
    private boolean idExists(String id) {
        for (UserTask u : userTasks) {
            if (u.getAttributeValue("id").equals(id)) {
                return true;
            }
        }
        return false;
    }

    /*
    Returns the user task to the given node.
     */
    private UserTask getUserTaskTo(Node<Step> node) {
        for (UserTask u : userTasks) {
            String result = "";
            result = u.getAttributeValue("id");

            if (result.equals(createIdOf(node.getData().getText()))) {
                return u;
            }
        }
        return null;
    }

    /*
    Creates a BPMN Element
     */
    private <T extends BpmnModelElementInstance> T createElement(BpmnModelElementInstance parentElement,
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

    private SequenceFlow createSequenceFlow(org.camunda.bpm.model.bpmn.instance.Process process, FlowNode from, FlowNode to, BpmnPlane plane,
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
            double waypointX = waypoints[i * 2];
            double waypointY = waypoints[i * 2 + 1];
            Waypoint wp = modelInstance.newInstance(Waypoint.class);
            wp.setX(waypointX);
            wp.setY(waypointY);
            bpmnEdge.addChildElement(wp);
        }
        plane.addChildElement(bpmnEdge);

        return sequenceFlow;
    }


    /*
    Checks for a given sequence. Returns true if the sequence exists alredy.
     */
    private boolean sequenceExists(String id) {
        for (SequenceFlow s :
                flows) {
            if (s.getAttributeValue("id").equals(id)) {
                return true;
            }
        }
        return false;
    }

    /*
    Creates a ID by replacing all spaces with _
     */
    private String createIdOf(String input) {
        return input.replace(" ", "_");
    }

    /**
     * Creates a XML-Instance of the given model
     *
     * @return the model in bpmn xml format
     */
    public void createXml() {
        Bpmn.validateModel(modelInstance);

        XMLWriter xmlWriter = new XMLWriter("test-from-recipe");
        xmlWriter.writeTo(Bpmn.convertToString(modelInstance));
    }
}
