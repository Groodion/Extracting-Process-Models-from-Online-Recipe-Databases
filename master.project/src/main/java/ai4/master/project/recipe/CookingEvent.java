package ai4.master.project.recipe;

public class CookingEvent {
	private EventType type;
	private String text;
	private Position pos;
	
	public CookingEvent(String text, EventType type, Position pos) {
		this.text = text;
		this.type = type;
		this.pos = pos;
	}

	public EventType getType() {
		return type;
	}
	public String getText() {
		return text;
	}
	public Position getPos() {
		return pos;
	}

	@Override
	public String toString() {
		return "CookingEvent [type=" + type + ", text=" + text + ", pos=" + pos + "]";
	}
}