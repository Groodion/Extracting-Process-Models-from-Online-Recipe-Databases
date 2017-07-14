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
 * Created by René Bärnreuther on 11.07.2017.
 */
public class BPMNLayouter {

    private BpmnModelInstance modelInstance;
    private ProcessModelerImpl processModeler;

    private int currentX = 0;
    private int currentY = 250;

    private int baseY = 250;
    private int baseX = 0;

    public BPMNLayouter(ProcessModelerImpl processModeler, BpmnModelInstance modelInstance){
        this.processModeler = processModeler;
        this.modelInstance = modelInstance;
    }



    public void layout(){
        clearBoundaries();
        setDataObjects();
        setGateSize();
        parse();
        layoutBoundaryEvents();
        connectSyncGates();
    }


    private void connectSyncGates(){
        for(ParallelGateway gateway : processModeler.gates){
            if(!gateway.getId().substring(0,4).equals("sync")){continue;}

            Collection<SequenceFlow> incomming = gateway.getIncoming();
            System.out.println("Incomming size for sync gateway: " + incomming.size());
            for(SequenceFlow in : incomming){
                Waypoint wp = createWaypoint(in.getTarget());
                System.out.println("Point from Sync to: " + wp.getX() + ", " + wp.getY());
                in.getDiagramElement().getWaypoints().add(createWaypoint(in.getTarget()));
            }
        }
    }

    private void setGateSize(){
        for(ParallelGateway gateway : processModeler.gates){
            gateway.getDiagramElement().getBounds().setHeight(30);
            gateway.getDiagramElement().getBounds().setWidth(30);
        }
    }
    private void layoutBoundaryEvents(){
        for(BoundaryEvent timer : processModeler.timers){
            CookingEvent cookingEvent = processModeler.timerEvents.get(timer);
            UserTask attachedTo = (UserTask) timer.getAttachedTo();

            if(cookingEvent.getPos() == Position.BEFORE){
                timer.getDiagramElement().getBounds().setX(attachedTo.getDiagramElement().getBounds().getX());
                timer.getDiagramElement().getBounds().setY(attachedTo.getDiagramElement().getBounds().getY() + attachedTo.getDiagramElement().getBounds().getHeight()/2);
            }else if(cookingEvent.getPos() == Position.AFTER){
                timer.getDiagramElement().getBounds().setX(attachedTo.getDiagramElement().getBounds().getX()+ attachedTo.getDiagramElement().getBounds().getWidth());
                timer.getDiagramElement().getBounds().setY(attachedTo.getDiagramElement().getBounds().getY() + attachedTo.getDiagramElement().getBounds().getHeight()/2);
            }

            BpmnLabel label = timer.getDiagramElement().getBpmnLabel();
            if(label != null){
                label.getBounds().setX(timer.getDiagramElement().getBounds().getX()+timer.getDiagramElement().getBounds().getHeight());
                label.getBounds().setY(timer.getDiagramElement().getBounds().getY() + timer.getDiagramElement().getBounds().getHeight());
            }
        }
    }
    private void setDataObjects(){
        int oldX = currentX;
        for(DataObjectReference dataObjectReference : processModeler.dataObjects){
            BpmnShape bpmnShape = (BpmnShape) dataObjectReference.getDiagramElement();
            bpmnShape.getBounds().setX(currentX);
            bpmnShape.getBounds().setY(0);

            BpmnLabel label = bpmnShape.getBpmnLabel();
            label.getBounds().setX(currentX);
            label.getBounds().setY(bpmnShape.getBounds().getHeight()+20);


            currentX += 200;

        }

        currentX = oldX;
    }
    private void parse(){
            processModeler.startEvent.getDiagramElement().getBounds().setX(currentX);
            processModeler.startEvent.getDiagramElement().getBounds().setY(currentY);

            currentX += 200;
            setLayout(processModeler.startEvent, processModeler.startEvent.getOutgoing());
    }


    private void setLayout(FlowNode origin, Collection<SequenceFlow> flows){
        List<SequenceFlow> commingFlows = new ArrayList<>();
        for(SequenceFlow flow : flows){
            flow.getDiagramElement().getWaypoints().clear();
            flow.getDiagramElement().getWaypoints().add(createWaypoint(origin));

            FlowNode target = flow.getTarget();
            commingFlows.addAll(target.getOutgoing());
            BpmnShape targetShape = (BpmnShape) target.getDiagramElement();
            BpmnShape originShape = (BpmnShape) origin.getDiagramElement();

            targetShape.getBounds().setX(originShape.getBounds().getX()+200);
            targetShape.getBounds().setY(originShape.getBounds().getY());
            BpmnLabel bpmnLabel = targetShape.getBpmnLabel();
            if(bpmnLabel != null) {
                bpmnLabel.getBounds().setX(currentX);
                bpmnLabel.getBounds().setY(currentY);
            }
            currentX += 200;
            flow.getDiagramElement().getWaypoints().add(createWaypoint(target));
        }
        if(commingFlows.size() > 1){
            //Here we are after a parallel gateway
            setParallelLayout(commingFlows);
        }else{
            if(commingFlows.isEmpty()){return;}
            FlowNode newOrigin = commingFlows.get(0).getSource();
            setLayout(newOrigin, commingFlows);
        }
    }

