package ai4.master.project.view;

import javafx.beans.property.SimpleStringProperty;

public class EventIndicatorEntry {
	private final SimpleStringProperty eventIndicator;
	
	public EventIndicatorEntry(String eventIndicator) {
		this.eventIndicator = new SimpleStringProperty(eventIndicator);
	}
	
	public String getEventIndicator() {
		return eventIndicator.get();
	}
	
	public void setEventIndicator(String eventIndicator) {
		this.eventIndicator.set(eventIndicator);
	}

}
