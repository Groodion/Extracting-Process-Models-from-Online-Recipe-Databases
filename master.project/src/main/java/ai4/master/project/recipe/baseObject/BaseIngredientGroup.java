package ai4.master.project.recipe.baseObject;

import ai4.master.project.KeyWordDatabase;
import ai4.master.project.recipe.object.IngredientGroup;

public class BaseIngredientGroup extends BaseIngredient {
	
	public BaseIngredientGroup() {
		super();
	}
	private BaseIngredientGroup(BaseIngredientGroup parent, KeyWordDatabase kwdb) {
		super(parent, kwdb);
	}
	
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
		
		sB.append("<Group name=\"");
		sB.append(getFirstName());
		sB.append("\">");
		for(String name : getNames()) {
			if(name != getFirstName()) {
				sB.append("<Name>");
				sB.append(name);
				sB.append("</Name>");
			}
		}
		sB.append("</Group>");
		
		return sB.toString();
	}

	@Override
	public BaseIngredientGroup clone(KeyWordDatabase kwdb) {
		return new BaseIngredientGroup(this, kwdb);
	}
}