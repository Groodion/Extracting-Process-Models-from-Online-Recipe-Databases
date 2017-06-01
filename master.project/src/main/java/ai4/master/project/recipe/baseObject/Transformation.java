package ai4.master.project.recipe.baseObject;

import java.util.ArrayList;
import java.util.List;

import ai4.master.project.recipe.object.Ingredient;
import ai4.master.project.recipe.object.ingredientTag.IngredientTag;

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
}
