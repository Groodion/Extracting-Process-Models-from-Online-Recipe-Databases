package ai4.master.project;

import java.util.ArrayList;
import java.util.List;

import ai4.master.project.recipe.baseObject.BaseCookingAction;
import ai4.master.project.recipe.baseObject.BaseIngredient;
import ai4.master.project.recipe.baseObject.BaseIngredientGroup;
import ai4.master.project.recipe.baseObject.BaseTool;
import ai4.master.project.stanfordParser.sentence.Role;


public class KeyWordDatabase {
	
	private List<BaseTool> tools;
	private List<BaseIngredient> ingredients;
	private List<BaseCookingAction> cookingActions;
	private List<BaseIngredientGroup> ingredientGroups;
	private List<String> partIndicators;
	private List<String> lastSentenceReferences;
	
	public KeyWordDatabase() {
		tools = new ArrayList<BaseTool>();
		ingredients = new ArrayList<BaseIngredient>();
		cookingActions = new ArrayList<BaseCookingAction>();
		partIndicators = new ArrayList<String>();
		lastSentenceReferences = new ArrayList<String>();
	}

	public List<BaseTool> getTools() {
		return tools;
	}
	public List<BaseIngredient> getIngredients() {
		return ingredients;
	}
	public List<BaseCookingAction> getCookingActions() {
		return cookingActions;
	}

	public List<String> getPartIndicators() {
		return partIndicators;
	}
	public List<String> getLastSentenceReferences() {
		return lastSentenceReferences;
	}
	
	public BaseTool findTool(String text) {
		return findTool(text, 0d);
	}
	public BaseTool findTool(String text, double error) {
		if(text == null) return null;
		
		BaseTool bestMatch = null;
		double e = 1.0;
		
		for(BaseTool tool : tools) {
			if(tool.getStemmedNames().contains(text)) {
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
	public BaseIngredient findIngredient(String text) {
		return findIngredient(text, 0d);
	}
	public BaseIngredient findIngredient(String text, double error) {
		if(text == null) return null;

		BaseIngredient bestMatch = null;
		double e = 1.0;

		for(BaseIngredient ingredient : ingredients) {
			if(ingredient.getStemmedNames().contains(text)) {
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
	public BaseCookingAction findCookingAction(String text) {
		return findCookingAction(text, 0d);
	}
	public BaseCookingAction findCookingAction(String text, double error) {
		if(text == null) return null;

		
		BaseCookingAction bestMatch = null;
		double e = 1.0;

		for(BaseCookingAction cookingAction : cookingActions) {
			if(cookingAction.getStemmedNames().contains(text)) {
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
	public BaseIngredientGroup findIngredientGroup(String text) {
		for(BaseIngredientGroup group : ingredientGroups) {
			if(group.getStemmedNames().contains(text)) {
				return group;
			}
		}
		
		return null;
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
	public boolean isLastSentenceRefernece(String text) {
		return lastSentenceReferences.contains(text.toLowerCase());
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
		BaseIngredient i = findIngredient(text);
		BaseTool t = findTool(text);
		
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

	public List<BaseIngredientGroup> getIngredientGroups() {
		return ingredientGroups;
	}

}