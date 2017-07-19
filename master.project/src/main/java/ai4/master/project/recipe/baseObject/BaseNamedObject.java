package ai4.master.project.recipe.baseObject;

import ai4.master.project.KeyWordDatabase;
import ai4.master.project.recipe.object.NamedObject;
import ai4.master.project.stanfordParser.sentence.Word;

import java.util.HashSet;
import java.util.Set;

public abstract class BaseNamedObject<N extends NamedObject<B>, B extends BaseNamedObject<N, B>> implements Comparable<B> {
	
	private String firstName;
	private Set<String> names;
	private Set<String> stemmedNames;
	
	
	public BaseNamedObject() {
		names = new HashSet<String>();
		stemmedNames = new HashSet<String>();
	}
	protected BaseNamedObject(BaseNamedObject<N, B> parent, KeyWordDatabase kwdb) {
		this();
		firstName = parent.firstName;
		names.addAll(parent.names);
		stemmedNames.addAll(parent.stemmedNames);
	}
	
	public String getFirstName() {
		return firstName;
	}
	
	public Set<String> getNames() {
		return names;
	}
	public Set<String> getStemmedNames() {
		return stemmedNames;
	}
	
	public void addName(String name) {
		if(firstName == null) {
			firstName = name;
		}
		names.add(name);
		stemmedNames.add(Word.stem(name));
	}
	public void removeName(String name) {
		names.remove(name);
		stemmedNames.remove(Word.stem(name));
		
		if(firstName.equals(name)) {
			firstName = null;
			
			if(names.size() != 0) {
				firstName = names.iterator().next();
			}
		}
	}
	
	@Override
	public int compareTo(B other) {
		if(other == null) {
			return 1;
		} else if(firstName == null) {
			if(other.getFirstName() == null) {
				return 0;
			} else {
				return -1;
			}
		} else {
			return getFirstName().compareTo(other.getFirstName());	
		}		
	}
	@Override
	public String toString() {
		return firstName;
	}

	public abstract N toObject();

	public abstract String toXML();
	public abstract String toRefXML();
	
	public abstract B clone(KeyWordDatabase kwdb);
}