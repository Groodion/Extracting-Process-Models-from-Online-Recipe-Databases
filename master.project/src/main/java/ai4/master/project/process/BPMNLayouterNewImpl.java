package ai4.master.project.process;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.camunda.bpm.model.bpmn.instance.BoundaryEvent;
import org.camunda.bpm.model.bpmn.instance.DataInputAssociation;
import org.camunda.bpm.model.bpmn.instance.DataObjectReference;
import org.camunda.bpm.model.bpmn.instance.DataOutputAssociation;
import org.camunda.bpm.model.bpmn.instance.EndEvent;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.ItemAwareElement;
import org.camunda.bpm.model.bpmn.instance.ParallelGateway;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.camunda.bpm.model.bpmn.instance.StartEvent;
import org.camunda.bpm.model.bpmn.instance.UserTask;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnEdge;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnLabel;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnShape;
import org.camunda.bpm.model.bpmn.instance.dc.Bounds;
import org.camunda.bpm.model.bpmn.instance.di.Waypoint;

public class BPMNLayouterNewImpl implements BPMNLayouter {
	@Override
	public void layout(ProcessModelerImpl modeler) {
		Element.reset();
		
		Element startElement = new Element(modeler.getStartEvent(), modeler.getTimers());
		startElement.sortLanes();
		startElement.calcSize();
		startElement.calcLocation();
		startElement.calcConnections(modeler);
		
		startElement.moveDown();
	}
}
class Element {
	private static Map<String, Element> syncGates = new HashMap<String, Element>();
	private static Map<Integer, Integer> levelWidths = new HashMap<Integer, Integer>();

	private static int minY = 0;
	
	private static final int DEFAULT_USERTASK_WIDTH = 150;
	private static final int DEFAULT_USERTASK_HEIGHT = 100;
	
	private static final int DEFAULT_ELEMENT_SPACING_WIDTH = 100;
	private static final int DEFAULT_ELEMENT_SPACING_HEIGHT = 50;
	
	private static final int DEFAULT_DATA_OBJECT_WIDTH = 36;
	private static final int DEFAULT_DATA_OBJECT_HEIGHT = 50;
	private static final int DEFAULT_BOUNDRY_EVENT_WIDTH = 36;
	private static final int DEFAULT_BOUNDRY_EVENT_HEIGHT = 36;
	private static final int DEFAULT_PARALLEL_GATEWAY_WIDTH = 50;
	private static final int DEFAULT_PARALLEL_GATEWAY_HEIGHT = 50;
	private static final int DEFAULT_START_EVENT_WIDTH = 50;
	private static final int DEFAULT_START_EVENT_HEIGHT = 50;
	private static final int DEFAULT_END_EVENT_WIDTH = 50;
	private static final int DEFAULT_END_EVENT_HEIGHT = 50;
	private static final int DEFAULT_LABEL_HEIGHT = 12;
	private static final int MAX_LABEL_WIDTH = 60;
	private static final int DEFAULT_DATA_OBJECT_SPACING_WIDTH = 10;
	private static final int DEFAULT_DATA_OBJECT_SPACING_HEIGHT = 30;
	private static final int DEFAULT_X_OFFSET = 50;
	private static final int DEFAULT_Y_OFFSET = 50;
	
	
	private FlowNode node;
	private Bounds bounds;

	private List<DataObjectReference> inputDataObjectReferences;
	private List<DataObjectReference> outputDataObjectReferences;
	
	private List<Element> next;
	private List<Element> prev;
	
	private int x, y;
	private int width, height;
	private int overflow;
	
	private int level;
	private boolean movedDown = false;

	private BoundaryEvent timer;
	
