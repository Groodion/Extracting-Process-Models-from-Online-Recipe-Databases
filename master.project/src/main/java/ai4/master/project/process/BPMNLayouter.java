package ai4.master.project.process;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.ProcessType;
import org.camunda.bpm.model.bpmn.builder.ProcessBuilder;
import org.camunda.bpm.model.bpmn.instance.*;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnDiagram;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnShape;
import org.camunda.bpm.model.bpmn.instance.di.DiagramElement;
import org.camunda.bpm.model.bpmn.instance.di.Waypoint;
import org.camunda.bpm.model.xml.ModelInstance;
import org.camunda.bpm.model.xml.instance.DomElement;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;
import org.camunda.bpm.model.xml.type.ModelElementType;
import org.intellij.lang.annotations.Flow;

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

    public BPMNLayouter(ProcessModelerImpl processModeler, BpmnModelInstance modelInstance){
        this.processModeler = processModeler;
        this.modelInstance = modelInstance;
    }



    public void layout(){
        clearBoundaries();
        parse();
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
            flow.getDiagramElement().getWaypoints().add(createWaypoint(origin, FlowDirection.Source));

            FlowNode target = flow.getTarget();
            commingFlows.addAll(target.getOutgoing());
            BpmnShape targetShape = (BpmnShape) target.getDiagramElement();
            targetShape.getBounds().setX(currentX);
            targetShape.getBounds().setY(currentY);
            currentX += 200;
            flow.getDiagramElement().getWaypoints().add(createWaypoint(target, FlowDirection.Target));
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

            flow.getDiagramElement().getWaypoints().add(createWaypoint(flow.getSource(),FlowDirection.Source));

            FlowNode target = flow.getTarget();
           commingFlows.get(i).addAll(target.getOutgoing());
            i++;
            BpmnShape targetShape = (BpmnShape) target.getDiagramElement();
            targetShape.getBounds().setX(currentX);
            targetShape.getBounds().setY(currentY);
            currentY += 200;
            flow.getDiagramElement().getWaypoints().add(createWaypoint(target, FlowDirection.Target));
        }
        currentY = 250;
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




    enum FlowDirection{Source, Target};
    enum Instance{StartEvent, EndEvenrt, ParallelGateway, UserTask, BoundaryTask, Undefined};


    private Waypoint createWaypoint(FlowNode origin, FlowDirection direction){
        double shapeHeight = 1;
        double shapeWidth = 1;
        BpmnShape originShape = (BpmnShape) origin.getDiagramElement();
        shapeHeight = originShape.getBounds().getHeight();
        shapeWidth = originShape.getBounds().getWidth();
        Waypoint w = modelInstance.newInstance(Waypoint.class);
        w.setY(originShape.getBounds().getY() + shapeHeight/2);
        w.setX(originShape.getBounds().getX() + shapeWidth/2);

        return w;
    }

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
    }

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
        System.out.println("Clearing boundaries");
        for(SequenceFlow flow : processModeler.flows){
            //flow.getDiagramElement().getWaypoints().clear();
        }
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
}
