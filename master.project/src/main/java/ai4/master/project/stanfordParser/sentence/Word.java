package ai4.master.project.stanfordParser.sentence;

import ai4.master.project.KeyWordDatabase;
import ai4.master.project.recipe.baseObject.BaseCookingAction;
import ai4.master.project.recipe.baseObject.BaseIngredient;
import ai4.master.project.recipe.baseObject.BaseTool;
import ai4.master.project.stanfordParser.STTSTag;
import org.tartarus.snowball.ext.German2Stemmer;

import java.util.ArrayList;
import java.util.List;


public class Word extends PartialObject<Word> {
	
	private static final German2Stemmer STEMMER = new German2Stemmer();
	
	public static String stem(String word) {
		STEMMER.setCurrent(word);
		STEMMER.stem();
		return STEMMER.getCurrent();
	}
	
	
	private String text;
	
	private STTSTag pos;
	private List<Word> referenceTargets;
	private List<Word> connections;
	private Role role;
		
	private SentencePart sentencePart;
	
	private Block block;
	
	private boolean isVerb = false;
	private boolean lastProductReference = false;
	private boolean conditionIndicator = false;
	
	private BaseCookingAction cookingAction;
	private List<BaseTool> tools;
	private List<BaseIngredient> ingredients;
	
	
	public Word(String text, STTSTag pos, SentencePart sentencePart) {
		this(text, pos, null, sentencePart);
	}
	public Word(String text, STTSTag pos, Word prev, SentencePart sentencePart) {
		super(prev);
		this.pos = pos;
		this.text = text;

		referenceTargets = new ArrayList<Word>();
		connections = new ArrayList<Word>();
		
		tools = new ArrayList<BaseTool>();
		ingredients = new ArrayList<BaseIngredient>();
		
		setSentencePart(sentencePart);
	}

	@Override 
	public String getText() {
		return text;
	}
	
	public STTSTag getPos() {
		return pos;
	}
	public List<Word> getReferenceTargets() {
		return referenceTargets;
	}
	public List<Word> getConnections() {
		return connections;
	}
	public Role getRole() {
		while(connections.remove(null));
		while(referenceTargets.remove(null));
		if(!referenceTargets.isEmpty()) {
			Role role = referenceTargets.get(0).getRole();
			
			for(Word word : referenceTargets) {
				switch(role) {
				case ACTION:
				case CONDITION:
					if(role != word.role) {
						role = null;
					}
					break;
				case INGREDIENT:
					switch(word.role) {
					case ACTION:
					case CONDITION:
					case TOOL:
						role = null;
						break;
					case INGREDIENT:
					case UNDECIDABLE_OBJECT:
						role = Role.INGREDIENT;
						break;
					default:
						break;
					}
					break;
				case TOOL:
					switch(word.role) {
					case UNDECIDABLE_OBJECT:
					case TOOL:
						role = Role.INGREDIENT;
						break;
					case ACTION:
					case CONDITION:
					case INGREDIENT:
						role = null;
						break;
					default:
						break;
					}
					break;
				case UNDECIDABLE_OBJECT:
					switch(word.role) {
					case ACTION:
					case CONDITION:
						role = null;
						break;
					case TOOL:
						role = Role.TOOL;
						break;
					case INGREDIENT:
						role = Role.INGREDIENT;
						break;
					case UNDECIDABLE_OBJECT:
						role = Role.UNDECIDABLE_OBJECT;
						break;
					default:
						break;
					}
					break;
				default:
					break;
				}
			}
			
			return role;
		} else if(!connections.isEmpty()) {
			Role role = connections.get(0).getRole();
			
			for(Word word : connections) {
				if(role == null) return null;
				
				switch(role) {
				case ACTION:
				case CONDITION:
					if(role != word.role) {
						role = null;
					}
					break;
				case INGREDIENT:
					switch(word.role) {
					case ACTION:
					case CONDITION:
					case TOOL:
						role = null;
						break;
					case INGREDIENT:
					case UNDECIDABLE_OBJECT:
						role = Role.INGREDIENT;
						break;
					default:
						break;
					}
					break;
				case TOOL:
					switch(word.role) {
					case UNDECIDABLE_OBJECT:
					case TOOL:
						role = Role.INGREDIENT;
						break;
					case ACTION:
					case CONDITION:
					case INGREDIENT:
						role = null;
						break;
					default:
						break;
					}
					break;
				case UNDECIDABLE_OBJECT:
					switch(word.role) {
					case ACTION:
					case CONDITION:
						role = null;
						break;
					case TOOL:
						role = Role.TOOL;
						break;
					case INGREDIENT:
						role = Role.INGREDIENT;
						break;
					case UNDECIDABLE_OBJECT:
						role = Role.UNDECIDABLE_OBJECT;
						break;
					default:
						break;
					}
					break;
				default:
					break;
				}
			}
			
			return role;
		} else {		
			return role;
		}
	}
	public Block getBlock() {
		return block;
	}
	private void setBlock(Block block) {
		if(this.block != null) {
			this.block.getWords().remove(this);
		}
		
		this.block = block;
		
		if(this.block != null) {
			this.block.getWords().add(this);
		}
	}
	
