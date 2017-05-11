package ai4.master.project;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.Iterator;

/**
 * Created by René Bärnreuther on 04.05.2017.
 * <p>
 * TODO Implement a method for ingredigents, too.
 * TODO Implement a method that creates the recipes itself to make them easier parseable.
 * <p>
 * Source for this:
 * http://www.pebra.net/blog/2012/10/31/how-to-get-recipes-fromchefkoch-dot-de/
 * http://www.pebra.net/blog/2013/03/13/Get-recipes-from-chefkoch.de-using-ruby-Part-2/
 */
public class RecipeGetter {

    private static final String BASE_API_STRING = "http://api.chefkoch.de/api/1.1/api-recipe-search.php?Suchbegriff=";
    private static final String REZEPTE_API_STRING = "http://api.chefkoch.de/api/1.1/api-recipe.php?ID=";

    private static RecipeGetter recipeGetter = null;

    private RecipeGetter() {}

    /**
     * Returns a RecipeGetter. Used to prevent too many API Requests at once by using different instances, but can be done otherwise, too.
     */
    public static RecipeGetter recipeGetterFactory() {
        if (recipeGetter == null) {
            recipeGetter = new RecipeGetter();
        }
        return recipeGetter;
    }

    /**
     * Finds a maximumNumber of recipeIds for the given searchstring using chefkoch api. Will return a list in csv format.
     *
     * @param searchString
     * @param maximumNumber
     * @return a String in the format id1,id2,id3,..,idn. Null, if nothing was found or an error was thrown.
     */
    public String getRecipeIDs(String searchString, int maximumNumber) {
        String[] seperatedSearchInput = searchString.split(" ");
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < seperatedSearchInput.length; i++) {
            stringBuilder.append(seperatedSearchInput[i]);
            stringBuilder.append("%20");
        }
        String response = this.getHttpRequestBody(BASE_API_STRING + stringBuilder.toString() + "i=0&z=1&m=0&o=0&t=&limit=" + String.valueOf(maximumNumber));
        JSONArray resultList = this.getJsonResultList(response);
        Iterator<JSONObject> resultListIterator = resultList.iterator();
        StringBuilder recipeIds = new StringBuilder();
        while (resultListIterator.hasNext()) {
            recipeIds.append(resultListIterator.next().get("RezeptShowID")); //the id to look for the data
            recipeIds.append(",");
        }

        return recipeIds.toString();
    }

    /**
     * Finds the preparation process for a given recipe id and returns it.
     *
     * @param id the id for the recipe.
     * @return the preparation
     */
    public String getRecipePreparation(String id) {
        StringBuilder preparation = new StringBuilder();
        String response = getHttpRequestBody(REZEPTE_API_STRING + id);
        JSONArray resultList = this.getJsonResultList(response);

        Iterator<JSONObject> resultListIterator = resultList.iterator();
        if (!resultListIterator.hasNext()) {
            return null;
        }

        JSONObject firstResult = resultListIterator.next();
        preparation.append(firstResult.get("rezept_zubereitung"));


        return preparation.toString();
    }


    /*
    Returns the JSONArray returnlist. Implemented here to remove redundancy.
     */
    private JSONArray getJsonResultList(String jsonString) {
        try {
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(jsonString);

            JSONArray resultList = (JSONArray) jsonObject.get("result");
            return resultList;
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /*
    Returns the HTML Body to the given URL, expected in JSON Format usually.
    Returns null, if nothing was given back.
     */
    private String getHttpRequestBody(String url) {
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);

        try {
            HttpResponse response = client.execute(request);
            HttpEntity entity = response.getEntity();
            return EntityUtils.toString(entity);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