	private int syncLevel = -1;
	
	
	public Element(FlowNode node, List<BoundaryEvent> timer) {
		this.node = node;
		bounds = ((BpmnShape) node.getDiagramElement()).getBounds();
		
		next = new ArrayList<Element>();
		prev = new ArrayList<Element>();
		inputDataObjectReferences = new ArrayList<DataObjectReference>();
		outputDataObjectReferences = new ArrayList<DataObjectReference>();
		
		for(SequenceFlow sFlow : node.getOutgoing()) {
			Element e = null;
			if(sFlow.getTarget() instanceof ParallelGateway && sFlow.getTarget().getOutgoing().size() == 1) {
				if(syncGates.containsKey(sFlow.getTarget().getId())) {
					e = syncGates.get(sFlow.getTarget().getId());
				} else {
					e = new Element(sFlow.getTarget(), timer);
					syncGates.put(sFlow.getTarget().getId(), e);
				}
			} else {
				e = new Element(sFlow.getTarget(), timer);
			}
			
			e.setLevel(level + 1);
			next.add(e);
			e.prev.add(this);
			e.inputDataObjectReferences.removeAll(outputDataObjectReferences);
		}
		
		if(node instanceof UserTask) {
			for(BoundaryEvent t : timer) {
				if(t.getAttachedTo() == node) {
					this.timer = t;
					break;
				}
			}
			
			for(DataInputAssociation dia : ((UserTask) node).getDataInputAssociations()) {
				for(ItemAwareElement iae : dia.getSources()) {
					inputDataObjectReferences.add((DataObjectReference) iae);
				}
			}
			for(DataOutputAssociation doa : ((UserTask) node).getDataOutputAssociations()) {
				for(ItemAwareElement iae : doa.getSources()) {
					outputDataObjectReferences.add((DataObjectReference) iae);
				}
			}
		}
	}
	public void setLevel(int level) {
		if(this.level < level) {
			this.level = level;
			
			for(Element child : next) {
				child.setLevel(level + 1);
			}
		}
	}
	
	public int getRealHeight() {
		if(inputDataObjectReferences.isEmpty()) {
			return height;
		} else {
			return height + DEFAULT_DATA_OBJECT_SPACING_HEIGHT + DEFAULT_LABEL_HEIGHT + DEFAULT_DATA_OBJECT_HEIGHT;
		}
	}
	public int getInputX() {
		return x;
	}
	public int getInputY() {
		return y + height/2;
	}
	public int getOutputX() {
		return x + width;
	}
	public int getOutputY() {
		return y + height/2;
	}
	
	public void sortLanes() {
		for(int i = 1; i < next.size(); i++) {
			if(next.get(i - 1).getSyncLevel() > next.get(i).getSyncLevel()) {
				Collections.swap(next, i - 1, i);
				i = Math.max(i - 2, 0);
			}
		}
			
		for(Element child : next) {
			child.sortLanes();
		}
	}
	
