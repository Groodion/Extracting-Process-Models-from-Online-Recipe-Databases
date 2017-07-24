package ai4.master.project;

import ai4.master.project.recipe.baseObject.BaseCookingAction;
import ai4.master.project.recipe.baseObject.BaseIngredient;
import ai4.master.project.recipe.baseObject.BaseIngredientGroup;
import ai4.master.project.recipe.baseObject.BaseNamedObject;
import ai4.master.project.recipe.baseObject.BaseTool;
import ai4.master.project.stanfordParser.sentence.Role;
import ai4.master.project.stanfordParser.sentence.Word;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class KeyWordDatabase {
		
	private List<BaseTool> tools;
	private List<BaseIngredient> ingredients;
	private List<BaseCookingAction> cookingActions;
	private List<BaseIngredientGroup> ingredientGroups;
	private List<String> partIndicators;
	private List<String> lastSentenceReferences;
	private List<String> eventIndicators;
	
	public KeyWordDatabase() {
		tools = new ArrayList<BaseTool>();
		ingredients = new ArrayList<BaseIngredient>();
		ingredientGroups = new ArrayList<BaseIngredientGroup>();
		cookingActions = new ArrayList<BaseCookingAction>();
		partIndicators = new ArrayList<String>();
		lastSentenceReferences = new ArrayList<String>();
		eventIndicators = new ArrayList<String>();
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
	public List<String> getEventIndicators() {
		return eventIndicators;
	}
	
	public BaseTool findTool(String text) {
		return findTool(text, 0d);
	}
	public BaseTool findTool(String text, double error) {
		if(text == null) return null;
		
		BaseTool bestMatch = null;
		double e = 1.0;
		
		for(BaseTool tool : tools) {
			if(tool.getStemmedNames().contains(Word.stem(text))) {
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
		if(text == null) return null;
		
		for(BaseIngredient ingredient : ingredients) {
			if(ingredient.getStemmedNames().contains(Word.stem(text))) {
				return ingredient;
			}
		}
		
		return findIngredientGroup(text);
	}
	public BaseCookingAction findCookingAction(String text) {
		return findCookingAction(text, 0d);
	}
	public BaseCookingAction findCookingAction(String text, double error) {
		if(text == null) return null;
		
		BaseCookingAction bestMatch = null;
		double e = 1.0;

		for(BaseCookingAction cookingAction : cookingActions) {
			if(cookingAction.getStemmedNames().contains(Word.stem(text))) {
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
			if(group.getStemmedNames().contains(Word.stem(text))) {
				return group;
			}
		}
		
		return null;
	}

	public BaseNamedObject<?, ?> find(String word) {
		BaseNamedObject<?, ?> obj = findTool(word);
		
		if(obj == null) {
			obj = findIngredient(word);
			
			if(obj == null) {
				obj = findCookingAction(word);
			}
		}
		
		return obj;
	}
	
	public boolean isUnknown(String text, double error) {
		return findTool(text, error) == null
				&& findIngredient(text) == null
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
	public boolean isEventIndicator(String text) {
		for(String i : eventIndicators) {
			if(Word.stem(i.toLowerCase()).equals(Word.stem(text.toLowerCase()))) {
				return true;
			}
		}
		
		return false;
	}
	public boolean isPartIndicator(String text) {
		for(String i : partIndicators) {
			if(Word.stem(i.toLowerCase()).equals(Word.stem(text.toLowerCase()))) {
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

	public String toXML() {
		StringBuilder sB = new StringBuilder();
		
		sB.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE root SYSTEM \"rezept.dtd\">");
		
		sB.append("<root>");
		sB.append("<tools>");
		for(BaseTool tool : tools) {
			sB.append(tool.toXML());
		}
		sB.append("</tools>");

		sB.append("<groups>");
		for(BaseIngredientGroup group : ingredientGroups) {
			sB.append(group.toXML());
		}
		sB.append("</groups>");

		sB.append("<ingredients>");
		for(BaseIngredient ingredient : ingredients) {
			sB.append(ingredient.toXML());
		}
		sB.append("</ingredients>");

		sB.append("<cookingActions>");
		for(BaseCookingAction cookingAction : cookingActions) {
			sB.append(cookingAction.toXML());
		}
		sB.append("</cookingActions>");

		sB.append("<partIndicators>");
		for(String partIndicator : partIndicators) {
			sB.append("<PartIndicator>");
			sB.append(partIndicator);
			sB.append("</PartIndicator>");
		}
		sB.append("</partIndicators>");

		sB.append("<lastSentenceReferences>");
		for(String lastSentenceReference : lastSentenceReferences) {
			sB.append("<LastSentenceReference>");
			sB.append(lastSentenceReference);
			sB.append("</LastSentenceReference>");
		}
		sB.append("</lastSentenceReferences>");
		sB.append("<eventIndicators>");
		for(String eventIndicator : eventIndicators) {
			sB.append("<EventIndicator>");
			sB.append(eventIndicator);
			sB.append("</EventIndicator>");
		}
		sB.append("</eventIndicators>");
		sB.append("</root>");

		return sB.toString();
	}

	@Override
	public KeyWordDatabase clone() {
		KeyWordDatabase clone = new KeyWordDatabase();
		
		for(BaseTool tool : tools) {
			clone.getTools().add(tool.clone(this));
		}
		for(BaseIngredient ingredient : ingredients) {
			clone.getIngredients().add(ingredient.clone(this));
		}
		for(BaseIngredientGroup group : ingredientGroups) {
			clone.getIngredientGroups().add(group.clone(this));
		}
		for(BaseCookingAction cookingAction : cookingActions) {
			clone.getCookingActions().add(cookingAction.clone(this));
		}

		clone.getPartIndicators().addAll(partIndicators);
		clone.getLastSentenceReferences().addAll(lastSentenceReferences);
		clone.getEventIndicators().addAll(eventIndicators);
		
		clone.sort();
		
		return clone;
	}

	public void sort() {
		Collections.sort(getTools());
		Collections.sort(getIngredients());
		Collections.sort(getIngredientGroups());
		Collections.sort(getCookingActions());
		Collections.sort(getEventIndicators());
		Collections.sort(getPartIndicators());
		Collections.sort(getLastSentenceReferences());
	}
}