    private void setParallelLayout(Collection<SequenceFlow> flows){
        List<List<SequenceFlow>> commingFlows = new ArrayList<>();
        int i = 0;
        for(SequenceFlow flow : flows){
            commingFlows.add(new ArrayList<>());
            flow.getDiagramElement().getWaypoints().clear();

            flow.getDiagramElement().getWaypoints().add(createWaypoint(flow.getSource()));

            FlowNode target = flow.getTarget();
           commingFlows.get(i).addAll(target.getOutgoing());
            i++;
            BpmnShape targetShape = (BpmnShape) target.getDiagramElement();
            targetShape.getBounds().setX(currentX);
            targetShape.getBounds().setY(currentY);
            BpmnLabel bpmnLabel = targetShape.getBpmnLabel();
            if(bpmnLabel != null) {
                bpmnLabel.getBounds().setX(currentX);
                bpmnLabel.getBounds().setY(currentY);
            }
            currentY += 200;
            flow.getDiagramElement().getWaypoints().add(createWaypoint(target));
        }
        currentY = baseY;
        currentX += 200;
        for(List<SequenceFlow> l : commingFlows){
            if(l.size() > 1){
                // Parallel again
                setParallelLayout(l);
            }else{
                if(l.isEmpty()){return;}
                FlowNode newOrigin = l.get(0).getSource();
                setLayout(newOrigin, l);
            }
        }
    }

    private Waypoint createWaypoint(FlowNode origin){
        double shapeHeight = 1;
        double shapeWidth = 1;
        BpmnShape originShape = (BpmnShape) origin.getDiagramElement();
        shapeHeight = originShape.getBounds().getHeight();
        shapeWidth = originShape.getBounds().getWidth();
        Waypoint w = modelInstance.newInstance(Waypoint.class);
        w.setY(originShape.getBounds().getY() + shapeHeight/2);
        w.setX(originShape.getBounds().getX() + shapeWidth/2);

        return w;
    };

    private Instance flowNodeIs(FlowNode flowNode){
        if(flowNode instanceof StartEvent){
            return Instance.StartEvent;
        }
        if(flowNode instanceof EndEvent){
            return Instance.EndEvenrt;
        }
        if(flowNode instanceof  ParallelGateway){
            return Instance.ParallelGateway;
        }
        if(flowNode instanceof  UserTask){
            return Instance.UserTask;
        }
        if(flowNode instanceof  BoundaryEvent){
            return Instance.BoundaryTask;
        }
        return Instance.Undefined;
    };

    private  List<Waypoint> createWaypoints(UserTask a, UserTask b){
        List<Waypoint> list = new ArrayList<>();
        double aX, aY, bX, bY;
        aX = a.getDiagramElement().getBounds().getX();
        aY = a.getDiagramElement().getBounds().getY();
        bX = b.getDiagramElement().getBounds().getX();
        bY = b.getDiagramElement().getBounds().getY();

        Waypoint waypoint = modelInstance.newInstance(Waypoint.class);
        waypoint.setX(aX);
        waypoint.setY(aY+processModeler.userTaskWidth/2);
        list.add(waypoint);

        Waypoint w2 = modelInstance.newInstance(Waypoint.class);
        w2.setX(bX);
        w2.setY(bY+processModeler.userTaskWidth/2);
        list.add(w2);
        return list;
    }

    private void clearBoundaries(){
        for(UserTask userTask : processModeler.userTasks){
            if(userTask != null){
                 userTask.getDiagramElement().getBounds().setY(900);
                 userTask.getDiagramElement().getBounds().setX(900);
            }
        }

        processModeler.startEvent.getDiagramElement().getBounds().setX(0);
        processModeler.startEvent.getDiagramElement().getBounds().setY(0);

        processModeler.endEvent.getDiagramElement().getBounds().setX(900);
        processModeler.endEvent.getDiagramElement().getBounds().setY(900);

        for(ParallelGateway parallelGateway : processModeler.gates){
            parallelGateway.getDiagramElement().getBounds().setY(900);
            parallelGateway.getDiagramElement().getBounds().setX(900);
        }


    }

enum FlowDirection{Source, Target}
enum Instance{StartEvent, EndEvenrt, ParallelGateway, UserTask, BoundaryTask, Undefined}
}
