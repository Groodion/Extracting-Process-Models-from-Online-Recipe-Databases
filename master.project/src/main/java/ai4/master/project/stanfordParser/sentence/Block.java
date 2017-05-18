package ai4.master.project.stanfordParser.sentence;

import java.util.ArrayList;
import java.util.List;

public class Block extends PartialObject<Block> {

	private List<Word> words;

	private SentencePart sentencePart;
	private BlockRole role;
	private Word descriptionTarget;
	
	
	public Block(SentencePart sentencePart) {
		this.sentencePart = sentencePart;
		
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

	public Word getDescriptionTarget() {
		return descriptionTarget;
	}
	public void setDescriptionTarget(Word descriptionTarget) {
		this.descriptionTarget = descriptionTarget;
	}


	@Override
	public String toString() {
		return "Block [role=" + role + ", size: " + words.size() + "]";
	}

	
}