package ai4.master.project.viewFx.components.editorViews.entries;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;

public class EventIndicatorEntry {
	private StringProperty eventIndicator;

	public EventIndicatorEntry(String eventIndicator, ObservableList<String> eventIndicators) {
		this.eventIndicator = new SimpleStringProperty(eventIndicator);

		this.eventIndicator.addListener((b, o, n) -> {
			if (n == null || n.length() == 0) {
				eventIndicators.remove(this);
			} else {
				eventIndicators.set(eventIndicators.indexOf(o), n);
			}
		});
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