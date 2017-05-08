package ai4.master.project.model;

import java.util.HashSet;
import java.util.Set;

import lombok.Getter;

@Getter
public class NamedObject {
	
	private Set<String> names;
	
	public NamedObject() {
		names = new HashSet<String>();
	}
}
