package ai4.master.project.process;

import ai4.master.project.output.XMLWriter;
import ai4.master.project.recipe.Recipe;
import ai4.master.project.recipe.Step;
import ai4.master.project.recipe.object.Ingredient;
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


    // TODO Add Tools
    // TODO Maybe a simple algorithm to design layout to be at least a bit visible..
    // TODO Add a possibility to use a parallel or a XOR gateway using nodes isXor


    private int taskX = 100;
    private int taskY = 50;

    private int userTaskHeight = 80;
    private int userTaskWidth = 100;
    /*
    If a node has more than one children we need a parallel gate. So maybe we could call a method that creates everything starting from there (the gate) on?
    We give the method the "subtree" and create the BPMN Model from there on and return and append it maybe.
     */

    BpmnModelInstance modelInstance;
    List<UserTask> userTasks = new ArrayList<>();
    List<SequenceFlow> flows = new ArrayList<>();

    private int tempX = 0;
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


        StartEvent startEvent = createElement(process, "start", "Start", StartEvent.class, plane, taskX, taskY, 50, 50, true);
        tempX = taskX+150;
        taskX+=300;
        //First we create the user tasks for all nodes.
        createUserTasks(nodes, process, plane);

        // We connect every node to every child node.
        // In case there are more than 1 children, we put a parallel gateway in between.
        createConnectionToChildren(nodes, process, plane);

        // The first node is an empty root node. Every children from there has to be connected to the start node.
        createStartEventToConnections(t, startEvent, process, plane);

        EndEvent endEvent = createElement(process, "end", "Ende", EndEvent.class, plane, taskX, taskY, 50, 50, false);

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
                        flows.add(createSequenceFlow(process, getUserTaskTo(node), endEvent, plane,
                                LayoutUtils.getCenterCoordinates(getUserTaskTo(node))[0],
                                LayoutUtils.getCenterCoordinates(getUserTaskTo(node))[1],
                                LayoutUtils.getCenterCoordinates(endEvent)[0],
                                LayoutUtils.getCenterCoordinates(endEvent)[1]));
                    }
                }
            }
        }
    }


    /*
    Creates connection from startevent to starting nodes.
     */
    private void createStartEventToConnections(Tree<Step> t, StartEvent startEvent, Process process, BpmnPlane plane) {
        if (t.getRoot().getChildren().size() > 1) {
            ParallelGateway startParallel = createElement(process, "parallel_gateway_start", "parallel_gateway_start", ParallelGateway.class, plane, tempX, taskY, 30, 30, false);
            taskX += 150;
            flows.add(createSequenceFlow(process, startEvent, startParallel, plane,
                    LayoutUtils.getCenterCoordinates(startEvent)[0],
                    LayoutUtils.getCenterCoordinates(startEvent)[1],
                    LayoutUtils.getCenterCoordinates(startParallel)[0],
                    LayoutUtils.getCenterCoordinates(startParallel)[1]));

            for (Node<Step> node : t.getRoot().getChildren()) {
                System.out.println("Creating connection from ParallelGateway to " + node.getData().getText());
                flows.add(createSequenceFlow(process, startParallel, getUserTaskTo(node), plane,
                        LayoutUtils.getCenterCoordinates(startParallel)[0],
                        LayoutUtils.getCenterCoordinates(startParallel)[1],
                        LayoutUtils.getCenterCoordinates(getUserTaskTo(node))[0],
                        LayoutUtils.getCenterCoordinates(getUserTaskTo(node))[1]));
            }
        } else {
            flows.add(createSequenceFlow(process, startEvent, getUserTaskTo(t.getRoot().getChildren().get(0)), plane,
                    LayoutUtils.getCenterCoordinates(startEvent)[0], //start x
                    LayoutUtils.getCenterCoordinates(startEvent)[1], //start y
                    LayoutUtils.getCenterCoordinates(getUserTaskTo(t.getRoot().getChildren().get(0)))[0], //end x
                    LayoutUtils.getCenterCoordinates(getUserTaskTo(t.getRoot().getChildren().get(0)))[1])); //end y

        }

    }


    /*
    Creates connection to children. Every parent is connected to every children. If there are more than one children we need a gateway in between.
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

            // TODO refactoring this part.
            if (!node.isXor()) {

                if (node.getChildren().size() == 1) {
                    UserTask to = getUserTaskTo(node.getChildren().get(0));
                    if (!sequenceExists(createId(from, to))) {
                        flows.add(createSequenceFlow(process, from, to, plane,
                                LayoutUtils.getCenterCoordinates(from)[0], LayoutUtils.getCenterCoordinates(from)[1],
                                LayoutUtils.getCenterCoordinates(to)[0], LayoutUtils.getCenterCoordinates(to)[1]));
                    }
                } else if (node.getChildren().size() > 1) {
                    System.out.println("Creating a parallel gateway");
                    ParallelGateway parallelGateway = createElement(process, "parallel_gateway_" + i, "parallel_gateway_" + i, ParallelGateway.class, plane, taskX, taskY, 30, 30, false);
                    taskX += 150;
                    // First we need to connect the parent to the parallel gateway.
                    if (!sequenceExists(createId(from, parallelGateway))) {
                        flows.add(createSequenceFlow(process, from, parallelGateway, plane,    LayoutUtils.getCenterCoordinates(from)[0], LayoutUtils.getCenterCoordinates(from)[1],
                                LayoutUtils.getCenterCoordinates(parallelGateway)[0], LayoutUtils.getCenterCoordinates(parallelGateway)[1]));
                    }

                    //Now we create a connection from the gateway to every child
                    for (Node<Step> childNode :
                            node.getChildren()) {

                        UserTask to = getUserTaskTo(childNode);
                        System.out.println("From: " + from.getAttributeValue("name") + " to: " + to.getAttributeValue("name"));

                        if (!sequenceExists(createId(from, to))) {
                            flows.add(createSequenceFlow(process, parallelGateway, to, plane, LayoutUtils.getCenterCoordinates(from)[0], LayoutUtils.getCenterCoordinates(from)[1],
                                    LayoutUtils.getCenterCoordinates(to)[0], LayoutUtils.getCenterCoordinates(to)[1]));

                        }

                    }
                }
                // XOR PART
                else {
                    if (node.getChildren().size() == 1) {
                        UserTask to = getUserTaskTo(node.getChildren().get(0));
                        if (!sequenceExists(createId(from, to))) {
                            flows.add(createSequenceFlow(process, from, to, plane, 65, 40, 100, 40));
                        }
                    } else if (node.getChildren().size() > 1) {
                        System.out.println("Creating a exclusive gateway");
                        ExclusiveGateway exclusiveGateway = createElement(process, "exclusive_gateway_" + i, "exclusive_gateway_" + i, ExclusiveGateway.class, plane, 50, 50, 30, 30, false);
                        // First we need to connect the parent to the parallel gateway.
                        if (!sequenceExists(createId(from, exclusiveGateway))) {
                            flows.add(createSequenceFlow(process, from, exclusiveGateway, plane, 65, 40, 100, 40));
                        }

                        //Now we create a connection from the gateway to every child
                        for (Node<Step> childNode :
                                node.getChildren()) {

                            UserTask to = getUserTaskTo(childNode);
                            System.out.println("From: " + from.getAttributeValue("name") + " to: " + to.getAttributeValue("name"));

                            if (!sequenceExists(createId(from, to))) {
                                flows.add(createSequenceFlow(process, exclusiveGateway, to, plane, 65, 40, 100, 40));

                            }

                        }
                    }
                }
                i++;

            }
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
                UserTask userTask = createElement(process, createIdOf(node.getData().getText()), node.getData().getText(), UserTask.class, plane, taskX, taskY, userTaskHeight, userTaskWidth, false);
                taskX += 150;
                /* Iterate over the Input parameter ( = ingredients) and output parameter ( = products) and add them */
                for (Ingredient ingredient : node.getData().getIngredients()) {
                    userTask.builder().camundaInputParameter("Ingredient", ingredient.getIngredientName());
                }

                for (Ingredient product : node.getData().getProducts()) {
                    userTask.builder().camundaOutputParameter("Product", product.getIngredientName());
                }


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
