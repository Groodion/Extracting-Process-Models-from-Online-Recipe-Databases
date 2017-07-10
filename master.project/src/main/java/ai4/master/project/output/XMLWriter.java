package ai4.master.project.output;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Created by René Bärnreuther on 31.05.2017.
 */
public class XMLWriter {


    private String fileName;

    public XMLWriter(String fileName){this.fileName = fileName;}

    public XMLWriter writeTo(String input){
        File xml = new File(fileName+ ".bpmn");
        try (BufferedWriter writer = new BufferedWriter( new OutputStreamWriter( new FileOutputStream(xml) , "UTF-8") )) {
			writer.write(input);
		} catch (IOException ex) {
			ex.printStackTrace();
		}

        return this;
    }

}
