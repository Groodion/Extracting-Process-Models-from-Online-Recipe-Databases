package ai4.master.project.recipe.baseObject;

import ai4.master.project.KeyWordDatabase;
import ai4.master.project.recipe.object.Ingredient;
import ai4.master.project.recipe.object.IngredientGroup;
import ai4.master.project.recipe.object.ingredientTag.IngredientTag;
import ai4.master.project.recipe.object.ingredientTag.QuantifierTag;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;


public class Transformation {

	private BaseIngredient product;
	private ObservableList<BaseIngredient> mandatoryIngredients;
	private IngredientTag tag;
	private List<String> regexIds;	
	
	public Transformation() {
		mandatoryIngredients = javafx.collections.FXCollections.observableArrayList();
		regexIds = new ArrayList<String>();
	}
	private Transformation(Transformation parent, KeyWordDatabase kwdb) {
		this();
		
		if(parent.product != null) {
			product = parent.product.clone(kwdb);
		}
		for(BaseIngredient ingredient : parent.mandatoryIngredients) {
			mandatoryIngredients.add(ingredient.clone(kwdb));
		}
		if(parent.tag != null) {
			tag = parent.tag.clone(kwdb);
		}
		regexIds.addAll(parent.regexIds);
	}
	
	public BaseIngredient getProduct() {
		return product;
	}
	public void setProduct(BaseIngredient product) {
		this.product = product;
	}
	public IngredientTag getTag() {
		return tag;
	}
	public void setTag(IngredientTag tag) {
		this.tag = tag;
	}
	public List<String> getRegexIds() {
		return regexIds;
	}

	public List<BaseIngredient> getMandatoryIngredients() {
		return mandatoryIngredients;
	}
	
	/**
	 * Testet ob die Vorraussetzungen an die gegebenen Zutaten f�r diese Transformation
	 * erf�llt sind
	 * @param ingredient Die zu transformierende Zutat
	 * @param list Liste mit restlichen Zutaten des Step-Objekts
	 * @return Testresultat
	 */
	public boolean matches(Ingredient ingredient, List<Ingredient> list) {
		if(mandatoryIngredients.isEmpty()) {
			return true;
		}
		f:for(BaseIngredient mI : mandatoryIngredients) {
			if(mI.equals(ingredient.getBaseObject())) {
				continue;
			} else {
				for(Ingredient i : list) {
					if(mI.equals(i.getBaseObject())) {
						continue f;
					}
				}
			}
			
			System.out.println(mI + " " + ingredient.getBaseObject() + " " + list);
			
			return false;
		}
		
		return true;
	}
	
	/**
	 * f�hrt eine von vier Transformationen durch:<br />
	 * 1. wenn das produkt gesetzt ist wird dieses als ergebnis der Transformation
	 * zur�ckgegeben<br />
	 * 2. wenn eine IngredientTag gesetzt ist wird die Hauptzutat damit getagged<br />
	 * 3. wenn eine QuantifierTag gesetzt ist wird die Hauptzutat damit getagged<br />
	 * 4. wenn nichts gesetzt ist wird die Zutat unver�ndert zur�ckgegeben
	 * @param ingredient Hauptzutat
	 * @param list Zutatsliste
	 * @return transformierte Zutat
	 */
	public Ingredient transform(Ingredient ingredient, List<Ingredient> list) {
		if(product != null) {
			return product.toObject();
		} else if(tag != null) {
			if(tag.getName().contains("INGREDIENT")) {
				Ingredient product = ingredient;
				for(Ingredient i : list) {
					if(!(ingredient == i || (i instanceof IngredientGroup && ((IngredientGroup)i).getIngredients().contains(ingredient)))) {
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

	@Override
	public String toString() {
		StringBuilder sB = new StringBuilder();
		
		if(product != null) {
			sB.append("Product: ");
			sB.append(product.getFirstName());
		} else if(tag != null) {
			if(tag instanceof QuantifierTag) {
				sB.append("QuantifierTag: ");
			} else {
				sB.append("IngredientTag: ");
			}
			sB.append(tag.getName());
		} else {
			sB.append("Empty");
		}
		
		return sB.toString();
	}
	public String toXML() {
		StringBuilder sB = new StringBuilder();
		
		sB.append("<Transformation regexRefIds=\"");
		for(int i = 0; i < regexIds.size(); i++) {
			if(i != 0) {
				sB.append(',');
			}
			sB.append(regexIds.get(i));
		}
		sB.append("\">");
		sB.append("<ingredients>");
		for(BaseIngredient ingredient : mandatoryIngredients) {
			sB.append("<Ingredient name=\"");
			sB.append(ingredient.getFirstName());
			sB.append("\"/>");
		}
		sB.append("</ingredients>");
		if(product != null) {
			sB.append("<Ingredient name=\"");
			sB.append(product.getFirstName());
			sB.append("\"/>");			
		} else if(tag != null) {
			if(tag instanceof QuantifierTag) {
				sB.append("<AddQuantifierTag>");
				sB.append(tag.getName());
				sB.append("</AddQuantifierTag>");
			} else {
				sB.append("<AddIngredientTag>");
				sB.append(tag.getName());
				sB.append("</AddIngredientTag>");
			}
		}
		sB.append("</Transformation>");
		
		return sB.toString();
	}
	
	public Transformation clone(KeyWordDatabase kwdb) {
		return new Transformation(this, kwdb);
	}
}
