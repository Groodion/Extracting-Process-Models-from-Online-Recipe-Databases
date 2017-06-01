package ai4.master.project.recipe.baseObject;

import java.util.HashSet;
import java.util.Set;

import ai4.master.project.recipe.object.NamedObject;
import ai4.master.project.stanfordParser.sentence.Word;

public abstract class BaseNamedObject<N extends NamedObject<B>, B extends BaseNamedObject<N, B>> {
	
	private Set<String> names;
	private Set<String> stemmedNames;
	
	public BaseNamedObject() {
		names = new HashSet<String>();
		stemmedNames = new HashSet<String>();
	}
	
	public Set<String> getNames() {
		return names;
	}
	public Set<String> getStemmedNames() {
		return stemmedNames;
	}
	
	public void addName(String name) {
		names.add(name);
		stemmedNames.add(Word.stem(name));
	}
	
	@Override
	public String toString() {
		return names.iterator().next();
	}

	public abstract N toObject();
}