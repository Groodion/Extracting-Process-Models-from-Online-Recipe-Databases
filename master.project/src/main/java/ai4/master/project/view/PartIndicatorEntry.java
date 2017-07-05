package ai4.master.project.view;

import ai4.master.project.KeyWordDatabase;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;

public class PartIndicatorEntry {
	private StringProperty partIndicator;

	public PartIndicatorEntry(String partIndicator, ObservableList<PartIndicatorEntry> parent,
			final KeyWordDatabase kwdb) {
		this.partIndicator = new SimpleStringProperty(partIndicator);

		this.partIndicator.addListener((b, o, n) -> {
			if (n == null || n.length() == 0) {
				parent.remove(this);
				kwdb.getPartIndicators().remove(partIndicator);
			} else {
				kwdb.getPartIndicators().set(kwdb.getPartIndicators().indexOf(o), n);
			}
		});

		parent.add(this);
	}

	public String getPartIndicator() {
		return partIndicator.get();
	}

	public void setPartIndicator(String partIndicator) {
		this.partIndicator.set(partIndicator);
	}

	public StringProperty partIndicatorProperty() {
		return partIndicator;
	}
}