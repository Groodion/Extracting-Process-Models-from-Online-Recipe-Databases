package ai4.master.project.process;

import ai4.master.project.output.XMLWriter;
import ai4.master.project.recipe.CookingEvent;
import ai4.master.project.recipe.Recipe;
import ai4.master.project.recipe.Step;
import ai4.master.project.recipe.object.Ingredient;
import ai4.master.project.recipe.object.Tool;
import ai4.master.project.tree.Node;
import ai4.master.project.tree.Tree;
import ai4.master.project.tree.TreeTraverser;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.impl.instance.SourceRef;
import org.camunda.bpm.model.bpmn.impl.instance.TargetRef;
import org.camunda.bpm.model.bpmn.instance.*;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.bpmndi.*;
import org.camunda.bpm.model.bpmn.instance.dc.Bounds;
import org.camunda.bpm.model.bpmn.instance.di.Waypoint;
import org.jetbrains.annotations.NotNull;

import java.util.*;


/**
 * Created by René Bärnreuther, Michi Bösch on 15.05.2017.
 */
public class ProcessModelerImpl implements ProcessModeler {


    BpmnModelInstance modelInstance;

    List<UserTask> userTasks = new ArrayList<>();
    List<SequenceFlow> flows = new ArrayList<>();
    List<ParallelGateway> gates = new ArrayList<>();
    List<DataObjectReference> dataObjects = new ArrayList<>();
    Map<BoundaryEvent, CookingEvent> timerEvents = new HashMap<>();
    List<BoundaryEvent> timers = new ArrayList<>();
    StartEvent startEvent = null;
    EndEvent endEvent = null;
    Process process = null;
    int userTaskHeight = 80;
    int userTaskWidth = 150;
    int i = 0;

    private String fileName = "test";
    /*
    If a node has more than one children we need a parallel gate. So maybe we could call a method that creates everything starting from there (the gate) on?
    We give the method the "subtree" and create the BPMN Model from there on and return and append it maybe.
     */
    private int doHeight = 60;
    private int doWidth = 36;

    public void createBpmn(Recipe recipe) {
        convertToProcess(recipe);
    }

    private BpmnModelInstance convertToProcess(Recipe recipe) {

        Tree<Step> t = new RecipeToTreeConverter().createTree(recipe);
        List<Node<Step>> nodes = new TreeTraverser<Step>(t).preOrder();
        /** INITIALIZATION OF IMPORTANT BPMN DEFINTIONS START HERE */
        modelInstance = Bpmn.createEmptyModel();
        Definitions definitions = modelInstance.newInstance(Definitions.class);
        definitions.setTargetNamespace("http://ai4.uni-bayreuth.de/master-project/converting-online-recipes-to-process-models/");
        modelInstance.setDefinitions(definitions);

        // create the process
        process = modelInstance.newInstance(Process.class);
        process.setAttributeValue("id", "process-one-task", true);
        definitions.addChildElement(process);

        BpmnDiagram diagram = modelInstance.newInstance(BpmnDiagram.class);
        BpmnPlane plane = modelInstance.newInstance(BpmnPlane.class);
        plane.setBpmnElement(process);
        diagram.setBpmnPlane(plane);
        /** INITIALIZATION END **/

        startEvent = createElement(process, "start", "Start", StartEvent.class, plane, 50, 50, true);
        //First we create the user tasks for all nodes.
        createUserTasks(nodes, process, plane);

        // We connect every node to every child node.
        // In case there are more than 1 children, we put a parallel gateway in between.
        createConnectionToChildren(nodes, process, plane);

        // The first node is an empty root node. Every children from there has to be connected to the start node.
        createStartEventToConnections(t, startEvent, process, plane);

        endEvent = createElement(process, "end", "Ende", EndEvent.class, plane, 50, 50, false);

        //Every node without children belongs to the endEvent
        createNodeToEndEventConnection(nodes, endEvent, process, plane);

        // Create a sync gatter at the end
        createSynchronisationGatter(process, plane);
        // validate and write model to file

        Bpmn.validateModel(modelInstance);
        createXml();

        this.setFileName(this.fileName + "_layouted");
        definitions.addChildElement(diagram);
        BPMNLayouter layouter = new BPMNLayouter(this, modelInstance);
        layouter.layout();


        // validate and write model to file
        Bpmn.validateModel(modelInstance);
        createXml();


        return modelInstance;

    }

