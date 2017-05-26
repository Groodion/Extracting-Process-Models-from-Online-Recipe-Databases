package ai4.master.project.stanfordParser.sentence;

import ai4.master.project.KeyWordDatabase;

public class PunctuationMark extends Word {
	public PunctuationMark(String text, SentencePart sentencePart) {
		super(text, null, sentencePart);
	}
	
	@Override
	public void init(KeyWordDatabase  kwdb) {
		
	}
}