	public void calcSize() {
		for(Element e : next) {
			e.calcSize();
		}
		
		if(node instanceof UserTask) {
			int d = inputDataObjectReferences.size();
			
			for(DataObjectReference dO : inputDataObjectReferences) {
				Bounds laBounds = ((BpmnShape) dO.getDiagramElement()).getBpmnLabel().getBounds();
				Bounds doBounds = ((BpmnShape) dO.getDiagramElement()).getBounds();
				laBounds.setWidth(MAX_LABEL_WIDTH);
				doBounds.setWidth(DEFAULT_DATA_OBJECT_WIDTH);
			}
			
			width = Math.max(DEFAULT_USERTASK_WIDTH, (d - 1) * DEFAULT_DATA_OBJECT_SPACING_WIDTH + MAX_LABEL_WIDTH * d);
			height = DEFAULT_USERTASK_HEIGHT;
		} else if(node instanceof ParallelGateway) {
			width = DEFAULT_PARALLEL_GATEWAY_WIDTH;
			height = DEFAULT_PARALLEL_GATEWAY_HEIGHT;
		} else if(node instanceof BoundaryEvent) {
			width = DEFAULT_BOUNDRY_EVENT_WIDTH;
			height = DEFAULT_BOUNDRY_EVENT_HEIGHT;
		} else if(node instanceof StartEvent) {
			width = DEFAULT_START_EVENT_WIDTH;
			height = DEFAULT_START_EVENT_HEIGHT;
		} else if(node instanceof EndEvent) {
			width = DEFAULT_END_EVENT_WIDTH;
			height = DEFAULT_END_EVENT_HEIGHT;
		}
		
		bounds.setWidth(width);
		bounds.setHeight(height);
		
		overflow = MAX_LABEL_WIDTH * outputDataObjectReferences.size();
		if(overflow != 0) {
			overflow += (outputDataObjectReferences.size() + 1) * DEFAULT_DATA_OBJECT_SPACING_WIDTH;
		}
		
		if(levelWidths.containsKey(level)) {
			levelWidths.put(level, Math.max(levelWidths.get(level), width + overflow));
		} else {
			levelWidths.put(level, width + overflow);
		}
	}
	public void calcLocation() {
		int offsetX = DEFAULT_X_OFFSET;
		
		for(int i = 0; i < level; i++) {
			offsetX += levelWidths.get(i);
		}
		offsetX += level * DEFAULT_ELEMENT_SPACING_WIDTH;
		
		x = offsetX;
		
		if(node instanceof ParallelGateway && node.getIncoming().size() > 1) {
			int maxHeight = prev.get(prev.size() - 1).y - prev.get(0).y + prev.get(prev.size() - 1).height;
			y = prev.get(0).y + maxHeight / 2 - height / 2;
		} else if(node instanceof EndEvent) {
			y = prev.get(0).getOutputY() - height / 2;
		}
				
		bounds.setX(x);
		bounds.setY(y);
		
		minY = minY > y ? y : minY;
		
		int sx = x;
		for(int i = 0; i < inputDataObjectReferences.size(); i++) {
			Bounds doBounds = ((BpmnShape) inputDataObjectReferences.get(i).getDiagramElement()).getBounds();
			doBounds.setX(sx + (MAX_LABEL_WIDTH - DEFAULT_DATA_OBJECT_WIDTH) / 2);
			doBounds.setY(y - DEFAULT_DATA_OBJECT_SPACING_HEIGHT  - DEFAULT_LABEL_HEIGHT - DEFAULT_DATA_OBJECT_HEIGHT);

			minY = (int) (minY > doBounds.getY() ? doBounds.getY() : minY);

			sx += DEFAULT_DATA_OBJECT_SPACING_WIDTH + MAX_LABEL_WIDTH;
		}
		sx = 0;
		for(int i = 0; i < outputDataObjectReferences.size(); i++) {
			Bounds doBounds = ((BpmnShape) outputDataObjectReferences.get(i).getDiagramElement()).getBounds();
			doBounds.setX(sx + (MAX_LABEL_WIDTH - DEFAULT_DATA_OBJECT_WIDTH) / 2);
			doBounds.setY(getOutputY() - DEFAULT_DATA_OBJECT_SPACING_HEIGHT  - DEFAULT_LABEL_HEIGHT - DEFAULT_DATA_OBJECT_HEIGHT);
			sx += DEFAULT_DATA_OBJECT_SPACING_WIDTH + MAX_LABEL_WIDTH;
		}
		
		calcLabel();
		
		int sy = y;
		if(next.size() > 1) {
			int maxHeight = 0;
			for(Element child : next) {
				maxHeight += child.getRealHeight();
			}
			maxHeight += DEFAULT_ELEMENT_SPACING_HEIGHT * (next.size() - 1);
			sy -= maxHeight / 2 - DEFAULT_USERTASK_HEIGHT / 2;
		} else if(node instanceof ParallelGateway || node instanceof StartEvent) {
			sy -= (next.get(0).height - height) / 2;
		}

		for(int i = 0; i < next.size(); i++) {
			next.get(i).y = sy;
			sy += next.get(i).getRealHeight() + DEFAULT_ELEMENT_SPACING_HEIGHT;
			
			next.get(i).calcLocation();
		}
		
		if(timer != null) {
			Bounds timerBounds = ((BpmnShape) timer.getDiagramElement()).getBounds();
			timerBounds.setX(getOutputX() - timerBounds.getWidth() / 2);
			timerBounds.setY(getOutputY() - timerBounds.getHeight() / 2);
		}
	}

