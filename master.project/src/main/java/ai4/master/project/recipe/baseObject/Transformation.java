package ai4.master.project.recipe.baseObject;

import java.util.ArrayList;
import java.util.List;

import ai4.master.project.recipe.object.Ingredient;
import ai4.master.project.recipe.object.ingredientTag.IngredientTag;
import ai4.master.project.recipe.object.ingredientTag.QuantifierTag;

public class Transformation {

	private Ingredient product;
	private List<Ingredient> mandatoryIngredients;
	private IngredientTag tag;
	
	
	public Transformation() {
		mandatoryIngredients = new ArrayList<Ingredient>();
	}
	
	public Ingredient getProduct() {
		return product;
	}
	public void setProduct(Ingredient product) {
		this.product = product;
	}
	public IngredientTag getTag() {
		return tag;
	}
	public void setTag(IngredientTag tag) {
		this.tag = tag;
	}

	public List<Ingredient> getMandatoryIngredients() {
		return mandatoryIngredients;
	}
	
	/**
	 * Testet ob die Vorraussetzungen an die gegebenen Zutaten für diese Transformation
	 * erfüllt sind
	 * @param ingredient Die zu transformierende Zutat
	 * @param list Liste mit restlichen Zutaten des Step-Objekts
	 * @return Testresultat
	 */
	public boolean matches(Ingredient ingredient, List<Ingredient> list) {
		if(mandatoryIngredients.isEmpty()) {
			return true;
		}
		
		List<Ingredient> checkList = new ArrayList<Ingredient>();
		
		checkList.addAll(mandatoryIngredients);
		
		checkList.remove(ingredient);
		checkList.remove(list);
		
		return checkList.isEmpty();
	}
	
	/**
	 * führt eine von vier Transformationen durch:<br />
	 * 1. wenn das produkt gesetzt ist wird dieses als ergebnis der Transformation
	 * zurückgegeben<br />
	 * 2. wenn eine IngredientTag gesetzt ist wird die Hauptzutat damit getagged<br />
	 * 3. wenn eine QuantifierTag gesetzt ist wird die Hauptzutat damit getagged<br />
	 * 4. wenn nichts gesetzt ist wird die Zutat unverändert zurückgegeben
	 * @param ingredient Hauptzutat
	 * @param list Zutatsliste
	 * @return transformierte Zutat
	 */
	public Ingredient transform(Ingredient ingredient, List<Ingredient> list) {
		if(product != null) {
			return product;
		} else if(tag != null) {
			if(tag.getName().contains("INGREDIENT")) {
				Ingredient product = ingredient;
				for(Ingredient i : list) {
					if(ingredient != i) {
						product = product.tag(tag.replace(i));
					}
				}
				return product;
			} else {
				return ingredient.tag(tag);
			}
		}
		
		return ingredient;
	}

	public String toXML() {
		StringBuilder sB = new StringBuilder();
		
		sB.append("<Transformation>");
		sB.append("<ingredients>");
		for(Ingredient ingredient : mandatoryIngredients) {
			sB.append("<Ingredient name=\"");
			sB.append(ingredient.getName());
			sB.append("\"/>");
		}
		sB.append("</ingredients>");
		if(product != null) {
			sB.append("<Ingredient name=\"");
			sB.append(product.getName());
			sB.append("\"/>");			
		} else if(tag instanceof QuantifierTag) {
			sB.append("<AddQuantifierTag>");
			sB.append(tag.getName());
			sB.append("</AddQuantifierTag>");
		} else {
			sB.append("<AddIngredientTag>");
			sB.append(tag.getName());
			sB.append("</AddIngredientTag>");
		}
		sB.append("</Transformation>");
		
		return sB.toString();
	}	
}
