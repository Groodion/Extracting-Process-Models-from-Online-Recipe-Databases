package ai4.master.project.recipe.object;

import ai4.master.project.recipe.baseObject.BaseTool;

public class Tool extends ImplicitNamedObject<BaseTool> {
	
	private boolean charged;
	
	public Tool(String name, BaseTool baseObject) {
		super(name, baseObject);
	}
	
	public boolean isCharged() {
		return charged;
	}
	public void setCharged(boolean charged) {
		this.charged = charged;
	}


	@Override
	public String toString() {
		return "Tool [name=" + getName() + ", implicit=" + isImplicit() + ", charged=" + charged + "]";
	}
}