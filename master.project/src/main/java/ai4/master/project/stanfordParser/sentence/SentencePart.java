package ai4.master.project.stanfordParser.sentence;

import ai4.master.project.KeyWordDatabase;
import ai4.master.project.recipe.CookingEvent;
import ai4.master.project.recipe.EventType;
import ai4.master.project.recipe.Position;
import ai4.master.project.recipe.baseObject.BaseCookingAction;
import ai4.master.project.recipe.baseObject.BaseIngredient;
import ai4.master.project.recipe.baseObject.BaseIngredientGroup;
import ai4.master.project.recipe.baseObject.BaseTool;
import ai4.master.project.recipe.object.Ingredient;
import ai4.master.project.recipe.object.IngredientGroup;
import ai4.master.project.recipe.object.Tool;
import ai4.master.project.stanfordParser.STTSTag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

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

	/**
	 * Gibt zurï¿½ck ob der Satzteil ein Verb beinhaltet
	 * @return
	 */
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

	/**
	 * Fï¿½gt zwei Satzteile zu einem zusammen
	 * @return
	 */
	public void mergeWith(SentencePart sentencePart) {
		if(!getWords().isEmpty() && !sentencePart.getWords().isEmpty()) {
			if(words.get(words.size() - 1) instanceof PunctuationMark && words.size() > 1) {
				words.get(words.size() - 2).setNext(sentencePart.words.get(0));	
			} else {
				words.get(words.size() - 1).setNext(sentencePart.words.get(0));
			}
		}
	
		
		for (; !sentencePart.words.isEmpty();)
			sentencePart.words.get(0).setSentencePart(this);
		getSentence().getParts().remove(sentencePart);
	}

	
	private String[] combinations;
	private String[] textComb(boolean ignorePunctuationMarks, Set<String> tags) {
		if(getText().contains("Brötchen")) System.out.println(tags);
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
						if(tags.contains('_' + word.getPos().toString())) {
							nCombs.add(combine(sB, word.getPos()));
						}
						if(word.getRole() != null && tags.contains('~' + word.getRole().toString())) {
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
		
		System.out.println(Arrays.toString(combinations));
		
		return combinations;
	}
	
	/**
	 * Testet ob der Satzteil einem regulï¿½ren Ausdruck entspricht.
	 * @return
	 */
	public boolean matches(String reg, boolean ignorePunctuationMarks, Set<String> usedTags) {
		String[] combinations = textComb(ignorePunctuationMarks, usedTags);
				
		for(String combination : combinations) {
			if(combination.matches(reg)) {
				System.out.println(reg + " -> " + combination);
				return true;
			}
		}
		
		return false;
	}
	/**
	 * Lï¿½scht die gespeicherten Textcombinationen des Ausdruckstesters, um Arbeitsspeicher frei zu machen.
	 * @return
	 */
	public void clearMemory() {
		this.combinations = null;
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
				if(word.getCookingAction() != null) {				
					return word.getCookingAction();
				}
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
				if(bIngredient instanceof BaseIngredientGroup) {
					ingredients.add(new IngredientGroup(word.getText(), (BaseIngredientGroup) bIngredient));
				} else {
					ingredients.add(new Ingredient(word.getText(), bIngredient));
				}
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
	
	public List<CookingEvent> getEvents() {
		List<CookingEvent> events = new ArrayList<CookingEvent>();
		
		for(Block block : blocks) {
			if(block.getRole() == BlockRole.CONDITION) {
				events.add(new CookingEvent(block.getText(), EventType.TIMER, Position.BEFORE));
			}
		}
		
		return events;
	}
	
	public boolean containsLastSentenceProductReference() {
		for(Word word : words) {
			if(word.isLastProductReference()) {
				return true;
			}
		}
		
		return false;
	}
	public boolean containsWord(String string) {
		for(Word word : getWords()) {
			if(Word.stem(word.getText().toLowerCase()).equals(Word.stem(string.toLowerCase()))) {
				return true;
			}
		}
		return false;
	}
	public boolean containsEventIndicator() {
		for(Word word : words) {
			if(word.isEventIndicator()) {
				return true;
			}
		}
		return false;
	}
}