	private void calcLabel() {
		if(node instanceof UserTask) {
			for(DataObjectReference doR : inputDataObjectReferences) {
				Bounds doBounds = ((BpmnShape) doR.getDiagramElement()).getBounds();
				Bounds labelBounds = ((BpmnShape) doR.getDiagramElement()).getBpmnLabel().getBounds();

				labelBounds.setX((doBounds.getWidth() - labelBounds.getWidth()) / 2 + doBounds.getX());
				labelBounds.setY(doBounds.getY() - labelBounds.getHeight());
				
				minY = (int) (minY > labelBounds.getY() ? labelBounds.getY() : minY);
			}
		} else if(node instanceof StartEvent) {
			Bounds labelBounds = ((BpmnShape) node.getDiagramElement()).getBpmnLabel().getBounds();
			
			labelBounds.setX((width - labelBounds.getWidth()) / 2 + x);
			labelBounds.setY(y + height);
		}
	}
	public void calcConnections(ProcessModeler processModeler) {
		SequenceFlow[] flows = node.getOutgoing().toArray(new SequenceFlow[0]);
		for(int i = 0; i < next.size(); i++) {
			SequenceFlow flow = flows[i];
			flow.getDiagramElement().getWaypoints().clear();
			
			Waypoint startPoint = processModeler.getModelInstance().newInstance(Waypoint.class);						
			Waypoint endPoint = processModeler.getModelInstance().newInstance(Waypoint.class);
			
			flow.getDiagramElement().getWaypoints().add(startPoint);
			
			if(flow.getTarget() instanceof ParallelGateway && flow.getTarget().getOutgoing().size() == 1) {
				startPoint.setX(getOutputX());
				startPoint.setY(getOutputY());

				if(getOutputY() == next.get(i).getInputY()) {
					endPoint.setX(next.get(i).getInputX());
					endPoint.setY(next.get(i).getInputY());					
				} else {
					Waypoint midPoint = processModeler.getModelInstance().newInstance(Waypoint.class);
					midPoint.setX(next.get(i).x + next.get(i).width / 2);
					midPoint.setY(getOutputY());
					flow.getDiagramElement().getWaypoints().add(midPoint);

					endPoint.setX(midPoint.getX());
					if(getOutputY() > next.get(i).getInputY()) {
						endPoint.setY(next.get(i).y + next.get(i).height);
					} else {
						endPoint.setY(next.get(i).y);
					}
				}
			} else if(node instanceof ParallelGateway && next.size() > 1) {
				if(getOutputY() == next.get(i).getInputY()) {
					startPoint.setX(getOutputX());
					startPoint.setY(getOutputY());					
				} else if(Math.abs(getOutputY() - next.get(i).getInputY()) < height) {
					startPoint.setX(getOutputX());
					startPoint.setY(getOutputY());
					
					Waypoint midPoint1 = processModeler.getModelInstance().newInstance(Waypoint.class);
					midPoint1.setX((next.get(i).getInputX() - getOutputX()) / 2 + getOutputX());
					midPoint1.setY(getOutputY());
					flow.getDiagramElement().getWaypoints().add(midPoint1);

					Waypoint midPoint2 = processModeler.getModelInstance().newInstance(Waypoint.class);
					midPoint2.setX(midPoint1.getX());
					midPoint2.setY(next.get(i).getInputY());
					flow.getDiagramElement().getWaypoints().add(midPoint2);

				} else {
					startPoint.setX(x + width/2);
					if(getOutputY() > next.get(i).getInputY()) {
						startPoint.setY(y);
					} else {
						startPoint.setY(y + height);
					}
					
					Waypoint midPoint = processModeler.getModelInstance().newInstance(Waypoint.class);
					midPoint.setX(startPoint.getX());
					midPoint.setY(next.get(i).getInputY());
					flow.getDiagramElement().getWaypoints().add(midPoint);
				}
				
				endPoint.setX(next.get(i).getInputX());
				endPoint.setY(next.get(i).getInputY());
			} else {
				startPoint.setX(getOutputX());
				startPoint.setY(getOutputY());
				endPoint.setX(next.get(i).getInputX());
				endPoint.setY(next.get(i).getInputY());
			}
			
			flow.getDiagramElement().getWaypoints().add(endPoint);
		}
		
		if(node instanceof UserTask) {
			int i = 0;
			for(DataInputAssociation dia : ((UserTask) node).getDataInputAssociations()) {
				BpmnEdge edge = dia.getDiagramElement();
				edge.getWaypoints().clear();
				
				Waypoint startPoint = processModeler.getModelInstance().newInstance(Waypoint.class);
				Waypoint endPoint = processModeler.getModelInstance().newInstance(Waypoint.class);
				
				Bounds doBounds = ((BpmnShape) inputDataObjectReferences.get(i).getDiagramElement()).getBounds();
				
				startPoint.setX(doBounds.getX() + doBounds.getWidth() / 2);
				startPoint.setY(doBounds.getY() + doBounds.getHeight());
				endPoint.setX(startPoint.getX());
				endPoint.setY(y);

				edge.getWaypoints().add(startPoint);
				edge.getWaypoints().add(endPoint);
				
				i++;
			}
			i = 0;
			for(DataOutputAssociation doa : ((UserTask) node).getDataOutputAssociations()) {
				BpmnEdge edge = doa.getDiagramElement();
				edge.getWaypoints().clear();
				
				Waypoint startPoint = processModeler.getModelInstance().newInstance(Waypoint.class);
				Waypoint endPoint = processModeler.getModelInstance().newInstance(Waypoint.class);
				
				Bounds doBounds = ((BpmnShape) outputDataObjectReferences.get(i).getDiagramElement()).getBounds();
				
				startPoint.setX(doBounds.getX() + doBounds.getWidth() / 2);
				startPoint.setY(doBounds.getY() + doBounds.getHeight());
				endPoint.setX(startPoint.getX());
				endPoint.setY(getOutputY());

				edge.getWaypoints().add(startPoint);
				edge.getWaypoints().add(endPoint);
				
				i++;
			}

		}
		for(Element child : next) {
			child.calcConnections(processModeler);
		}		
	}
	
