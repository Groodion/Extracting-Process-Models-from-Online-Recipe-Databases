package ai4.master.project.recipe.object;

import ai4.master.project.recipe.baseObject.BaseTool;

public class Tool extends NamedObject<BaseTool> {
	
	private boolean implicit;
	private boolean charged;
	
	public Tool(String name, BaseTool baseObject) {
		super(name, baseObject);
	}

	public boolean isImplicit() {
		return implicit;
	}
	public void setImplicit(boolean implicit) {
		this.implicit = implicit;
	}
	
	public boolean isCharged() {
		return charged;
	}
	public void setCharged(boolean charged) {
		this.charged = charged;
	}


	@Override
	public String toString() {
		return "Tool [name=" + getName() + ", implicit=" + implicit + ", charged=" + charged + "]";
	}
}