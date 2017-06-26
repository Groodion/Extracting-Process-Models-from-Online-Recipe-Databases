package ai4.master.project.recipe.baseObject;

import ai4.master.project.recipe.object.IngredientGroup;

public class BaseIngredientGroup extends BaseIngredient {
	@Override
	public IngredientGroup toObject() {
		String name = "UNNAMED";
		
		if(getNames().size() != 0) {
			name = getNames().iterator().next();
		}
		
		return new IngredientGroup(name, this);
	}
	
	@Override
	public String toXML() {
		StringBuilder sB = new StringBuilder();
		
		sB.append("<Group>");
		for(String name : getNames()) {
			sB.append("<Name>");
			sB.append(name);
			sB.append("</Name>");			
		}
		sB.append("</Group>");
		
		return sB.toString();
	}
}
