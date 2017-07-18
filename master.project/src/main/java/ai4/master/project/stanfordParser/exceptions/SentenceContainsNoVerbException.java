package ai4.master.project.stanfordParser.exceptions;

import ai4.master.project.stanfordParser.sentence.Sentence;

public class SentenceContainsNoVerbException extends Exception {
	private static final long serialVersionUID = 1L;

	private Sentence sentence;
	
	public SentenceContainsNoVerbException(Sentence sentence) {
		super("The Sentence '" + sentence.getText() + "' contains no verb.");
		
		this.sentence = sentence;
	}

	public Sentence getSentence() {
		return sentence;
	}
}
