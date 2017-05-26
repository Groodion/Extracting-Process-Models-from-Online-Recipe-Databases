package ai4.master.project.stanfordParser.sentence;

import java.util.ArrayList;
import java.util.List;

import ai4.master.project.KeyWordDatabase;


public class Sentence extends PartialObject<Sentence> {
	
	private List<SentencePart> parts;
	
	
	public Sentence() {
		this(null);
	}
	public Sentence(Sentence prev) {
		super(prev);
		
		parts = new ArrayList<SentencePart>();
	}
	
	@Override
	public String getText() {
		StringBuilder sB = new StringBuilder();
		
		for(int i = 0; i < parts.size(); i++) {
			if(i != 0) {
				sB.append(' ');
			}
			sB.append(parts.get(i).getText());
		}
		return sB.toString();
	}

	public List<SentencePart> getParts() {
		return parts;
	}
	
	public void init(KeyWordDatabase kwdb) {
		for(SentencePart part : parts) {
			part.init(kwdb);
		}
	}
}