package ai4.master.project.process;

import ai4.master.project.recipe.CookingEvent;
import ai4.master.project.recipe.Position;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnLabel;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnShape;
import org.camunda.bpm.model.bpmn.instance.di.Waypoint;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * <p>
 * Creates a simple, but at least a little bit readable layout for the BPMN-File
 */
@Deprecated
public class BPMNLayouterImpl implements BPMNLayouter{

    private BpmnModelInstance modelInstance;
    private ProcessModelerImpl processModeler;

    private int currentX = 0;
    private int currentY = 250;

    private int incrementX = 175;
    private int incrementY = 200;

    private int baseY = 250;
    private int baseX = 0;

    private List<UserTask> usedTasks = new ArrayList<>();

    /*
    Creates a instance of the BPMNLayouter
     */
    public BPMNLayouterImpl() {
    }

    @Override
    public void layout(ProcessModelerImpl modeler) {
        this.processModeler = modeler;
        setDataObjects();
        parse();
        layoutBoundaryEvents();
        connectSyncGates();
        connectDataObjects();
    }

    private void connectDataObjects() {
        int incremental = 0;
        for (DataInputAssociation dataInputAssociation : processModeler.getDataInputAssociations()) {
            DataObjectReference source = (DataObjectReference) dataInputAssociation.getSources().iterator().next();
            UserTask target = processModeler.getDataObjectAssoc().get(dataInputAssociation);

            if (source == null || target == null) {
                continue;
            }
            BpmnShape sourceShape = (BpmnShape) source.getDiagramElement();
            BpmnShape targetShape = (BpmnShape) target.getDiagramElement();

            if (usedTasks.contains(target)) {
                incremental++;
            } else {
                incremental = 0;
            }
            sourceShape.getBounds().setX(targetShape.getBounds().getX() + incremental * 10);
            sourceShape.getBounds().setY(targetShape.getBounds().getY() - sourceShape.getBounds().getHeight() - 10);

            BpmnLabel sourceLabel = ((BpmnShape) source.getDiagramElement()).getBpmnLabel();
            sourceLabel.getBounds().setX(targetShape.getBounds().getX() + sourceShape.getBounds().getWidth() + 75);
            sourceLabel.getBounds().setY(targetShape.getBounds().getY() - sourceShape.getBounds().getHeight() + incremental * 10);


            dataInputAssociation.getDiagramElement().getWaypoints().clear();
            Waypoint srcWp = processModeler.getModelInstance().newInstance(Waypoint.class);
            srcWp.setX(sourceShape.getBounds().getX());
            srcWp.setY(sourceShape.getBounds().getY());

            Waypoint trgWp = processModeler.getModelInstance().newInstance(Waypoint.class);
            trgWp.setX(targetShape.getBounds().getX());
            trgWp.setY(targetShape.getBounds().getY());
            dataInputAssociation.getDiagramElement().getWaypoints().add(srcWp);
            dataInputAssociation.getDiagramElement().getWaypoints().add(trgWp);

            usedTasks.add(target);
        }
    }

    /*
    Connects every relevant node to the fitting synchronisation gate
     */
    private void connectSyncGates() {
        for (ParallelGateway gateway : processModeler.getGates()) {
            if (!gateway.getId().substring(0, 4).equals("sync")) {
                continue;
            }
            gateway.getDiagramElement().getBounds().setHeight(30);
            gateway.getDiagramElement().getBounds().setWidth(30);
            Collection<SequenceFlow> incomming = gateway.getIncoming();
            double maxX = gateway.getDiagramElement().getBounds().getX(); //cannot be smaller..
            for (SequenceFlow in : incomming) {
                BpmnShape taskShape = (BpmnShape) in.getSource().getDiagramElement();
                if (taskShape.getBounds().getX() > maxX) {
                    maxX = taskShape.getBounds().getX();
                }
                in.getDiagramElement().getWaypoints().add(createWaypoint(in.getTarget(), Direction.TO));
            }
        }
    }

    /*
    Puts the Boundary Events (mainly Timer-Events) on the right position depending on the event itself.
     */
    private void layoutBoundaryEvents() {
        for (BoundaryEvent timer : processModeler.getTimers()) {
            CookingEvent cookingEvent = processModeler.getTimerEvents().get(timer);
            UserTask attachedTo = (UserTask) timer.getAttachedTo();

            if (cookingEvent.getPos() == Position.BEFORE) {
                timer.getDiagramElement().getBounds().setX(attachedTo.getDiagramElement().getBounds().getX());
                timer.getDiagramElement().getBounds().setY(attachedTo.getDiagramElement().getBounds().getY() + attachedTo.getDiagramElement().getBounds().getHeight() / 2);
            } else if (cookingEvent.getPos() == Position.AFTER) {
                timer.getDiagramElement().getBounds().setX(attachedTo.getDiagramElement().getBounds().getX() + attachedTo.getDiagramElement().getBounds().getWidth());
                timer.getDiagramElement().getBounds().setY(attachedTo.getDiagramElement().getBounds().getY() + attachedTo.getDiagramElement().getBounds().getHeight() / 2);
            }

            BpmnLabel label = timer.getDiagramElement().getBpmnLabel();
            if (label != null) {
                label.getBounds().setX(timer.getDiagramElement().getBounds().getX() + timer.getDiagramElement().getBounds().getHeight());
                label.getBounds().setY(timer.getDiagramElement().getBounds().getY() + timer.getDiagramElement().getBounds().getHeight());
            }
        }
    }

