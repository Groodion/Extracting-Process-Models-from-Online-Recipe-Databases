package ai4.master.project.recipe.baseObject;

import java.util.ArrayList;
import java.util.List;

import ai4.master.project.recipe.ResultType;
import ai4.master.project.recipe.object.CookingAction;
import ai4.master.project.recipe.object.Ingredient;

public class BaseCookingAction extends BaseNamedObject<CookingAction, BaseCookingAction> {
	
	private ResultType resultFinder;
	
	private List<BaseTool> implicitTools;
	private List<Regex> regexList;
	private List<Transformation> transformations;
	
	
	public BaseCookingAction() {
		implicitTools = new ArrayList<BaseTool>();
		regexList = new ArrayList<Regex>();
		transformations = new ArrayList<Transformation>();
	}
	
	public ResultType getResultFinder() {
		return resultFinder;
	}
	public void setResultFinder(ResultType resultFinder) {
		this.resultFinder = resultFinder;
	}

	public List<BaseTool> getImplicitTools() {
		return implicitTools;
	}
	public List<Regex> getRegexList() {
		return regexList;
	}
	public List<Transformation> getTransformations() {
		return transformations;
	}
	
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