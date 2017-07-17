package ai4.master.project.process;

import ai4.master.project.recipe.Recipe;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;


/**
 * Created by René Bärnreuther on 15.05.2017.
 * A modeler that creates a BPMN Model from a recipe has to implement this interface.
 */
public interface ProcessModeler {


    /**
     * Converts a given process to a bpmn model
     * @param recipe The recipe containing the steps annd other informations for convesion
     */
    void createBpmn(Recipe recipe);

    void setFileName(String name);


    String getXml();

    default DoubleProperty getProgress(){
       DoubleProperty d = new SimpleDoubleProperty();
       d.setValue(0.0);
       return d;
    }
}
