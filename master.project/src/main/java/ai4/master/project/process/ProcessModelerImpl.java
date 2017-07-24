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
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
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

import java.io.File;
import java.util.*;


public class ProcessModelerImpl implements ProcessModeler {


    private BpmnModelInstance modelInstance;

    private List<UserTask> userTasks = new ArrayList<>();
    private List<SequenceFlow> flows = new ArrayList<>();
    private List<ParallelGateway> gates = new ArrayList<>();
    private List<DataObjectReference> dataObjects = new ArrayList<>();
    private Map<BoundaryEvent, CookingEvent> timerEvents = new HashMap<>();
    private List<BoundaryEvent> timers = new ArrayList<>();
    private List<DataInputAssociation> dataInputAssociations = new ArrayList<>();
    private List<DataOutputAssociation> dataOutputAssociations = new ArrayList<>();
    private Map<DataInputAssociation, UserTask> dataObjectAssoc = new HashMap<>();
    private StartEvent startEvent = null;
    private EndEvent endEvent = null;


    private Process process = null;
    private File file = null;

    /*
    Just for process bar reasions
     */
    private DoubleProperty progress;

    private int userTaskHeight = 80;
    private int userTaskWidth = 150;
    private int i = 0;
    private String fileName = "test";

    /*
    If a node has more than one children we need a parallel gate. So maybe we could call a method that creates everything starting from there (the gate) on?
    We give the method the "subtree" and create the BPMN Model from there on and return and append it maybe.
     */
    private int doHeight = 60;
    private int doWidth = 36;

    public void createBpmn(Recipe recipe) {
        if (progress == null) {
            progress = new SimpleDoubleProperty();
        }

        progress.set(0);
        convertToProcess(recipe);
    }

    /*
    Converts the Recipe to the process
     */
    private BpmnModelInstance convertToProcess(Recipe recipe) {

        Tree<Step> t = new RecipeToTreeConverter().createTree(recipe);
        progress.setValue(0.25);
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

        progress.setValue(0.30);
        startEvent = createElement(process, "start", "Start", StartEvent.class, plane, 50, 50, true);
        //First we create the user tasks for all nodes.
        createUserTasks(nodes, process, plane);
        progress.setValue(0.45);
        // We connect every node to every child node.
        // In case there are more than 1 children, we put a parallel gateway in between.
        createConnectionToChildren(nodes, process, plane);
        progress.setValue(0.60);
        // The first node is an empty root node. Every children from there has to be connected to the start node.
        createStartEventToConnections(t, startEvent, process, plane);
        endEvent = createElement(process, "end", "Ende", EndEvent.class, plane, 50, 50, false);

        //Every node without children belongs to the endEvent
        createNodeToEndEventConnection(nodes, endEvent, process, plane);
        progress.setValue(0.75);
        // Create a sync gatter at the end
        createSynchronisationGatter(process, plane);
        // validate and write model to file

        Bpmn.validateModel(modelInstance);
        this.setFileName("only_bpmn");
        createXml();
        progress.setValue(0.80);
        this.setFileName(this.fileName + "_layouted");
        definitions.addChildElement(diagram);
        BPMNLayouter layouter = new BPMNLayouterNewImpl();
        layouter.layout(this);
        progress.setValue(0.98);

        // validate and write model to file
        //Bpmn.validateModel(modelInstance);
//        createXml();
        createXmlFromFile();
        progress.setValue(1.00);

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
                    BoundaryEvent boundaryEvent = createElement(process, createIdOf("timer_" + event.getText()), "", BoundaryEvent.class, plane, 30, 30, false);
                    boundaryEvent.setAttachedTo(userTask);
                    boundaryEvent.builder().timerWithDuration(event.getText());
                    timerEvents.put(boundaryEvent, event);
                    timers.add(boundaryEvent);
                }