	public boolean isVerb() {
		return isVerb;
	}
	public boolean isLastProductReference() {
		return lastProductReference;
	}
	public boolean isConditionIndicator() {
		return conditionIndicator;
	}
	
	public SentencePart getSentencePart() {
		return sentencePart;
	}
	public void setSentencePart(SentencePart sentencePart) {
		if(this.sentencePart != null) {
			this.sentencePart.getWords().remove(this);
		}
		
		this.sentencePart = sentencePart;
		
		if(this.sentencePart != null) {
			this.sentencePart.getWords().add(this);
		}
	}

	public void init(KeyWordDatabase kwdb) {
		conditionIndicator = kwdb.isConditionIndicator(getText());
		
		if(kwdb.isLastSentenceRefernece(getText())) {
			lastProductReference = true;
		}
		
		switch(pos) {
		case ADJA:
			connections.add(getNextNoun());
			break;
		case ADJD:
			break;
		case ADV:
			break;
		case APPO:
			break;
		case APPR:
			break;
		case APPRART:
			break;
		case APZR:
			break;
		case ART:
			Word noun = getNextNoun();
			connections.add(noun);
			break;
		case CARD:
			break;
		case FM:
			break;
		case ITJ:
			break;
		case KOKOM:
			break;
		case KON:
			break;
		case KOUI:
			break;
		case KOUS:
			Block conditionBlock = new Block(sentencePart);
			
			for(Word word = this; word != null; word = word.getNext()) {
				word.setBlock(conditionBlock);
				conditionBlock.setRole(BlockRole.CONDITION);
				
				if(word.pos == STTSTag.VAFIN) {
					break;
				}
			}
			break;
		case NE:
		case NN:
			role = kwdb.identify(stem(text));
			if(role == Role.TOOL) {
				tools.add(kwdb.findTool(stem(getText())));
			} else if(role == Role.INGREDIENT) {
				ingredients.add(kwdb.findIngredient(stem(getText())));
			}
			break;
		case PDAT:
			break;
		case PDS:
			break;
		case PIAT:
			break;
		case PIS:
			break;
		case PPER:
			getReferenceTargets().add(getLastIngredient());
			if(getLastIngredient() == null) {
			} else {
				getIngredients().addAll(getLastIngredient().getIngredients());
			}
			break;
		case PPOSAT:
			break;
		case PPOSS:
			break;
		case PRELAT:
			break;
		case PRELS:
			Block descriptionBlock = new Block(sentencePart);
			
			for(Word word = this; word != null; word = word.getNext()) {
				word.setBlock(descriptionBlock);
				descriptionBlock.setRole(BlockRole.DESCRIPTION);
				
				if(word.pos == STTSTag.VAFIN) {
					break;
				}
			}
			
			for(Word word = this; word != null; word = word.getPrev()) {
				if(word.pos == STTSTag.NN) {
					descriptionBlock.setDescriptionTarget(word);
					break;
				}
			}
			
			break;
		case PRF:
			break;
		case PROAV:
			break;
		case PTKA:
			break;
		case PTKANT:
			break;
		case PTKNEG:
			break;
		case PTKVZ:
			break;
		case PTKZU:
			break;
		case PWAT:
			break;
		case PWAV:
			break;
		case PWS:
			break;
		case TRUNC:
			break;
		case VVINF:
		case VVFIN:
		case VVIMP:
		case VVIZU:
			role = Role.ACTION;
			cookingAction = kwdb.findCookingAction(stem(getText()));
		case VAFIN:
		case VAIMP:
		case VAINF:
		case VAPP:
		case VMFIN:
		case VMINF:
		case VMPP:
		case VVPP:
			isVerb = true;
			//System.out.println(getText() + " " + getPos());
			break;
		case XY:
			break;
		default:
			break;
		}
	}
	public void lexConnectTo(Word word) {
		if(word.isVerb()) {
			if(getBlock().getRole() == BlockRole.INGREDIENT_TOOL_COLLECTION) {
				getBlock().setSubject(true);
			}
		}
	}
	
