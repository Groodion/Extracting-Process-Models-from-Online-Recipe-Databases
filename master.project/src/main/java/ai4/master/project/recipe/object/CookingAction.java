package ai4.master.project.recipe.object;

import ai4.master.project.recipe.baseObject.BaseCookingAction;

public class CookingAction extends NamedObject<BaseCookingAction> {

	public CookingAction(String name, BaseCookingAction baseObject) {
		super(name, baseObject);
	}

	@Override
	public String toString() {
		return "CookingAction [name=" + getName() + "]";
	}


}