package ai4.master.project.stanfordParser.sentence;

import java.util.ArrayList;
import java.util.List;

public class Block extends PartialObject<Block> {

	private List<Word> words;

	private SentencePart sentencePart;
	private BlockRole role;
	private Word descriptionTarget;
	
	private boolean subject = false;
	private boolean object = false;
	
	
	public Block(SentencePart sentencePart) {
		this.sentencePart = sentencePart;
		
		this.sentencePart.getBlocks().add(this);
		
		words = new ArrayList<Word>();
	}
	
	public SentencePart getSentencePart() {
		return sentencePart;
	}
	@Override
	public String getText() {
		StringBuilder sB = new StringBuilder();
		
		for(int i = 0; i < words.size(); i++) {
			if(i != 0) {
				sB.append(' ');
			}
			sB.append(words.get(i).getText());
		}
		
		return sB.toString();
	}

	public List<Word> getWords() {
		return words;
	}

	public void setRole(BlockRole role) {
		this.role = role;
	}
	public BlockRole getRole() {
		return role;
	}

	/**
	 * Wenn der Block eine Beschreibung ist, dann ist in diesem Feld das Beschreibungsziel gespeichert. 
	 * Ansonsten null
	 * @return
	 */
	public Word getDescriptionTarget() {
		return descriptionTarget;
	}
	public void setDescriptionTarget(Word descriptionTarget) {
		this.descriptionTarget = descriptionTarget;
	}

	/**
	 * Gibt an ob der Block das Subjekt des Satzes ist
	 * @return
	 */
	public boolean isSubject() {
		return subject;
	}
	public void setSubject(boolean subject) {
		this.subject = subject;
	}

	/**
	 * Gibt an ob der Block das Objekt des Satzes ist
	 * @return
	 */
	public boolean isObject() {
		return object;
	}
	public void setObject(boolean object) {
		this.object = object;
	}

	/**
	 * Gibt das erste Wort des Blocks zurück
	 * @return
	 */
	public Word getFirstWord() {
		return words.get(0);
	}
	/**
	 * Gibt das letzte Wort des Blocks zurück
	 * @return
	 */
	public Word getLastWord() {
		return words.get(words.size() - 1);
	}
	
	/**
	 * Gibt das erste Wort vor dem Block zurück
	 * @return
	 */
	public Word getPrevWord() {
		return getFirstWord().getPrev();
	}
	/**
	 * Gibt das erste Wort nach dem Block zurück
	 * @return
	 */
	public Word getNextWord() {
		return getLastWord().getNext();
	}
	
	@Override
	public String toString() {
		return "Block [role=" + role + ", size: " + words.size() + "]";
	}

	
}