	public BaseCookingAction getCookingAction() {
		return cookingAction;
	}

	public void deepBlockGeneration(KeyWordDatabase kwdb) {
		if(getPos() == STTSTag.KON) {
			if(getNext() != null && getPrev() != null && ((getNext().getRole() == Role.INGREDIENT || getNext().getRole() == Role.UNDECIDABLE_OBJECT) && (getPrev().getRole() == Role.INGREDIENT || getPrev().getRole() == Role.UNDECIDABLE_OBJECT))) {
				Block collectionBlock = new Block(sentencePart);
				collectionBlock.setRole(BlockRole.INGREDIENT_TOOL_COLLECTION);
				
				Word lastIngredient = getNext().getConnections().size() == 0 ? getNext() : getNext().getConnections().get(0);
								
				Word sWord = null;
				for(Word word = getPrev(); word != null && (word instanceof PunctuationMark || word.getPos() == STTSTag.KON || word.getRole() == Role.INGREDIENT || word.getRole() == Role.UNDECIDABLE_OBJECT); word = word.getPrev()) {
					sWord = word;
				}
				for(Word word = sWord; word != lastIngredient.getNext(); word = word.getNext()) {
					if(word.getRole() == Role.INGREDIENT) {
						collectionBlock.setRole(BlockRole.INGREDIENT_COLLECTION);
					}
					word.setBlock(collectionBlock);
				}
				
				for(Word word : collectionBlock.getWords()) {
					word.select(Role.INGREDIENT, kwdb);
				}
			}
		} else if(block == null && conditionIndicator) {			
			Word start = this;
			Word end = this;
						
			for(; start.getPrev() != null && start.getPrev().getRole() == null && start.getPrev().getBlock() == null; start = start.getPrev());
			for(; end.getNext() != null && end.getNext().getRole() == null && end.getNext().getBlock() == null; end = end.getNext());
			
			Block conditionBlock = new Block(sentencePart);
			
			for(Word word = start; word != end.getNext(); word = word.getNext()) {
				word.setBlock(conditionBlock);
			}
			
			conditionBlock.setRole(BlockRole.CONDITION);
		}
	}
	
	private void select(Role role, KeyWordDatabase kwdb) {
		if(this.role == Role.UNDECIDABLE_OBJECT) {
			this.role = role;
			
			ingredients.add(kwdb.findIngredient(getText()));
		}
	}
	public List<Word> getLastSubjectWords() {
		List<Word> subjectWords = new ArrayList<Word>();
		
		return subjectWords;
	}
	public Block getLastSubjectBlock() {
		return null;
	}
	public Word getNextNoun() {
		for(Word word = getNext(); word != null; word = word.getNext()) {
			if(word.pos == STTSTag.NN || word.pos == STTSTag.NE) {
				return word;
			}
		}
		return null;
	}
	public Word getLastIngredient() {
		for(Word word = getPrev(); word != null; word = word.getPrev()) {
			if(word.role == Role.INGREDIENT) {
				System.out.println("ref: " + word);
				return word;
			}
		}
		return null;
	}

	public List<BaseTool> getTools() {
		return tools;
	}
	public List<BaseIngredient> getIngredients() {
		return ingredients;
	}
	
	@Override
	public String toString() {
		return "Word [text=" + text + ", pos=" + pos + ", role=" + getRole() + ", block=" + block + "]";
	}
}