package ai4.master.project.recipe;

import java.util.HashSet;
import java.util.Set;

import ai4.master.project.stanfordParser.sentence.Word;

public class NamedObject {
	
	private Set<String> names;
	private Set<String> stemmedNames;
	
	public NamedObject() {
		names = new HashSet<String>();
		stemmedNames = new HashSet<String>();
	}
	
	public Set<String> getNames() {
		return names;
	}
	public Set<String> getStemmedNames() {
		return stemmedNames;
	}

	public void setName(String name){
		names.add(name);
	}
	
	public void addName(String name) {
		names.add(name);
				
		stemmedNames.add(Word.stem(name));
	}
	
	@Override
	public String toString() {
		return names.iterator().next();
	}
}
