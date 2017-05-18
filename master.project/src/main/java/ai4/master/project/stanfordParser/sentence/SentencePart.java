package ai4.master.project.stanfordParser.sentence;

import java.util.ArrayList;
import java.util.List;

import ai4.master.project.KeyWordDatabase;

public class SentencePart extends PartialObject<SentencePart> {

	private List<Word> words;
	private List<Block> blocks;
	
	private Sentence sentence;
	
	
	public SentencePart(Sentence sentence) {
		this(null, sentence);
	}
	public SentencePart(SentencePart prev, Sentence sentence) {
		super(prev);
		
		this.sentence = sentence;
		words = new ArrayList<Word>();
		blocks = new ArrayList<Block>();
		
		sentence.getParts().add(this);
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
	public List<Block> getBlocks() {
		return blocks;
	}
	public Sentence getSentence() {
		return sentence;
	}

	public boolean containsVerb() {
		for(Word word : words) {
			switch(word.getPos()) {
			case VAFIN:
			case VAIMP:
			case VAINF:
			case VAPP:
			case VMFIN:
			case VMINF:
			case VMPP:
			case VVFIN:
			case VVIMP:
			case VVINF:
			case VVIZU:
			case VVPP:
				return true;
			default:
				break;
			}
		}
		return false;
	}
	
	public void init(KeyWordDatabase kwdb) {
		for(Word word : words) {
			word.init(kwdb);
		}
	}
	public void mergeWith(SentencePart sentencePart) {
		words.get(words.size() - 1).setNext(sentencePart.words.get(0));
		for(;!sentencePart.words.isEmpty();) sentencePart.words.get(0).setSentencePart(this);
	}	
}