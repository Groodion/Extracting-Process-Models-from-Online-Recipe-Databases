package ai4.master.project.process;

import org.camunda.bpm.model.bpmn.instance.Event;
import org.camunda.bpm.model.bpmn.instance.Gateway;
import org.camunda.bpm.model.bpmn.instance.UserTask;

/**
 * Created by René Bärnreuther, Michi Bösch on 20.06.2017.
    This class delievers values for the layout design of the bpmn model.

 */

@Deprecated
public class LayoutUtils {


    public static int[] getCenterCoordinates(Event node) {
        if(!ProcessModelerImpl.isForLayout){
        double x = node.getDiagramElement().getBounds().getX();
        double y = node.getDiagramElement().getBounds().getY();

        double height = node.getDiagramElement().getBounds().getHeight();
        double width = node.getDiagramElement().getBounds().getWidth();

        int[] center = new int[2];
        center[0] = (int) (x + (height / 2));
        center[1] = (int) (y + (width / 2));

        return center;}
        return new int[2];
    }


    public static int[] getCenterCoordinates(Gateway node) {
        if(!ProcessModelerImpl.isForLayout) {
            double x = node.getDiagramElement().getBounds().getX();
            double y = node.getDiagramElement().getBounds().getY();

            double height = node.getDiagramElement().getBounds().getHeight();
            double width = node.getDiagramElement().getBounds().getWidth();

            int[] center = new int[2];
            center[0] = (int) (x + (height / 2));
            center[1] = (int) (y + (width / 2));
            return center;
        }
        return new int[2];
    }

    public static int[] getCenterCoordinates(UserTask node) {
        if(!ProcessModelerImpl.isForLayout){
        double x=0,y=0;
             x = node.getDiagramElement().getBounds().getX();
             y = node.getDiagramElement().getBounds().getY();

        double height = node.getDiagramElement().getBounds().getHeight();
        double width = node.getDiagramElement().getBounds().getWidth();

        int[] center = new int[2];
        center[0] = (int) (x + (height / 2));
        center[1] = (int) (y + (width / 2));

        return center;}
        return new int[2];
    }



}
