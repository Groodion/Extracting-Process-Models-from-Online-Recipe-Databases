package ai4.master.project.recipe.baseObject;

import ai4.master.project.KeyWordDatabase;
import ai4.master.project.recipe.object.Ingredient;

import java.util.ArrayList;
import java.util.List;


public class BaseIngredient extends BaseNamedObject<Ingredient, BaseIngredient> {

	private List<BaseIngredientGroup> groups;
	
	public BaseIngredient() {
		groups = new ArrayList<BaseIngredientGroup>();
	}
	protected BaseIngredient(BaseIngredient parent, KeyWordDatabase kwdb) {
		super(parent, kwdb);
		
		groups = new ArrayList<BaseIngredientGroup>();

		for(BaseIngredientGroup group : parent.groups) {
			groups.add(kwdb.findIngredientGroup(group.getFirstName()));
		}
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
		
		sB.append("<Ingredient name=\"");
		sB.append(getFirstName());
		sB.append("\">");
		for(String name : getNames()) {
			if(name != getFirstName()) {
				sB.append("<Name>");
				sB.append(name);
				sB.append("</Name>");
			}
		}
		sB.append("<groups>");
		for(BaseIngredientGroup group : getIngredientGroups()) {
			sB.append("<Group name=\"");
			sB.append(group.getNames().iterator().next());
			sB.append("\" />");
		}
		
		sB.append("</groups>");
		sB.append("</Ingredient>");
		
		return sB.toString();
	}
	@Override
	public String toRefXML() {
		StringBuilder sB = new StringBuilder();
		
		sB.append("<Ingredient name=\"");
		sB.append(getFirstName());
		sB.append("\" />");
		
		return sB.toString();
	}

	
	@Override
	public BaseIngredient clone(KeyWordDatabase kwdb) {
		return new BaseIngredient(this, kwdb);
	}
}