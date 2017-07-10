package ai4.master.project.viewFx.components.editorViews.entries;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;

public class LastSentenceReferenceEntry {
	private StringProperty lastSentenceReference;

	public LastSentenceReferenceEntry(String lastSentenceReference, ObservableList<String> lastSentenceReferences) {
		this.lastSentenceReference = new SimpleStringProperty(lastSentenceReference);

		this.lastSentenceReference.addListener((b, o, n) -> {
			if (n == null || n.length() == 0) {
				lastSentenceReferences.remove(this);
			} else {
				lastSentenceReferences.set(lastSentenceReferences.indexOf(o), n);
			}
		});
	}

	public String getLastSentenceReference() {
		return lastSentenceReference.get();
	}
	public void setLastSentenceReference(String lastSentenceReference) {
		this.lastSentenceReference.set(lastSentenceReference);
	}
	public StringProperty lastSentenceReferenceProperty() {
		return lastSentenceReference;
	}
}