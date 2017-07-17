package ai4.master.project.process;

import ai4.master.project.recipe.CookingEvent;
import ai4.master.project.recipe.Position;
import org.camunda.bpm.model.bpmn.instance.*;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnLabel;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnShape;
import org.camunda.bpm.model.bpmn.instance.di.Waypoint;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by René Bärnreuther on 11.07.2017.
 *
 * Creates a simple, but at least a little bit readable layout for the BPMN-File
 */
public class BPMNLayouter {

    private ProcessModelerImpl processModeler;

    private int currentX = 0;
    private int currentY = 250;

    private int incrementX = 300;
    private int baseY = 250;

    /*
    Creates a instance of the BPMNLayouter
     */
    public BPMNLayouter(ProcessModelerImpl modeler){this.processModeler = modeler;}

    /*
    Layouts the given modelInstance.
     */
    public void layout(){
        setDataObjects();
        parse();
        layoutBoundaryEvents();
        connectSyncGates();
    }

    /*
    Connects every relevant node to the fitting synchronisation gate
     */
    private void connectSyncGates(){
        for(ParallelGateway gateway : processModeler.getGates()){
            if(!gateway.getId().substring(0,4).equals("sync")){continue;}

            Collection<SequenceFlow> incomming = gateway.getIncoming();
            System.out.println("Incomming size for sync gateway: " + incomming.size());
            double maxX = gateway.getDiagramElement().getBounds().getX(); //cannot be smaller..
            for(SequenceFlow in : incomming){
                BpmnShape taskShape = (BpmnShape) in.getSource().getDiagramElement();
                System.out.println("Current shape x: " + taskShape.getBounds().getX());
                if(taskShape.getBounds().getX() > maxX){
                    maxX = taskShape.getBounds().getX();
                }
                in.getDiagramElement().getWaypoints().add(createWaypoint(in.getTarget()));
            }
            System.out.println("Setting from: " + gateway.getDiagramElement().getBounds().getX() + " to " + maxX);
            // Doesn't behave like intended. First focus on the other important aspects
            //gateway.getDiagramElement().getBounds().setX(maxX);


        }
    }

    /*
    Puts the Boundary Events (mainly Timer-Events) on the right position depending on the event itself.
     */
    private void layoutBoundaryEvents(){
        for(BoundaryEvent timer : processModeler.getTimers()){
            CookingEvent cookingEvent = processModeler.getTimerEvents().get(timer);
            UserTask attachedTo = (UserTask) timer.getAttachedTo();
            //Size of boundary events is 30,30
            if(cookingEvent.getPos() == Position.BEFORE){
                timer.getDiagramElement().getBounds().setX(attachedTo.getDiagramElement().getBounds().getX()-30);
                timer.getDiagramElement().getBounds().setY(attachedTo.getDiagramElement().getBounds().getY() + attachedTo.getDiagramElement().getBounds().getHeight()/2-30);
            }else if(cookingEvent.getPos() == Position.AFTER){
                timer.getDiagramElement().getBounds().setX(attachedTo.getDiagramElement().getBounds().getX()+ attachedTo.getDiagramElement().getBounds().getWidth());
                timer.getDiagramElement().getBounds().setY(attachedTo.getDiagramElement().getBounds().getY() + attachedTo.getDiagramElement().getBounds().getHeight()/2-30);
            }

            BpmnLabel label = timer.getDiagramElement().getBpmnLabel();
            if(label != null){
                label.getBounds().setX(timer.getDiagramElement().getBounds().getX()+timer.getDiagramElement().getBounds().getHeight());
                label.getBounds().setY(timer.getDiagramElement().getBounds().getY() + timer.getDiagramElement().getBounds().getHeight());
            }
        }
    }

   /*
   Sets the DataObjects, currently only orderd on top.
    */
    private void setDataObjects(){
        int oldX = currentX;
        for(DataObjectReference dataObjectReference : processModeler.getDataObjects()){
            BpmnShape bpmnShape = (BpmnShape) dataObjectReference.getDiagramElement();
            bpmnShape.getBounds().setX(currentX);
            bpmnShape.getBounds().setY(0);

            BpmnLabel label = bpmnShape.getBpmnLabel();
            label.getBounds().setX(currentX);
            label.getBounds().setY(bpmnShape.getBounds().getHeight()+20);


            currentX += incrementX;

        }

        currentX = oldX;
    }

   /*
   Main Layout algorithm which goes recursive through all relevant nodes till the end.
    */
    private void parse(){
            processModeler.getStartEvent().getDiagramElement().getBounds().setX(currentX);
            processModeler.getStartEvent().getDiagramElement().getBounds().setY(currentY);

            currentX += incrementX;
            setLayout(processModeler.getStartEvent(), processModeler.getStartEvent().getOutgoing());
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
            currentX += incrementX;
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
            currentY += incrementX;
            flow.getDiagramElement().getWaypoints().add(createWaypoint(target));
        }
        currentY = baseY;
        currentX += incrementX;
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

    /*
    Creates a Waypoint for the node.
     */
    private Waypoint createWaypoint(FlowNode origin){
        double shapeHeight = 1;
        double shapeWidth = 1;
        BpmnShape originShape = (BpmnShape) origin.getDiagramElement();
        shapeHeight = originShape.getBounds().getHeight();
        shapeWidth = originShape.getBounds().getWidth();
        Waypoint w = processModeler.getModelInstance().newInstance(Waypoint.class);
        w.setY(originShape.getBounds().getY() + shapeHeight/2);
        w.setX(originShape.getBounds().getX() + shapeWidth/2);

        return w;
    };

}
