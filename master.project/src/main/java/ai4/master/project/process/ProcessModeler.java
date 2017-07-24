package ai4.master.project.process;

import ai4.master.project.recipe.Recipe;
import javafx.beans.property.DoubleProperty;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;

import java.io.File;


/**
 * A modeler that creates a BPMN Model from a recipe has to implement this interface.
 *
 * We use this interface to be able to (theoretically) use different BPMN-Frameworks.
 */
public interface ProcessModeler {


    /**
     * Converts a given process to a bpmn model
     * @param recipe The recipe containing the steps annd other informations for convesion
     */
    void createBpmn(Recipe recipe, boolean withOutput);

    /**
     * Sets the filename of the file to create
     * @param name
     */
    void setFileName(String name);
    void setFile(File file);


    /**
     *
     * @return the BPMN-Model as a xml-String.
     */
    String getXml();

    /**
     *
     * @return the current progress of the conversation.
     */
     DoubleProperty getProgress();

	BpmnModelInstance getModelInstance();
}
