package ai4.master.project.recipe.baseObject;

import java.util.List;
import java.util.ArrayList;

import ai4.master.project.recipe.object.Ingredient;


public class BaseIngredient extends BaseNamedObject<Ingredient, BaseIngredient> {

	private List<BaseIngredientGroup> groups;
	
	
	public BaseIngredient() {
		groups = new ArrayList<BaseIngredientGroup>();
	}
	
	public List<BaseIngredientGroup> getIngredientGroups() {
		return groups;
	}
	
	@Override
	public Ingredient toObject() {
		String name = "UNNAMED";
		
		if(getNames().size() != 0) {
			name = getNames().iterator().next();
		}
		
		return new Ingredient(name, this);
	}
	
	@Override
	public String toXML() {
		StringBuilder sB = new StringBuilder();
		
		sB.append("<Ingredient>");
		for(String name : getNames()) {
			sB.append("<Name>");
			sB.append(name);
			sB.append("</Name>");			
		}
		sB.append("<groups>");
		for(BaseIngredientGroup group : getIngredientGroups()) {
			System.out.println(group + " " + this);
			sB.append("<Group name=\"");
			sB.append(group.getNames().iterator().next());
			sB.append("\" />");
		}
		
		sB.append("</groups>");
		sB.append("</Ingredient>");
		
		return sB.toString();
	}
}