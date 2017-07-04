package ai4.master.project.view;

import javafx.beans.property.SimpleStringProperty;

public class LastSentenceReferenceEntry {
private final SimpleStringProperty lastSentenceReference;
	
	public LastSentenceReferenceEntry(String lastSentenceReference) {
		this.lastSentenceReference = new SimpleStringProperty(lastSentenceReference);
	}
	
	public String getLastSentenceReference() {
		return lastSentenceReference.get();
	}
	
	public void setLastSentenceReference(String lastSentenceReference) {
		this.lastSentenceReference.set(lastSentenceReference);
	}

}