	public int getSyncLevel() {
		if(syncLevel == -1) {
			if(node instanceof EndEvent) syncLevel = 1;
			else if(node instanceof ParallelGateway && next.size() == 1) syncLevel = next.get(0).getSyncLevel() + 1;
			else syncLevel = next.get(0).getSyncLevel();
		}
		return syncLevel;
	}

	public void moveDown() {
		if(!movedDown) {
			movedDown = true;
			int dy = -minY + DEFAULT_Y_OFFSET;
			
			bounds.setY(bounds.getY() + dy);
			BpmnLabel label = ((BpmnShape) node.getDiagramElement()).getBpmnLabel();
			if(label != null) {
				label.getBounds().setY(label.getBounds().getY() + dy);
			}
			
			for(SequenceFlow flow : node.getOutgoing()) {
				for(Waypoint wP : flow.getDiagramElement().getWaypoints()) {
					wP.setY(wP.getY() + dy);
				}
			}
			
			if(node instanceof UserTask) {
				for(DataObjectReference dor : inputDataObjectReferences) {
					BpmnShape dorShape = ((BpmnShape) dor.getDiagramElement());
					dorShape.getBounds().setY(dorShape.getBounds().getY() + dy);
					dorShape.getBpmnLabel().getBounds().setY(dorShape.getBpmnLabel().getBounds().getY() + dy);
				}
				for(DataInputAssociation dia :((UserTask) node).getDataInputAssociations()) {
					for(Waypoint wP : dia.getDiagramElement().getWaypoints()) {
						wP.setY(wP.getY() + dy);
					}
				}
			}
			if(timer != null) {
				timer.getDiagramElement().getBounds().setY(timer.getDiagramElement().getBounds().getY() + dy);
			}
			
			for(Element child : next) {
				child.moveDown();
			}
		}
	}
	
	public String toString() {
		StringBuilder sB = new StringBuilder();
		
		for(int i = 0; i < level; i++) {
			sB.append('\t');
		}
		if(level != 0) {
			sB.append(" -> ");
		}
		
		sB.append(node.getClass().getSimpleName());
		sB.append(" (");
		sB.append(level);
		sB.append(",");
		sB.append(next.size());
		sB.append(")");
		for(Element child : next) {
			System.out.println(node.getId());
			sB.append('\n');
			sB.append(child);
		}
		
		return sB.toString();
	}

	public static void reset() {
		syncGates.clear();
		levelWidths.clear();
		minY = 0;
	}
}