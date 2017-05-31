package ai4.master.project.output;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by René Bärnreuther on 31.05.2017.
 */
public class XMLWriter {


    private String fileName;

    public XMLWriter(String fileName){this.fileName = fileName;}

    public XMLWriter writeTo(String input){
        BufferedWriter writer = null;
        try{
            File xml = new File(fileName+ ".bpmn");

            writer = new BufferedWriter(new FileWriter(xml));
            writer.write(input);
        }catch(IOException ex){
            ex.printStackTrace();
        }finally{
            try{
                writer.close();
            }catch(IOException ex){
                ex.printStackTrace();
            }
        }
        return this;
    }

}
