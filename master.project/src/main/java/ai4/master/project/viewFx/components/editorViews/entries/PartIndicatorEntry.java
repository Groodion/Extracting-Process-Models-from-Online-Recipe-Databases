package ai4.master.project.viewFx.components.editorViews.entries;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;

public class PartIndicatorEntry {
	private StringProperty partIndicator;

	public PartIndicatorEntry(String partIndicator, ObservableList<String> partIndicators) {
		this.partIndicator = new SimpleStringProperty(partIndicator);

		this.partIndicator.addListener((b, o, n) -> {
			if (n == null || n.length() == 0) {
				partIndicators.remove(this);
			} else {
				partIndicators.set(partIndicators.indexOf(o), n);
			}
		});
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