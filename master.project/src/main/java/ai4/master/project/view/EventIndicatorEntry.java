package ai4.master.project.view;

import ai4.master.project.KeyWordDatabase;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;

public class EventIndicatorEntry {
	private StringProperty eventIndicator;
	
	public EventIndicatorEntry(String eventIndicator, ObservableList<EventIndicatorEntry> parent, final KeyWordDatabase kwdb) {
		this.eventIndicator = new SimpleStringProperty(eventIndicator);
		
		this.eventIndicator.addListener((b, o, n) -> {
			if (n == null || n.length() == 0) {				
					parent.remove(this);
					kwdb.getEventIndicators().remove(eventIndicator);
			} 
			else {
				kwdb.getEventIndicators().set(kwdb.getEventIndicators().indexOf(o), n);
			}
		});

		parent.add(this);
	}
	
	public String getEventIndicator() {
		return eventIndicator.get();
	}
	
	public void setEventIndicator(String eventIndicator) {
		this.eventIndicator.set(eventIndicator);
	}
	
	public StringProperty eventIndicatorProperty() {
		return eventIndicator;
	}
}