package ai4.master.project.recipe;

public class CookingEvent {
	private EventType type;
	private String text;
	
	public CookingEvent(String text, EventType type) {
		this.text = text;
		this.type = type;
	}

	public EventType getType() {
		return type;
	}
	public String getText() {
		return text;
	}
}
