package ai4.master.project.view;

import ai4.master.project.KeyWordDatabase;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;

public class LastSentenceReferenceEntry {
	private StringProperty lastSentenceReference;

	public LastSentenceReferenceEntry(String lastSentenceReference, ObservableList<LastSentenceReferenceEntry> parent,
			final KeyWordDatabase kwdb) {
		this.lastSentenceReference = new SimpleStringProperty(lastSentenceReference);

		this.lastSentenceReference.addListener((b, o, n) -> {
			if (n == null || n.length() == 0) {
				parent.remove(this);
				kwdb.getLastSentenceReferences().remove(lastSentenceReference);
			} else {
				kwdb.getLastSentenceReferences().set(kwdb.getLastSentenceReferences().indexOf(o), n);
			}
		});

		parent.add(this);
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