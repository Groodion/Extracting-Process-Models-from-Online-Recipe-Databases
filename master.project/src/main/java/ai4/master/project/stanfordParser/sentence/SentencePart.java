package ai4.master.project.stanfordParser.sentence;

import java.util.ArrayList;
import java.util.List;

import ai4.master.project.KeyWordDatabase;
import ai4.master.project.recipe.baseObject.BaseCookingAction;
import ai4.master.project.recipe.baseObject.BaseIngredient;
import ai4.master.project.recipe.baseObject.BaseTool;
import ai4.master.project.recipe.object.Ingredient;
import ai4.master.project.recipe.object.Tool;
import ai4.master.project.stanfordParser.STTSTag;

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

		for (int i = 0; i < words.size(); i++) {
			if (i != 0 && !(words.get(i) instanceof PunctuationMark)) {
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
		for (Word word : words) {
			if (!(word instanceof PunctuationMark)) {
				switch (word.getPos()) {
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
		}
		return false;
	}

	public void init(KeyWordDatabase kwdb) {
		for (Word word : words) {
			word.init(kwdb);
		}
		for (Word word : words) {
			word.deepBlockGeneration(kwdb);
		}

	}

	public void mergeWith(SentencePart sentencePart) {
		words.get(words.size() - 1).setNext(sentencePart.words.get(0));
		for (; !sentencePart.words.isEmpty();)
			sentencePart.words.get(0).setSentencePart(this);
		getSentence().getParts().remove(sentencePart);
	}

	/**
	 * string * _KON ~INGREDIENT #TYPE
	 * @param reg
	 * @return
	 */
	private String[] combinations;
	
	private String[] textComb(boolean ignorePunctuationMarks) {
		if(combinations == null) {
			List<StringBuilder> combinations = new ArrayList<StringBuilder>();
			
			combinations.add(new StringBuilder());
			
			for(int i = 0; i < words.size(); i++) {
				Word word = words.get(i);
				if(word instanceof PunctuationMark) {
					if(ignorePunctuationMarks) {
						continue;
					} else {
						for(StringBuilder sB : combinations) {
							sB.append(word.getText());
						}
					}
				} else {
					List<StringBuilder> nCombs = new ArrayList<StringBuilder>();
					
					for(StringBuilder sB : combinations) {
						nCombs.add(combine(sB, word.getText().toLowerCase()));
						nCombs.add(combine(sB, word.getPos()));
						if(word.getRole() != null) {
							nCombs.add(combine(sB, word.getRole()));
						}
					}
						
					combinations = nCombs;
				}
			}
			String[] c = new String[combinations.size()];
			for(int i = 0; i < c.length; i++) {
				c[i] = combinations.get(i).toString().trim();
			}
			this.combinations = c;
		}
		
		return combinations;
	}
	
	
	public boolean matches(String reg, boolean ignorePunctuationMarks) {
		String[] combinations = textComb(ignorePunctuationMarks);
		
		for(String combination : combinations) {
			if(combination.matches(reg)) {
				return true;
			}
		}
		
		return false;
	}
	public void clearMemory() {
		this.combinations = null;
	}
	
	public List<Object> identify(String reg) {
		for(String combination : combinations) {
			if(combination.matches(reg)) {
				List<Object> objects = new ArrayList<Object>();
				
				
				
				return objects;
			}
		}
		
		return null;
	}
	
	private static StringBuilder combine(StringBuilder sB, Object s) {
		StringBuilder nSB = new StringBuilder(sB);
		
		nSB.append(' ');
		
		if(s instanceof STTSTag) {
			nSB.append('_');
		} else if(s instanceof Role) {
			nSB.append('~');
		} else if(s instanceof BlockRole) {
			nSB.append('#');
		}
		
		nSB.append(s);
		
		return nSB;
	}
	public BaseCookingAction getCookingAction() {
		for(Word word : words) {
			if(word.getRole() == Role.ACTION) {
				return word.getCookingAction();
			}
		}
		return null;
	}
	public List<Tool> getTools() {
		List<Tool> tools = new ArrayList<Tool>();
		
		for(Word word : words) {
			for(BaseTool bTool : word.getTools()) {
				tools.add(new Tool(word.getText(), bTool));				
			}
		}
		
		return tools;
	}
	public List<Ingredient> getIngredients() {
		List<Ingredient> ingredients = new ArrayList<Ingredient>();
		
		for(Word word : words) {
			for(BaseIngredient bIngredient : word.getIngredients()) {
				ingredients.add(new Ingredient(word.getText(), bIngredient));				
			}
		}
		
		return ingredients;
	}
	public Word getMainVerb() {
		for(Word word : words) {
			if(word.getRole() == Role.ACTION) {
				return word;
			}
		}
		
		return null;
	}
	
	public boolean containsLastSentenceProductReference() {
		for(Word word : words) {
			if(word.isLastProductReference()) {
				return true;
			}
		}
		
		return false;
	}
}