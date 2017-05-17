package ai4.master.project.stanfordParser;

import java.util.ArrayList;
import java.util.List;

import ai4.master.project.KeyWordDatabase;
import ai4.master.project.recipe.CookingAction;
import ai4.master.project.recipe.Ingredient;
import ai4.master.project.recipe.Tool;

public class Sentence {

	private List<String> verbs = new ArrayList<String>();
	private List<String> nouns = new ArrayList<String>();
	private String mainVerb;
	
	private String text;
	
	private KeyWordDatabase kwdb;
	
	
	public Sentence(String text, KeyWordDatabase kwdb) {
		this.text = text;
		this.kwdb = kwdb;
	}

	public List<String> getVerbs() {
		return verbs;
	}
	public List<String> getNouns() {
		return nouns;
	}
	public String getText() {
		return text;
	}

	public List<Tool> getTools() {
		List<Tool> tools = new ArrayList<Tool>();
		
		for(String noun : nouns) {
			Tool tool = kwdb.findTool(noun, Parser.ERROR);
			
			if(tool != null) tools.add(tool);
		}
		return tools;
	}
	public List<Ingredient> getIngredients() {
		List<Ingredient> ingredients = new ArrayList<Ingredient>();
		
		for(String noun : nouns) {
			Ingredient ingredient = kwdb.findIngredient(noun, Parser.ERROR);
			
			if(ingredient != null) ingredients.add(ingredient);
		}

		return ingredients;
	}
	public CookingAction getCookingAction() {
		CookingAction cookingAction = kwdb.findCookingAction(mainVerb, Parser.ERROR);
		return cookingAction;
	}
	public Ingredient getMainIngredient() {
		List<Ingredient> ingredients = getIngredients();
		if(ingredients.isEmpty()) return null;
		return ingredients.get(0);
	}

	public void mergeWith(Sentence sentence) {
		nouns.addAll(sentence.getNouns());
		verbs.addAll(sentence.getVerbs());
		text += sentence.text;
		if(mainVerb == null) {
			mainVerb = sentence.mainVerb;
		}
	}

	public boolean containsLastSentenceReference() {
		return kwdb.textContainsLastSentenceReference(text) || getIngredients().isEmpty();
	}

	public void setMainVerb(String mainVerb) {
		this.mainVerb = mainVerb;
	}
	public String getMainVerb() {
		return mainVerb;
	}

	public void setText(String text) {
		this.text = text;
	}
}
