package ai4.master.project;

import java.util.ArrayList;
import java.util.List;

import ai4.master.project.recipe.CookingAction;
import ai4.master.project.recipe.Ingredient;
import ai4.master.project.recipe.Tool;
import ai4.master.project.stanfordParser.sentence.Role;

public class KeyWordDatabase {
	
	private List<Tool> tools;
	private List<Ingredient> ingredients;
	private List<CookingAction> cookingActions;
	private List<String> partIndicators;
	private List<String> lastSentenceReferences;
	
	public KeyWordDatabase() {
		tools = new ArrayList<Tool>();
		ingredients = new ArrayList<Ingredient>();
		cookingActions = new ArrayList<CookingAction>();
		partIndicators = new ArrayList<String>();
		lastSentenceReferences = new ArrayList<String>();
	}

	public List<Tool> getTools() {
		return tools;
	}
	public List<Ingredient> getIngredients() {
		return ingredients;
	}
	public List<CookingAction> getCookingActions() {
		return cookingActions;
	}

	public List<String> getPartIndicators() {
		return partIndicators;
	}
	public List<String> getLastSentenceReferences() {
		return lastSentenceReferences;
	}
	
	public Tool findTool(String text) {
		return findTool(text, 0d);
	}
	public Tool findTool(String text, double error) {
		if(text == null) return null;
		
		Tool bestMatch = null;
		double e = 1.0;
		
		for(Tool tool : tools) {
			if(tool.getNames().contains(text)) {
				return tool;
			}
			for(String name : tool.getNames()) {
				int diff = stringDiff(name, text);
				double er = 1d * diff / text.length();
				
				if(er <= error && er < e) {
					e = er;
					bestMatch = tool;
				}
			}
		}
		return bestMatch;
	}
	public Ingredient findIngredient(String text) {
		return findIngredient(text, 0d);
	}
	public Ingredient findIngredient(String text, double error) {
		if(text == null) return null;

		Ingredient bestMatch = null;
		double e = 1.0;

		for(Ingredient ingredient : ingredients) {
			if(ingredient.getNames().contains(text)) {
				return ingredient;
			}
			for(String name : ingredient.getNames()) {
				int diff = stringDiff(name, text);
				double er = 1d * diff / text.length();
				
				if(er <= error && er < e) {
					e = er;
					bestMatch = ingredient;
				}
			}
		}
		return bestMatch;
	}
	public CookingAction findCookingAction(String text) {
		return findCookingAction(text, 0d);
	}
	public CookingAction findCookingAction(String text, double error) {
		if(text == null) return null;

		CookingAction bestMatch = null;
		double e = 1.0;

		for(CookingAction cookingAction : cookingActions) {
			if(cookingAction.getNames().contains(text)) {
				return cookingAction;
			}
			for(String name : cookingAction.getNames()) {
				int diff = stringDiff(name, text);
				double er = 1d * diff / text.length();
				
				if(er <= error && er < e) {
					e = er;
					bestMatch = cookingAction;
				}
			}
		}
		return bestMatch;
	}

	public boolean isUnknown(String text, double error) {
		return findTool(text, error) == null
				&& findIngredient(text, error) == null
				&& findCookingAction(text, error) == null;
	}
	public boolean textContainsLastSentenceReference(String text) {
		String lcText = text.toLowerCase();
		for(String reference : lastSentenceReferences) {
			if(lcText.contains(reference.toLowerCase())) {
				return true;
			}
		}
		
		return false;
	}

	private static int stringDiff(String a, String b) {
		int dif = Math.abs(a.length() - b.length());
		
		for(int i = 0; i < a.length() && i < b.length(); i++) {
			if(a.charAt(i) != b.charAt(i)) {
				dif++;
			}
		}
		
		return dif;
	}

	public Role identify(String text) {
		Ingredient i = findIngredient(text);
		Tool t = findTool(text);
		
		if(i != null && t != null) {
			return Role.UNDECIDABLE_OBJECT;
		} else if(i != null) {
			return Role.INGREDIENT;
		} else if(t != null) {
			return Role.TOOL;
		} else {
			return null;
		}
	}
}