                /* Iterate over the Input parameter ( = ingredients) and output parameter ( = products) and add them */
                int i = 0;
                List<DataObjectReference> used = new ArrayList<>();
                for (Ingredient ingredient : node.getData().getIngredients()) {
                    if (used.contains(ingredient)) {
                        continue;
                    }
                    //userTask.builder().camundaInputParameter("Ingredient", ingredient.getName());
                    System.out.println("Creating dataObject_I" + i + "_" + createIdOf(ingredient.getCompleteName()));
                    DataObjectReference dor = createDataObject(process, createIdOf("dataObject_I" + i + "_" + createIdOf(ingredient.getCompleteName()) + createIdOf(node.getData().getText())), ingredient.getName(), plane, true);
                    dataObjects.add(dor);
                    i++;

                    // TODO: DataOutputAssociation erstellen, wenn ein Ingredient vom vorherigen usertask zum dataObjectReference, wenn Ingredient bei beiden gleich

                    DataInputAssociation dia = createDataInputAssociation(process, dor, userTask, plane);
                    DataOutputAssociation dao = createDataOutputAssociation(process, dor, userTask, plane);
                    //dataOutputAssociations.add(doa);
                    dataInputAssociations.add(dia);
                }

                /* Add tools as input parameter */
                for (Tool tool : node.getData().getTools()) {

                    userTask.builder().camundaInputParameter("Tool", tool.getName());
                    DataObjectReference dor = createDataObject(process, createIdOf("dataObject_I" + i + createIdOf(tool.getName()) + createIdOf(node.getData().getText())), tool.getName(), plane, true);
                    dataObjects.add(dor);
                    DataInputAssociation dia = createDataInputAssociation(process, dor, userTask, plane);
                    dataInputAssociations.add(dia);
                }
                userTasks.add(userTask);

            }
        }

    }

    private DataOutputAssociation createDataOutputAssociation(Process process, DataObjectReference dor, UserTask userTask, BpmnPlane plane) {


        String identifier = dor.getId() + "-" + userTask.getId() + "_prod";
        DataOutputAssociation dataOutputAssociation = modelInstance.newInstance(DataOutputAssociation.class);
        dataOutputAssociation.setAttributeValue("id", identifier, true);


        TargetRef targetRef = modelInstance.newInstance(TargetRef.class);
        targetRef.setTextContent(dor.getId());

        SourceRef sourceRef = modelInstance.newInstance(SourceRef.class);
        sourceRef.setTextContent(userTask.getId());

        dataOutputAssociation.addChildElement(targetRef);
        dataOutputAssociation.addChildElement(sourceRef);
        userTask.getDataOutputAssociations().add(dataOutputAssociation);

        BpmnEdge edge = modelInstance.newInstance(BpmnEdge.class);
        edge.setBpmnElement(dataOutputAssociation);

        Waypoint w1 = modelInstance.newInstance(Waypoint.class);
        w1.setX(0);
        w1.setY(0);
        Waypoint w2 = modelInstance.newInstance(Waypoint.class);
        w2.setX(1);
        w2.setY(1);
        edge.addChildElement(w1);
        edge.addChildElement(w2);

        plane.addChildElement(edge);

        dataOutputAssociations.add(dataOutputAssociation);

        return dataOutputAssociation;
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
      Creates a dataObject.
       */
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
    Creates a DataInputAssociation to a UserTask
     */
    private DataInputAssociation createDataInputAssociation(Process process, DataObjectReference dataObjectReference, UserTask userTask, BpmnPlane plane) {
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

        BpmnEdge edge = modelInstance.newInstance(BpmnEdge.class);
        edge.setBpmnElement(dataInputAssociation);

        Waypoint w1 = modelInstance.newInstance(Waypoint.class);
        w1.setX(0);
        w1.setY(0);
        Waypoint w2 = modelInstance.newInstance(Waypoint.class);
        w2.setX(1);
        w2.setY(1);
        edge.addChildElement(w1);
        edge.addChildElement(w2);
        plane.addChildElement(edge);

        dataObjectAssoc.put(dataInputAssociation, userTask);
        return dataInputAssociation;
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

    /*
    Creates a Sequence Flow
     */
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
     */
    public void createXml() {
        Bpmn.validateModel(modelInstance);
        System.out.println("Writing to: " + file.getAbsolutePath());
        XMLWriter xmlWriter = new XMLWriter(fileName);
        xmlWriter.writeTo(Bpmn.convertToString(modelInstance));
    }

    /*
    Returns the XML
     */
    public String getXml() {
        Bpmn.validateModel(modelInstance);
        return Bpmn.convertToString(modelInstance);
    }

    /**
     * @return the current progress of the conversation.
     */
    @Override
    public DoubleProperty getProgress() {
        if (this.progress == null) {
            progress = new SimpleDoubleProperty();
            progress.setValue(0.0);
        }
        return this.progress;
    }

    /**
     * @param name the filename to be given to the file
     */
    public void setFileName(String name) {
        this.fileName = name;
    }

    /*
    Sets the current file.
     */
    public void setFile(File file) {
        this.file = file;
    }

    /*
    Creates a xml-file from the currently saved file.
     */
    public void createXmlFromFile() {
//      Bpmn.validateModel(modelInstance);
        XMLWriter xmlWriter = new XMLWriter(fileName);
        xmlWriter.writeTo(file, getXml());
    }

    public BpmnModelInstance getModelInstance() {
        return modelInstance;
    }

    public void setModelInstance(BpmnModelInstance modelInstance) {
        this.modelInstance = modelInstance;
    }

    public List<UserTask> getUserTasks() {
        return userTasks;
    }

    public void setUserTasks(List<UserTask> userTasks) {
        this.userTasks = userTasks;
    }

    public List<SequenceFlow> getFlows() {
        return flows;
    }

    public void setFlows(List<SequenceFlow> flows) {
        this.flows = flows;
    }

    public List<ParallelGateway> getGates() {
        return gates;
    }

    public void setGates(List<ParallelGateway> gates) {
        this.gates = gates;
    }

    public List<DataObjectReference> getDataObjects() {
        return dataObjects;
    }

    public void setDataObjects(List<DataObjectReference> dataObjects) {
        this.dataObjects = dataObjects;
    }

    public Map<BoundaryEvent, CookingEvent> getTimerEvents() {
        return timerEvents;
    }

    public void setTimerEvents(Map<BoundaryEvent, CookingEvent> timerEvents) {
        this.timerEvents = timerEvents;
    }

    public List<BoundaryEvent> getTimers() {
        return timers;
    }

    public void setTimers(List<BoundaryEvent> timers) {
        this.timers = timers;
    }

    public List<DataInputAssociation> getDataInputAssociations() {
        return dataInputAssociations;
    }

    public void setDataInputAssociations(List<DataInputAssociation> dataInputAssociations) {
        this.dataInputAssociations = dataInputAssociations;
    }

    public StartEvent getStartEvent() {
        return startEvent;
    }

    public void setStartEvent(StartEvent startEvent) {
        this.startEvent = startEvent;
    }

    public EndEvent getEndEvent() {
        return endEvent;
    }

    public void setEndEvent(EndEvent endEvent) {
        this.endEvent = endEvent;
    }

    public Process getProcess() {
        return process;
    }

    public void setProcess(Process process) {
        this.process = process;
    }

    public File getFile() {
        return file;
    }

    public DoubleProperty progressProperty() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress.set(progress);
    }

    public int getUserTaskHeight() {
        return userTaskHeight;
    }

    public void setUserTaskHeight(int userTaskHeight) {
        this.userTaskHeight = userTaskHeight;
    }

    public int getUserTaskWidth() {
        return userTaskWidth;
    }

    public void setUserTaskWidth(int userTaskWidth) {
        this.userTaskWidth = userTaskWidth;
    }

    public String getFileName() {
        return fileName;
    }

    public int getDoHeight() {
        return doHeight;
    }

    public void setDoHeight(int doHeight) {
        this.doHeight = doHeight;
    }

    public int getDoWidth() {
        return doWidth;
    }

    public void setDoWidth(int doWidth) {
        this.doWidth = doWidth;
    }

    public Map<DataInputAssociation, UserTask> getDataObjectAssoc() {
        return dataObjectAssoc;
    }

    public void setDataObjectAssoc(Map<DataInputAssociation, UserTask> dataObjectAssoc) {
        this.dataObjectAssoc = dataObjectAssoc;
    }
}
