package ai4.master.project.view;

import javafx.beans.property.SimpleStringProperty;

public class ToolEntry {
	private final SimpleStringProperty name;
	private final SimpleStringProperty synonyms;
	
	public ToolEntry(String name, String synonyms) {
		this.name = new SimpleStringProperty(name);
		this.synonyms = new SimpleStringProperty(synonyms);
	}
	
	public String getName() {
		return name.get();
	}
	
	public String getSynonyms() {
		return synonyms.get();
	}
	
	public void setName(String name) {
		this.name.set(name);
	}
	
	public void setSynonyms(String synonyms) {
		this.synonyms.set(synonyms);
	}
	
}
