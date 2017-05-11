package ai4.master.project.recipe;

import java.util.HashSet;
import java.util.Set;

public class NamedObject {
	
	private Set<String> names;
	
	public NamedObject() {
		names = new HashSet<String>();
	}
	
	public Set<String> getNames() {
		return names;
	}
}
