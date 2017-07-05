package ai4.master.project.view;

import javafx.beans.property.SimpleStringProperty;

public class PartIndicatorEntry {
private final SimpleStringProperty partIndicator;
	
	public PartIndicatorEntry(String partIndicator) {
		this.partIndicator = new SimpleStringProperty(partIndicator);
	}
	
	public String getPartIndicator() {
		return partIndicator.get();
	}
	
	public void setPartIndicator(String partIndicator) {
		this.partIndicator.set(partIndicator);
	}

}
