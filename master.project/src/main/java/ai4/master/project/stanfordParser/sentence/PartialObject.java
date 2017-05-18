package ai4.master.project.stanfordParser.sentence;

public abstract class PartialObject<T extends PartialObject<T>> {

	private T prev;
	private T next;
	
	
	public PartialObject() {
		prev = null;
		next = null;
	}
	@SuppressWarnings("unchecked")
	public PartialObject(T prev) {
		this();
		
		if(prev != null) {
			prev.setNext((T) this);
		}
	}
	
	public abstract String getText();
	
	public T getPrev() {
		return prev;
	}
	public T getNext() {
		return next;
	}

	@SuppressWarnings("unchecked")
	public void setNext(T next) {
		if(this.next != null) {
			this.next.prev = null;
		}
		
		this.next = next;

		if(this.next != null) {
			if(this.next.prev != null) {
				this.next.prev.next = null;
			}
			
			this.next.prev = (T) this;
		}
	}
}