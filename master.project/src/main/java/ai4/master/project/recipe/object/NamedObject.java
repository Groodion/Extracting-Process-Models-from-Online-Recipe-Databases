package ai4.master.project.recipe.object;

import ai4.master.project.recipe.baseObject.BaseNamedObject;

public abstract class NamedObject<T extends BaseNamedObject<?, T>> {

	private String name;
	
	private T baseObject;
	
	
	public NamedObject(String name, T baseObject) {
		this.name = name;
		this.baseObject = baseObject;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public T getBaseObject() {
		return baseObject;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((baseObject == null) ? 0 : baseObject.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NamedObject<?> other = (NamedObject<?>) obj;
		if (baseObject == null) {
			if (other.baseObject != null)
				return false;
		} else if (!baseObject.equals(other.baseObject))
			return false;
		return true;
	}

}