package ai4.master.project.recipe.baseObject;

import java.util.ArrayList;
import java.util.List;

import ai4.master.project.recipe.object.CookingAction;
import ai4.master.project.recipe.object.Ingredient;


public class BaseCookingAction extends BaseNamedObject<CookingAction, BaseCookingAction> {
	
	private List<BaseTool> implicitTools;
	private List<Regex> regexList;
	private List<Transformation> transformations;
	
	
	public BaseCookingAction() {
		implicitTools = new ArrayList<BaseTool>();
		regexList = new ArrayList<Regex>();
		transformations = new ArrayList<Transformation>();
	}

	/**
	 * Liste mit allen Werkzeugen die von der Aktion Impliziert werden 
	 * und bei der automatischen Step-Erzeugung dem Objekt hinzugef�gt 
	 * werden wenn kein anderes Werkzeug im Text erw�hnt wird.
	 * @return Implizite Werkzeugliste
	 */
	public List<BaseTool> getImplicitTools() {
		return implicitTools;
	}
	/**
	 * Liste mit allen regul�ren Ausdr�cken die zur automatischen 
	 * Produktidentifikation von einer Aktion gespeichert sind.
	 * @return Liste mit regul�ren Ausdr�cken
	 */
	public List<Regex> getRegexList() {
		return regexList;
	}
	/**
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
	 * @return transformaierte Zutat
	 */
	public Ingredient transform(Ingredient ingredient, List<Ingredient> list) {
		for(Transformation transformation : transformations) {
			if(transformation.matches(ingredient, list)) {
				return transformation.transform(ingredient, list);
			}
		}
		
		return null;
	}

	@Override
	public CookingAction toObject() {
		String name = "UNNAMED";
		
		if(getNames().size() != 0) {
			name = getNames().iterator().next();
		}
		return new CookingAction(name, this);
	}
}