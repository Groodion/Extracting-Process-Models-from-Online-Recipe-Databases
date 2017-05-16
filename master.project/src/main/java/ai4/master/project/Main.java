package ai4.master.project;

import ai4.master.project.process.ProcessModelerImpl;
import ai4.master.project.recipe.Recipe;

/**
 * Created by René Bärnreuther on 15.05.2017.
 */
public class Main {

    public static void main(String[] args) {

        ProcessModelerImpl processModeler = new ProcessModelerImpl();
        processModeler.convertToProcess(new Recipe());

    }
}
