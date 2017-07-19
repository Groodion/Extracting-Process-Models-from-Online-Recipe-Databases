package ai4.master.project.recipe.object;

import ai4.master.project.recipe.baseObject.BaseNamedObject;

public class ImplicitNamedObject<B extends BaseNamedObject<?, B>> extends NamedObject<B> {
	
	private boolean implicit;

	
	public ImplicitNamedObject(String name, B baseObject) {
		super(name, baseObject);
	}
	
	public boolean isImplicit() {
		return implicit;
	}
	public void setImplicit(boolean implicit) {
		this.implicit = implicit;
	}
}