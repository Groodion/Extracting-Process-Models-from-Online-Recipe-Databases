package ai4.master.project.stanfordParser.sentence;

import java.util.ArrayList;
import java.util.List;

import ai4.master.project.KeyWordDatabase;
import ai4.master.project.stanfordParser.STTSTag;

public class Word extends PartialObject<Word> {
	
	private String text;
	
	private STTSTag pos;
	private List<Word> referenceTargets;
	private List<Word> connections;
	private Role role;
		
	private SentencePart sentencePart;
	
	private Block block;
	

	public Word(String text, STTSTag pos, SentencePart sentencePart) {
		this(text, pos, null, sentencePart);
	}
	public Word(String text, STTSTag pos, Word prev, SentencePart sentencePart) {
		super(prev);
		this.pos = pos;
		this.text = text;

		referenceTargets = new ArrayList<Word>();
		connections = new ArrayList<Word>();
		
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
			connections.add(getNextNoun());
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
			role = kwdb.identify(text);
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
		case VAFIN:
			break;
		case VAIMP:
			break;
		case VAINF:
			break;
		case VAPP:
			break;
		case VMFIN:
			break;
		case VMINF:
			break;
		case VMPP:
			break;
		case VVFIN:
			break;
		case VVIMP:
			break;
		case VVINF:
			role = Role.ACTION;
			break;
		case VVIZU:
			break;
		case VVPP:
			break;
		case XY:
			break;
		default:
			break;
		
		}
		System.out.println(this);
	}
	
	public void deepBlockGeneration(KeyWordDatabase kwdb) {
		
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

	
	@Override
	public String toString() {
		return "Word [text=" + text + ", pos=" + pos + ", role=" + getRole() + ", block=" + block + "]";
	}
}