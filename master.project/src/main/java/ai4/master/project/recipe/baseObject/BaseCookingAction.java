package ai4.master.project.recipe.baseObject;

import ai4.master.project.KeyWordDatabase;
import ai4.master.project.recipe.object.CookingAction;
import ai4.master.project.recipe.object.Ingredient;
import ai4.master.project.recipe.object.IngredientGroup;
import ai4.master.project.recipe.object.Tool;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class BaseCookingAction extends BaseNamedObject<CookingAction, BaseCookingAction> {
	
	private List<ItemGroup<BaseTool, Tool>> implicitTools;
	private List<ItemGroup<BaseIngredient, Ingredient>> implicitIngredients;
	private List<Regex> regexList;
	private List<Transformation> transformations;
	
	
	public BaseCookingAction() {
		implicitTools = new ArrayList<ItemGroup<BaseTool, Tool>>();
		implicitIngredients = new ArrayList<ItemGroup<BaseIngredient, Ingredient>>();
		regexList = new ArrayList<Regex>();
		transformations = new ArrayList<Transformation>();
	}
	private BaseCookingAction(BaseCookingAction parent, KeyWordDatabase kwdb) {
		super(parent, kwdb);

		implicitTools = new ArrayList<ItemGroup<BaseTool, Tool>>();
		implicitIngredients = new ArrayList<ItemGroup<BaseIngredient, Ingredient>>();
		regexList = new ArrayList<Regex>();
		transformations = new ArrayList<Transformation>();
		
		for(ItemGroup<BaseTool, Tool> toolGroup : parent.implicitTools) {
			implicitTools.add(toolGroup.clone(kwdb));
		}
		for(ItemGroup<BaseIngredient, Ingredient> ingredientGroup : parent.implicitIngredients) {
			implicitIngredients.add(ingredientGroup.clone(kwdb));
		}
		for(Regex regex : parent.regexList) {
			regexList.add(regex.clone(kwdb));
		}
		for(Transformation transformation : parent.transformations) {
			transformations.add(transformation.clone(kwdb));
		}
	}
	
	/**
	 * Liste mit allen Werkzeugen die von der Aktion Impliziert werden 
	 * und bei der automatischen Step-Erzeugung dem Objekt hinzugef�gt 
	 * werden wenn kein anderes Werkzeug im Text erw�hnt wird.
	 * @return Implizite Werkzeugliste
	 */
	public List<ItemGroup<BaseTool, Tool>> getImplicitTools() {
		return implicitTools;
	}
	public List<ItemGroup<BaseIngredient, Ingredient>> getImplicitIngredients() {
		return implicitIngredients;
	}
	/**
	 * Liste mit allen regul�ren Ausdr�cken die zur automatischen 
	 * Produktidentifikation von einer Aktion gespeichert sind.
	 * @return Liste mit regul�ren Ausdr�cken
	 */
	public List<Regex> getRegexList() {
		return regexList;
	}
	public Set<String> getUsedRegexTags() {
		Set<String> usedRegexTags = new HashSet<String>();
		
		for(Regex regex : regexList) {
			usedRegexTags.addAll(regex.getUsedTags());
		}
		
		return usedRegexTags;
	}	/**
	 * Liste mit allen Transformationsm�glichkeiten die von einer Aktion
	 * ausgehen
	 * @return Liste mit Transformationsm�glichkeiten
	 */
	public List<Transformation> getTransformations() {
		return transformations;
	}
	
	/**
	 * W�hlt ausgehend von den zus�tzlichen Zutaten das entsprechende Transformations-Objekt
	 * aus der Liste aus und wendet diese auf die Hauptzutat an.
	 * @param ingredient Die zu transformierende Zutat
	 * @param list Liste mit restlichen Zutaten des Step-Objekts
	 * @param regex 
	 * @return transformaierte Zutat
	 */
	public List<Ingredient> transform(Ingredient ingredient, List<Ingredient> list, Regex regex) {
		List<Ingredient> transformedIngredients = new ArrayList<Ingredient>();
		for(Transformation transformation : transformations) {
			if(regex.canExecute(transformation) && transformation.matches(ingredient, list)) {
				if(ingredient instanceof IngredientGroup && !((IngredientGroup) ingredient).getIngredients().isEmpty()) {
					for(Ingredient i : ((IngredientGroup) ingredient).getIngredients()) {
						transformedIngredients.add(transformation.transform(i, list));
					}
				} else {
					transformedIngredients.add(transformation.transform(ingredient, list));
				}
				
				break;
			}
		}
		
		return transformedIngredients;
	}

	@Override
	public CookingAction toObject() {
		String name = "UNNAMED";
		
		if(getNames().size() != 0) {
			name = getNames().iterator().next();
		}
		return new CookingAction(name, this);
	}
	
	@Override
	public String toXML() {
		StringBuilder sB = new StringBuilder();
		
		sB.append("<CookingAction name=\"");
		sB.append(getFirstName());
		sB.append("\">");
		for(String name : getNames()) {
			if(name != getFirstName()) {
				sB.append("<Name>");
				sB.append(name);
				sB.append("</Name>");
			}
		}
		sB.append("<regs>");
		for(Regex regex : regexList) {
			sB.append(regex.toXML());
		}
		sB.append("</regs>");
		sB.append("<transformations>");
		for(Transformation transformation : transformations) {
			sB.append(transformation.toXML());		
		}
		sB.append("</transformations>");
		sB.append("<tools>");
		for(ItemGroup<BaseTool, Tool> toolGroup : implicitTools) {
			sB.append(toolGroup.toXML());
		}
		sB.append("</tools>");
		sB.append("<ingredients>");
		for(ItemGroup<BaseIngredient, Ingredient> ingredientGroup : implicitIngredients) {
			sB.append(ingredientGroup.toXML());
		}
		sB.append("</ingredients>");

		sB.append("</CookingAction>");
		
		return sB.toString();
	}
	@Override
	public String toRefXML() {
		StringBuilder sB = new StringBuilder();
		
		sB.append("<CookingAction name=\"");
		sB.append(getFirstName());
		sB.append("\" />");
		
		return sB.toString();
	}

	@Override
	public BaseCookingAction clone(KeyWordDatabase kwdb) {
		return new BaseCookingAction(this, kwdb);
	}
}