    /*
    Creates synchronisation gatter for the parallel gateways.
     */
    private void createSynchronisationGatter(Process process, BpmnPlane plane) {
        for (UserTask userTask : userTasks) {
            Collection<SequenceFlow> incomming = userTask.getIncoming();
            if (incomming.size() < 2) {
                continue;
            }
            StringBuilder id = new StringBuilder();
            for (SequenceFlow sequenceFlow : incomming) {
                id.append(createIdOf(sequenceFlow.getAttributeValue("id")));
            }
            ParallelGateway parallelGateway = createElement(process, "sync_" + id.toString(), "", ParallelGateway.class, plane, 30, 30, false);
            gates.add(parallelGateway);

            for (SequenceFlow sequenceFlow : incomming) {
                sequenceFlow.setTarget(parallelGateway);
                parallelGateway.getIncoming().add(sequenceFlow);
            }
            userTask.getIncoming().clear();
            SequenceFlow sequenceFlow = createSequenceFlow(process, parallelGateway, userTask, plane);
            flows.add(sequenceFlow);
        }


        if (endEvent.getIncoming().size() > 1) {
            Collection<SequenceFlow> incomming = endEvent.getIncoming();
            StringBuilder id = new StringBuilder();
            for (SequenceFlow sequenceFlow : incomming) {
                id.append(sequenceFlow.getAttributeValue("id"));
            }
            ParallelGateway parallelGateway = createElement(process, "sync_" + id.toString(), "", ParallelGateway.class, plane, 0, 0, false);
            gates.add(parallelGateway);

            for (SequenceFlow sequenceFlow : incomming) {
                sequenceFlow.setTarget(parallelGateway);
                parallelGateway.getIncoming().add(sequenceFlow);
            }
            endEvent.getIncoming().clear();
            SequenceFlow sequenceFlow = createSequenceFlow(process, parallelGateway, endEvent, plane);
            flows.add(sequenceFlow);
        }
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
                        flows.add(createSequenceFlow(process, getUserTaskTo(node), endEvent, plane));
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
            // in case the root node has more than one child we use a parallelgateway on the start. this will be initialized here.
            ParallelGateway startParallel = createElement(process, "parallel_gateway_start", "", ParallelGateway.class, plane, 30, 30, false);
            gates.add(startParallel);

            flows.add(createSequenceFlow(process, startEvent, startParallel, plane));

            for (Node<Step> node : t.getRoot().getChildren()) {

                //Every child will have a connecton from the gateway to the node.
                System.out.println("Creating connection from ParallelGateway to " + node.getData().getText());
                flows.add(createSequenceFlow(process, startParallel, getUserTaskTo(node), plane));
            }
        } else {
            //if root has only one child, we will connect start with it. no parallelismn here.
            flows.add(createSequenceFlow(process, startEvent, getUserTaskTo(t.getRoot().getChildren().get(0)), plane)); //end y

        }

    }

    /*
    Creates connection to children. Every parent is connected to every children. If there are more than one children we need a gateway in between.
     */
    private void createConnectionToChildren(List<Node<Step>> nodes, Process process, BpmnPlane plane) {

        for (Node<Step> node :
                nodes) {

            if (node.getData().getText() == null) {
                continue;
            }
            UserTask from = getUserTaskTo(node);

            if (node.getChildren().size() == 1) {
                //If we have only one child we can connect it directly.
                UserTask to = getUserTaskTo(node.getChildren().get(0));

                if (!sequenceExists(createId(from, to))) {

                    flows.add(createSequenceFlow(process, from, to, plane));
                }
            } else if (node.getChildren().size() > 1) {
                ParallelGateway parallelGateway = null;
                if (!gateExists("parallel_gateway_" + createIdOf(node.getData().getText()))) {
                    //  System.out.println("Creating a parallel gateway for" + node.getData().getText());
                    parallelGateway = createElement(process, "parallel_gateway_" + createIdOf(node.getData().getText()), "", ParallelGateway.class, plane, 30, 30, false);
                    gates.add(parallelGateway);
                    // incXby(150);
                    // First we need to connect the parent to the parallel gateway.
                    if (!sequenceExists(createId(from, parallelGateway))) {
                        flows.add(createSequenceFlow(process, from, parallelGateway, plane));
                    }
                    //Now we create a connection from the gateway to every child
                    for (Node<Step> childNode :
                            node.getChildren()) {

                        UserTask to = getUserTaskTo(childNode);
                        //System.out.println("From: " + from.getAttributeValue("name") + " to: " + to.getAttributeValue("name"));
                        System.err.println("From: " + node.getData().getText() + "(Products)" + node.getData().getProducts().toString() + " + \n + (Ingredients) " + node.getData().getIngredients().toString() + "\n" +
                                ", to: " + childNode.getData().getText() + " (Products)" + childNode.getData().getProducts().toString() + "(Ingredients)" + childNode.getData().getIngredients().toString());
                        //     to.getDiagramElement().getBounds().setY(to.getDiagramElement().getBounds().getY()+i*150);
                        i++;
                        if (!sequenceExists(createId(from, to))) {
                            flows.add(createSequenceFlow(process, parallelGateway, to, plane));

                        }

                    }
                }


            }
        }

    }

    /*
    Create all user tasks.
     */
    private void createUserTasks(List<Node<Step>> nodes, Process process, BpmnPlane plane) {

        for (Node<Step> node : nodes) {
            if (node.getData().getText() == null) {
                continue; // this is especially for the root node who is empty.
            }

            if (!idExists(createIdOf(node.getData().getText()))) { //Avoid duplicates by having more than one dependence which creates two parts in the tree.

                UserTask userTask = createElement(process, createIdOf(node.getData().getText()), node.getData().getText(), UserTask.class, plane, userTaskHeight, userTaskWidth, false);
                List<CookingEvent> events = node.getData().getEvents();

                for (CookingEvent event : events) {
                    BoundaryEvent boundaryEvent = createElement(process, createIdOf("timer_" + event.getText()), event.getText(), BoundaryEvent.class, plane, 30, 30, true);
                    boundaryEvent.setAttachedTo(userTask);
                    timerEvents.put(boundaryEvent, event);
                    timers.add(boundaryEvent);
                }

                /* Iterate over the Input parameter ( = ingredients) and output parameter ( = products) and add them */
                int i = 0;
                for (Ingredient ingredient : node.getData().getIngredients()) {
                    //userTask.builder().camundaInputParameter("Ingredient", ingredient.getName());
                    DataObjectReference dor = createDataObject(process, createIdOf("dataObject_I" + i + createIdOf(ingredient.getCompleteName()) + createIdOf(node.getData().getText())), ingredient.getCompleteName(), plane, true);
                    dataObjects.add(dor);
                    DataInputAssociation dia = createDataAssociation(process, dor, userTask, plane);
                    i++;
                }

                /* Add tools as input parameter */
                for (Tool tool : node.getData().getTools()) {
                    userTask.builder().camundaInputParameter("Tool", tool.getName());
                    DataObjectReference dor = createDataObject(process, createIdOf("dataObject_I" + i + createIdOf(tool.getName()) + createIdOf(node.getData().getText())), tool.getName(), plane, true);
                    dataObjects.add(dor);
                    DataInputAssociation dia = createDataAssociation(process, dor, userTask, plane);
                }
                userTasks.add(userTask);

            }
        }

    }


    private DataInputAssociation createDataAssociation(Process process, DataObjectReference dataObjectReference, UserTask userTask, BpmnPlane plane) {
        String identifier = dataObjectReference.getId() + "-" + userTask.getId();

        DataInputAssociation dataInputAssociation = modelInstance.newInstance(DataInputAssociation.class);
        dataInputAssociation.setAttributeValue("id", identifier, true);

        TargetRef targetRef = modelInstance.newInstance(TargetRef.class);
        targetRef.setTextContent(userTask.getId());

        SourceRef sourceRef = modelInstance.newInstance(SourceRef.class);
        sourceRef.setTextContent(dataObjectReference.getId());

        dataInputAssociation.addChildElement(targetRef);
        dataInputAssociation.addChildElement(sourceRef);
        userTask.getDataInputAssociations().add(dataInputAssociation);

        // TODO Set DataInpuTassociation tasks in Layouter
        return dataInputAssociation;
    }

    /*
    Returns the by default created id for flows to check for their existence already because we cannot add dupplicates.
     */
    @NotNull
    private String createId(FlowNode from, FlowNode to) {
        return from.getId() + "-" + to.getId();
    }

    /*
    Returns true, if a given id of a gateway already exists. False otherwise
     */
    private boolean gateExists(String id) {
        for (ParallelGateway parallel :
                gates) {
            if (parallel.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }


    /*
    Creates a dataObject.
     */

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

    private DataObjectReference createDataObject(BpmnModelElementInstance bpmnModelElementInstance, String id, String name, BpmnPlane plane, boolean withLabel) {
        DataObjectReference dataObject = modelInstance.newInstance(DataObjectReference.class);
        dataObject.setAttributeValue("id", id, true);
        dataObject.setAttributeValue("name", name, false);
        bpmnModelElementInstance.addChildElement(dataObject);

        BpmnShape bpmnShape = modelInstance.newInstance(BpmnShape.class);
        bpmnShape.setBpmnElement((BaseElement) dataObject);


        Bounds bounds = modelInstance.newInstance(Bounds.class);
        bounds.setX(0);
        bounds.setY(0);
        bounds.setHeight(doHeight);
        bounds.setWidth(doWidth);
        bpmnShape.setBounds(bounds);

        if (withLabel) {
            BpmnLabel bpmnLabel = modelInstance.newInstance(BpmnLabel.class);
            Bounds labelBounds = modelInstance.newInstance(Bounds.class);
            labelBounds.setX(0);
            labelBounds.setY(0);
            labelBounds.setHeight(21);
            labelBounds.setWidth(37);
            bpmnLabel.addChildElement(labelBounds);
            bpmnShape.addChildElement(bpmnLabel);
        }
        plane.addChildElement(bpmnShape);


        return dataObject;
    }

    /*
    Creates a BPMN Element
     */
    private <T extends BpmnModelElementInstance> T createElement(BpmnModelElementInstance parentElement,
                                                                 String id, String name, Class<T> elementClass, BpmnPlane plane,
                                                                 double heigth, double width, boolean withLabel) {
        T element = modelInstance.newInstance(elementClass);
        element.setAttributeValue("id", id, true);
        element.setAttributeValue("name", name, false);
        parentElement.addChildElement(element);
        BpmnShape bpmnShape = modelInstance.newInstance(BpmnShape.class);
        bpmnShape.setBpmnElement((BaseElement) element);

        Bounds bounds = modelInstance.newInstance(Bounds.class);
        bounds.setX(0);
        bounds.setY(0);
        bounds.setHeight(heigth);
        bounds.setWidth(width);
        bpmnShape.setBounds(bounds);

        if (withLabel) {
            BpmnLabel bpmnLabel = modelInstance.newInstance(BpmnLabel.class);
            Bounds labelBounds = modelInstance.newInstance(Bounds.class);
            labelBounds.setX(0);
            labelBounds.setY(0);
            labelBounds.setHeight(heigth);
            labelBounds.setWidth(width);
            bpmnLabel.addChildElement(labelBounds);
            bpmnShape.addChildElement(bpmnLabel);
        }
        plane.addChildElement(bpmnShape);


        return element;
    }

    private SequenceFlow createSequenceFlow(org.camunda.bpm.model.bpmn.instance.Process process, FlowNode from, FlowNode to, BpmnPlane plane) {
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

        //Add two fake waypoints to be able to change them later. If you don't add them, there will be an error because a sequenceflow needs at least2.
        Waypoint wp = modelInstance.newInstance(Waypoint.class);
        wp.setX(0);
        wp.setY(0);
        bpmnEdge.addChildElement(wp);
        Waypoint wp2 = modelInstance.newInstance(Waypoint.class);
        wp.setX(1);
        wp.setY(1);
        bpmnEdge.addChildElement(wp2);

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
    private String createIdOf(String s) {
        s = s.replace("ä", "ae");
        s = s.replace("ö", "oe");
        s = s.replace("ü", "ue");
        s = s.replace(",", "");
        s = s.replace(".", "");
        s = s.replace("/", "_durch_");
        s = s.replace("°", "_Grad_");
        s = s.replace(":", "_");
        s = s.replace("(", "_");
        s = s.replace(")", "_");
        //Comment to make it changed..
        return s.replace(" ", "_");
    }

    /**
     * Creates a XML-Instance of the given model
     *
     * @return the model in bpmn xml format
     */
    public void createXml() {
        Bpmn.validateModel(modelInstance);
        System.out.println("Writing to: " + fileName);
        XMLWriter xmlWriter = new XMLWriter(fileName);
        xmlWriter.writeTo(Bpmn.convertToString(modelInstance));
    }

    public void setFileName(String name) {
        this.fileName = name;
    }
}