    private void setDataObjects() {
        int oldX = currentX;
        for (DataObjectReference dataObjectReference : processModeler.getDataObjects()) {
            BpmnShape bpmnShape = (BpmnShape) dataObjectReference.getDiagramElement();
            bpmnShape.getBounds().setX(currentX);
            bpmnShape.getBounds().setY(0);

            BpmnLabel label = bpmnShape.getBpmnLabel();
            label.getBounds().setX(currentX);
            label.getBounds().setY(bpmnShape.getBounds().getHeight() + 20);


            currentX += incrementX;

        }

        currentX = oldX;
    }

    /*
    Main Layout algorithm which goes recursive through all relevant nodes till the end.
     */
    private void parse() {
        processModeler.getStartEvent().getDiagramElement().getBounds().setX(currentX);
        processModeler.getStartEvent().getDiagramElement().getBounds().setY(currentY);

        currentX += incrementX;
        setLayout(processModeler.getStartEvent(), processModeler.getStartEvent().getOutgoing());
    }


    private void setLayout(FlowNode origin, Collection<SequenceFlow> flows) {
        List<SequenceFlow> commingFlows = new ArrayList<>();
        for (SequenceFlow flow : flows) {
            flow.getDiagramElement().getWaypoints().clear();
            flow.getDiagramElement().getWaypoints().add(createWaypoint(origin, Direction.FROM));

            FlowNode target = flow.getTarget();
            commingFlows.addAll(target.getOutgoing());
            BpmnShape targetShape = (BpmnShape) target.getDiagramElement();
            BpmnShape originShape = (BpmnShape) origin.getDiagramElement();

            targetShape.getBounds().setX(originShape.getBounds().getX() + 200);
            targetShape.getBounds().setY(originShape.getBounds().getY());
            BpmnLabel bpmnLabel = targetShape.getBpmnLabel();
            if (bpmnLabel != null) {
                bpmnLabel.getBounds().setX(currentX);
                bpmnLabel.getBounds().setY(currentY);
            }
            currentX += incrementX;
            if (target.getIncoming().size() > 1) {
                flow.getDiagramElement().getWaypoints().add(createWaypoint(target, Direction.DOWN));
            } else {
                flow.getDiagramElement().getWaypoints().add(createWaypoint(target, Direction.TO));
            }
        }
        if (commingFlows.size() > 1) {
            //Here we are after a parallel gateway
            setParallelLayout(commingFlows);
        } else {
            if (commingFlows.isEmpty()) {
                return;
            }
            FlowNode newOrigin = commingFlows.get(0).getSource();
            setLayout(newOrigin, commingFlows);
        }
    }

    private void setParallelLayout(Collection<SequenceFlow> flows) {
        List<List<SequenceFlow>> commingFlows = new ArrayList<>();
        int i = 0;
        for (SequenceFlow flow : flows) {
            commingFlows.add(new ArrayList<>());
            flow.getDiagramElement().getWaypoints().clear();

            flow.getDiagramElement().getWaypoints().add(createWaypoint(flow.getSource(), Direction.FROM));

            FlowNode target = flow.getTarget();
            commingFlows.get(i).addAll(target.getOutgoing());
            i++;
            BpmnShape targetShape = (BpmnShape) target.getDiagramElement();
            targetShape.getBounds().setX(currentX);
            targetShape.getBounds().setY(currentY);
            BpmnLabel bpmnLabel = targetShape.getBpmnLabel();
            if (bpmnLabel != null) {
                bpmnLabel.getBounds().setX(currentX);
                bpmnLabel.getBounds().setY(currentY);
            }
            currentY += incrementY;
            flow.getDiagramElement().getWaypoints().add(createWaypoint(target, Direction.TO));
        }
        currentY = baseY;
        currentX += incrementX;
        for (List<SequenceFlow> l : commingFlows) {
            if (l.size() > 1) {
                // Parallel again
                setParallelLayout(l);
            } else {
                if (l.isEmpty()) {
                    return;
                }
                FlowNode newOrigin = l.get(0).getSource();
                setLayout(newOrigin, l);
            }
        }
    }

    private Waypoint createWaypoint(FlowNode origin, Direction direction) {
        double shapeHeight = 1;
        double shapeWidth = 1;
        BpmnShape originShape = (BpmnShape) origin.getDiagramElement();
        shapeHeight = originShape.getBounds().getHeight();
        shapeWidth = originShape.getBounds().getWidth();
        Waypoint w = processModeler.getModelInstance().newInstance(Waypoint.class);
        w.setY(originShape.getBounds().getY() + shapeHeight / 2);
        w.setX(originShape.getBounds().getX() + shapeWidth / 2);
        if (direction == Direction.TO) {
            w.setY(originShape.getBounds().getY() + shapeHeight / 2);
            w.setX(originShape.getBounds().getX());
        } else if (direction == Direction.FROM) {
            w.setY(originShape.getBounds().getY() + shapeHeight / 2);
            w.setX(originShape.getBounds().getX() + shapeWidth);
        } else {
            w.setY(originShape.getBounds().getY() + shapeHeight);
            w.setX(originShape.getBounds().getX() + shapeWidth / 2);
        }

        return w;
    }

    /*
    Creates a Waypoint for the node.
     */
    enum Direction {
        FROM, TO, DOWN
    }

    ;
}
