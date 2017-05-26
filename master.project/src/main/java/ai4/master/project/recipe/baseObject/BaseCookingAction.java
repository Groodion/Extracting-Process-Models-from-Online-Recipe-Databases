package ai4.master.project.recipe.baseObject;

import java.util.ArrayList;
import java.util.List;

import ai4.master.project.recipe.ResultType;
import ai4.master.project.recipe.object.CookingAction;

public class BaseCookingAction extends BaseNamedObject<CookingAction, BaseCookingAction> {

	private String result;
	
	private ResultType resultFinder;
	
	private List<BaseTool> implicitTools;
	
	
	public BaseCookingAction() {
		result = null;
		implicitTools = new ArrayList<BaseTool>();
	}
	
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
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
	
	public BaseIngredient transform(BaseIngredient mainIngredient, List<BaseIngredient> ingredients) {
		if(result == null) {
			return mainIngredient;
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