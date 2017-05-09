package recipe;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by René Bärnreuther on 04.05.2017.
 */
//Getter and Setter is via lombook and creates them while compiling. Plugin makes it usable without showing errors.
@Getter
@Setter
public class Recipe {

    private List<String> ingredigents;

    private String preparation;


    public Recipe() {
        this.ingredigents = new ArrayList<String>();
        this.preparation = "";
    }

    /**
     * Add an ingredigent to the list of ingredigents.
     * @param ingredigent ingredigent in Format [name] [qty] [einheit]
     */
    public void addIngredigent(String ingredigent){
        if(ingredigents!=null)
         ingredigents.add(ingredigent);
    }
}
