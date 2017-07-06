package ai4.master.project.apirequests;

import ai4.master.project.recipe.Recipe;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.ArrayList;


/**s
 * Created by Michael on 08.06.2017.
 */
public class RecipeGetterKochbar implements RecipeGetter {
    public RecipeGetterKochbar(){}

    public String getRecipePreparation(String id, Recipe recipe) {
        String prep = "";
        Document document = setup(id);
        Elements divElemente = document.select("div[class*=recipe-steps-right]");
        for (Element div : divElemente ){
            prep += div.text();
        }
        recipe.setPreparation(prep);
        return recipe.getPreparation();
    }

    public String getRecipeIngredients(String id, Recipe recipe) {
        ArrayList<String> names = new ArrayList<String>();
        ArrayList<String> sizes = new ArrayList<String>();
        Document document = setup(id);
        Elements divElemente = document.select("div[class*=recipe-ingredient]");
        for (Element food : divElemente.select("span[class*=kb-ft-rob-reg]")){
            names.add(food.text());
        }
        for (Element size : divElemente.select("div[class*=rtli-bold rtli-pr-large]")){
            sizes.add(size.text());
        }
        String fullIngredient = "";
        for (int i=0;i<names.size();i++) {
            fullIngredient += names.get(i) + " " + sizes.get(i) + "\n";
        }
        return fullIngredient;
    }

    private Document setup(String link){
        Document doc = null;
        try {
            doc = Jsoup.connect(link).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return doc;
    }

//    public static void main(String args[]){
//        RecipeGetterKochbar r = new RecipeGetterKochbar();
//        Recipe recipe = new Recipe(LANG_FLAG.DE);
//        System.out.println(r.getRecipePreparation("http://www.kochbar.de/rezept/526489/Kichererbsen-Melonen-Suppe.html",recipe));
//        System.out.println(r.getRecipeIngredients("http://www.kochbar.de/rezept/526489/Kichererbsen-Melonen-Suppe.html",recipe));
//
//    }
}
